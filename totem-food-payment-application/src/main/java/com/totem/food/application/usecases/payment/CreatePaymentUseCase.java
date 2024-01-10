package com.totem.food.application.usecases.payment;

import com.totem.food.application.exceptions.ElementNotFoundException;
import com.totem.food.application.ports.in.dtos.payment.PaymentCreateDto;
import com.totem.food.application.ports.in.dtos.payment.PaymentQRCodeDto;
import com.totem.food.application.ports.in.mappers.payment.IPaymentMapper;
import com.totem.food.application.ports.out.internal.customer.CustomerFilterRequest;
import com.totem.food.application.ports.out.internal.customer.CustomerResponseRequest;
import com.totem.food.application.ports.out.internal.order.OrderFilterRequest;
import com.totem.food.application.ports.out.internal.order.OrderResponseRequest;
import com.totem.food.application.ports.out.persistence.commons.ICreateRepositoryPort;
import com.totem.food.application.ports.out.persistence.commons.IUpdateRepositoryPort;
import com.totem.food.application.ports.out.persistence.payment.PaymentModel;
import com.totem.food.application.ports.out.web.ISendRequestPort;
import com.totem.food.application.usecases.annotations.UseCase;
import com.totem.food.application.usecases.commons.ICreateUseCase;
import com.totem.food.domain.exceptions.InvalidStatusException;
import com.totem.food.domain.payment.PaymentDomain;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@UseCase
public class CreatePaymentUseCase implements ICreateUseCase<PaymentCreateDto, PaymentQRCodeDto> {

    private final ICreateRepositoryPort<PaymentModel> iCreateRepositoryPort;
    private final IUpdateRepositoryPort<PaymentModel> iUpdateRepositoryPort;
    private final IPaymentMapper iPaymentMapper;
    private final ISendRequestPort<OrderFilterRequest, Optional<OrderResponseRequest>> iSearchUniqueOrderRepositoryPort;
    private final ISendRequestPort<CustomerFilterRequest, Optional<CustomerResponseRequest>> iSearchUniqueCustomerRepositoryPort;
    private final ISendRequestPort<PaymentModel, PaymentQRCodeDto> iSendRequest;

    @Override
    public PaymentQRCodeDto createItem(PaymentCreateDto item) {

        final var orderDomain = iSearchUniqueOrderRepositoryPort.sendRequest(OrderFilterRequest.builder()
                        .orderId(item.getOrderId())
                        .build())
                .orElseThrow(() -> new ElementNotFoundException(String.format("Order [%s] not found", item.getOrderId())));

        if (orderDomain.getStatus().equals("WAITING_PAYMENT")) { //@todo - refact - trocar para enum
            final var paymentDomainBuilder = PaymentDomain.builder();

            Optional.ofNullable(item.getCustomerId())
                    .filter(StringUtils::isNotEmpty)
                    .ifPresent(customerId -> {
                        final var customerModel = iSearchUniqueCustomerRepositoryPort.sendRequest(CustomerFilterRequest.builder()
                                        .customerId(customerId)
                                        .build())
                                .orElseThrow(() -> new ElementNotFoundException(String.format("Customer [%s] not found", customerId)));
                        //paymentDomainBuilder.customer(customerDomain); //@todo - refact - setar customerId
                    });


            //@todo - refact - setar orderId paymentDomainBuilder.order(domain);
            paymentDomainBuilder.price(orderDomain.getPrice());
            paymentDomainBuilder.token(UUID.randomUUID().toString());
            paymentDomainBuilder.status(PaymentDomain.PaymentStatus.PENDING);


            final var paymentDomain = paymentDomainBuilder.build();
            paymentDomain.fillDates();

            final var paymentModel = iPaymentMapper.toModel(paymentDomain);

            final var paymentDomainSaved = iCreateRepositoryPort.saveItem(paymentModel);

            final var paymentDto = iSendRequest.sendRequest(paymentDomainSaved);

            if (paymentDto.getQrcodeBase64() != null) {
                paymentDomainSaved.setQrcodeBase64(paymentDto.getQrcodeBase64());
                iUpdateRepositoryPort.updateItem(paymentDomainSaved);
            }

            paymentDto.setStatus(PaymentDomain.PaymentStatus.PENDING.key);
            paymentDto.setPaymentId(paymentDomainSaved.getId());
            return paymentDto;
        }

        throw new InvalidStatusException("Order", orderDomain.getStatus(), "WAITING_PAYMENT"); //@todo - refact - trocar para enum
    }

}