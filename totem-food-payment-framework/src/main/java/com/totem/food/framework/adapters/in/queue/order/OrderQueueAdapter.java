package com.totem.food.framework.adapters.in.queue.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.totem.food.application.exceptions.ElementNotFoundException;
import com.totem.food.application.ports.in.dtos.context.XUserIdentifierContextDto;
import com.totem.food.application.ports.in.dtos.event.AWSMessage;
import com.totem.food.application.ports.in.dtos.event.PaymentNotificationDto;
import com.totem.food.application.ports.in.dtos.payment.PaymentCreateDto;
import com.totem.food.application.ports.in.dtos.payment.PaymentDto;
import com.totem.food.application.ports.in.dtos.payment.PaymentQRCodeDto;
import com.totem.food.application.ports.in.event.IReceiveEventPort;
import com.totem.food.application.ports.out.email.EmailNotificationDto;
import com.totem.food.application.ports.out.event.ISendEventPort;
import com.totem.food.application.ports.out.internal.customer.CustomerFilterRequest;
import com.totem.food.application.ports.out.internal.customer.CustomerResponse;
import com.totem.food.application.ports.out.persistence.commons.IUpdateRepositoryPort;
import com.totem.food.application.ports.out.persistence.payment.PaymentEmailUpdateModel;
import com.totem.food.application.ports.out.persistence.payment.PaymentModel;
import com.totem.food.application.ports.out.web.ISendRequestPort;
import com.totem.food.application.usecases.commons.IContextUseCase;
import com.totem.food.application.usecases.commons.ICreateImageUseCase;
import com.totem.food.application.usecases.commons.ICreateUseCase;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.listener.acknowledgement.Acknowledgement;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.Optional;

@AllArgsConstructor
@Slf4j
@Component
public class OrderQueueAdapter implements IReceiveEventPort<Message<AWSMessage>, Void> {

    private final ICreateUseCase<PaymentCreateDto, PaymentQRCodeDto> iCreateUseCase;
    private final IContextUseCase<XUserIdentifierContextDto, String> iContextUseCase;
    private final ISendEventPort<EmailNotificationDto, Boolean> sendEmailEventPort;
    private final ISendRequestPort<CustomerFilterRequest, Optional<CustomerResponse>> iSearchUniqueCustomerRepositoryPort;
    private final ICreateImageUseCase<PaymentDto, byte[]> iCreateImageUseCase;
    private final IUpdateRepositoryPort<PaymentModel> iUpdateRepositoryPort;
    private final ObjectMapper objectMapper;
    private final IUpdateRepositoryPort<PaymentEmailUpdateModel> updatePaymentEmailRepositoryPort;

    @SqsListener(value = "${ms.internal.queue.payment_create}")
    @Override
    public Void receiveMessage(Message<AWSMessage> message) {
        try {
            var paymentEventMessageDto = objectMapper.readValue(message.getPayload().getMessage(), PaymentNotificationDto.class);

            iContextUseCase.setContext(new XUserIdentifierContextDto(paymentEventMessageDto.getOrder().getCustomer()));

            Optional.ofNullable(paymentEventMessageDto.getOrder().getCustomer())
                    .filter(StringUtils::isNotEmpty)
                    .ifPresent(customer -> {
                        final var customerModel = iSearchUniqueCustomerRepositoryPort.sendRequest(CustomerFilterRequest.builder()
                                        .customer(customer)
                                        .build())
                                .orElseThrow(() -> new ElementNotFoundException(String.format("Customer [%s] not found", customer)));

                        final var payment = iCreateUseCase.createItem(
                                new PaymentCreateDto(
                                        paymentEventMessageDto.getOrder().getId(),
                                        paymentEventMessageDto.getOrder().getCustomer()
                                )
                        );

                        byte[] imageBytes = iCreateImageUseCase.createImage(PaymentDto.builder()
                                .qrcodeBase64(payment.getQrcodeBase64())
                                .build()
                        );
                        final var imageString = new String(Base64.encodeBase64(imageBytes));

                        if(!payment.isEmailSentBefore()) {
                            var emailNotificationDto = getEmailNotificationDto(
                                    customerModel,
                                    imageString,
                                    payment.getQrcodeBase64(),
                                    paymentEventMessageDto.getOrder().getId(),
                                    paymentEventMessageDto.getOrder().getPrice()
                            );
                            sendEmailEventPort.sendMessage(emailNotificationDto);
                            updatePaymentEmailRepositoryPort.updateItem(new PaymentEmailUpdateModel(payment.getPaymentId(), 1));
                        }
                    });
            Acknowledgement.acknowledge(message);
        } catch (JsonProcessingException e) {
            log.error("Error to processing Event with body {}", message);
        }
        return null;
    }

    private static EmailNotificationDto getEmailNotificationDto(CustomerResponse customerModel, String imageString, String qrCodeBase64, String orderId, double price) {
        var emailNotificationDto = new EmailNotificationDto();
        emailNotificationDto.setEmail(customerModel.getEmail());
        final var image = "<img src=\"data:image/jpeg;base64,".concat(imageString).concat("\">");
        final var message = String.format(
                "Pagamento no valor de <b>R$ %.2f </b>! </br> Formas de pagamento! </br> Código pix: <b>%s</b> </br> QRCode abaixo! </br> %s",
                price,
                qrCodeBase64,
                image
        );
        emailNotificationDto.setMessage(message);
        emailNotificationDto.setSubject(String.format("[%s] Pagamento do Pedido %s", "Totem Food Service", orderId));
        return emailNotificationDto;
    }
}
