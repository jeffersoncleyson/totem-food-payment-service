package com.totem.food.application.usecases.payment;

import com.totem.food.application.enums.OrderStatusEnum;
import com.totem.food.application.exceptions.ElementNotFoundException;
import com.totem.food.application.ports.in.dtos.context.XUserIdentifierContextDto;
import com.totem.food.application.ports.in.dtos.payment.PaymentCreateDto;
import com.totem.food.application.ports.in.dtos.payment.PaymentFilterDto;
import com.totem.food.application.ports.in.dtos.payment.PaymentQRCodeDto;
import com.totem.food.application.ports.in.mappers.payment.IPaymentMapper;
import com.totem.food.application.ports.out.internal.customer.CustomerFilterRequest;
import com.totem.food.application.ports.out.internal.customer.CustomerResponse;
import com.totem.food.application.ports.out.internal.order.OrderFilterRequest;
import com.totem.food.application.ports.out.internal.order.OrderResponseRequest;
import com.totem.food.application.ports.out.persistence.commons.ICreateRepositoryPort;
import com.totem.food.application.ports.out.persistence.commons.ISearchRepositoryPort;
import com.totem.food.application.ports.out.persistence.commons.IUpdateRepositoryPort;
import com.totem.food.application.ports.out.persistence.payment.PaymentModel;
import com.totem.food.application.ports.out.web.ISendRequestPort;
import com.totem.food.application.usecases.annotations.UseCase;
import com.totem.food.application.usecases.commons.IContextUseCase;
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
    private final ISendRequestPort<CustomerFilterRequest, Optional<CustomerResponse>> iSearchUniqueCustomerRepositoryPort;
    private final ISendRequestPort<PaymentModel, PaymentQRCodeDto> iSendRequest;
    private final IContextUseCase<XUserIdentifierContextDto, String> iContextUseCase;
    private final ISearchRepositoryPort<PaymentFilterDto, Optional<PaymentModel>> iSearchRepositoryPort;

    @Override
    public PaymentQRCodeDto createItem(PaymentCreateDto item) {

        final var orderDomain = iSearchUniqueOrderRepositoryPort.sendRequest(OrderFilterRequest.builder()
                        .orderId(item.getOrderId())
                        .customerId(item.getCustomerId())
                        .build())
                .orElseThrow(() -> new ElementNotFoundException(String.format("Order [%s] not found", item.getOrderId())));

        final var paymentQRCodeDtoOpt = iSearchRepositoryPort.findAll(
                PaymentFilterDto.builder()
                    .orderId(orderDomain.getId())
                    .status(PaymentDomain.PaymentStatus.PENDING.key)
                .build()
        ).map(paymentModel -> new PaymentQRCodeDto(
                paymentModel.getQrcodeBase64(),
                orderDomain.getId(),
                paymentModel.getStatus().key,
                paymentModel.getId(),
                paymentModel.getEmail()
        ));

        if(paymentQRCodeDtoOpt.isPresent()) return paymentQRCodeDtoOpt.get();

        if (orderDomain.getStatus().equals(OrderStatusEnum.WAITING_PAYMENT.toString())) {
            final var paymentDomainBuilder = PaymentDomain.builder();

            Optional.ofNullable(iContextUseCase.getContext())
                    .filter(StringUtils::isNotEmpty)
                    .ifPresent(customer -> {
                        final var customerModel = iSearchUniqueCustomerRepositoryPort.sendRequest(CustomerFilterRequest.builder()
                                        .customer(customer)
                                        .build())
                                .orElseThrow(() -> new ElementNotFoundException(String.format("Customer [%s] not found", customer)));
                        paymentDomainBuilder.customer(customerModel.getCpf());
                    });


            paymentDomainBuilder.order(orderDomain.getId());
            paymentDomainBuilder.price(orderDomain.getPrice());
            paymentDomainBuilder.token(UUID.randomUUID().toString());
            paymentDomainBuilder.status(PaymentDomain.PaymentStatus.PENDING);
            paymentDomainBuilder.email(0);


            final var paymentDomain = paymentDomainBuilder.build();
            paymentDomain.fillDates();

            final var paymentModel = iPaymentMapper.toModel(paymentDomain);

            final var paymentDomainSaved = iCreateRepositoryPort.saveItem(paymentModel);

            final var paymentDto = iSendRequest.sendRequest(paymentDomainSaved);

            if (paymentDto.getQrcodeBase64() != null) {
                paymentDomainSaved.setQrcodeBase64(paymentDto.getQrcodeBase64());
                iUpdateRepositoryPort.updateItem(paymentDomainSaved);
            }

            paymentDto.setEmail(paymentModel.getEmail());
            paymentDto.setStatus(PaymentDomain.PaymentStatus.PENDING.key);
            paymentDto.setPaymentId(paymentDomainSaved.getId());
            return paymentDto;
        }

        throw new InvalidStatusException("Order", orderDomain.getStatus(), OrderStatusEnum.WAITING_PAYMENT.toString());
    }

}