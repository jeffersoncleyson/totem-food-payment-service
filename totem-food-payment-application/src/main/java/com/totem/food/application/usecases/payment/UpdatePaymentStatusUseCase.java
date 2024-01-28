package com.totem.food.application.usecases.payment;

import com.totem.food.application.ports.in.dtos.payment.PaymentDto;
import com.totem.food.application.ports.in.dtos.payment.PaymentFilterDto;
import com.totem.food.application.ports.in.mappers.payment.IPaymentMapper;
import com.totem.food.application.ports.out.persistence.commons.ISearchRepositoryPort;
import com.totem.food.application.ports.out.persistence.commons.IUpdateRepositoryPort;
import com.totem.food.application.ports.out.persistence.payment.PaymentModel;
import com.totem.food.application.usecases.annotations.UseCase;
import com.totem.food.application.usecases.commons.IUpdateStatusUseCase;
import com.totem.food.domain.payment.PaymentDomain;

import java.util.Optional;

@UseCase
public class UpdatePaymentStatusUseCase implements IUpdateStatusUseCase<PaymentDto> {

    private final IPaymentMapper iPaymentMapper;
    private final IUpdateRepositoryPort<PaymentModel> iUpdateRepositoryPort;

    private final ISearchRepositoryPort<PaymentFilterDto, Optional<PaymentModel>> iSearchOnePaymentRepositoryPort;

    public UpdatePaymentStatusUseCase(
            IPaymentMapper iPaymentMapper,
            IUpdateRepositoryPort<PaymentModel> iUpdateRepositoryPort,
            ISearchRepositoryPort<PaymentFilterDto, Optional<PaymentModel>> iSearchOnePaymentRepositoryPort
    ) {
        this.iPaymentMapper = iPaymentMapper;
        this.iUpdateRepositoryPort = iUpdateRepositoryPort;
        this.iSearchOnePaymentRepositoryPort = iSearchOnePaymentRepositoryPort;
    }

    @Override
    public PaymentDto updateStatus(String id, String status) {
        return iSearchOnePaymentRepositoryPort.findAll(PaymentFilterDto.builder()
                .orderId(id)
                .status(status)
                .build()
        ).map(payment -> {
            payment.updateStatus(PaymentDomain.PaymentStatus.CANCELED);
            return iUpdateRepositoryPort.updateItem(payment);
        })
        .map(iPaymentMapper::toDto)
        .orElse(null);
    }
}