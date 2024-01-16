package com.totem.food.application.usecases.payment;

import com.totem.food.application.ports.in.dtos.payment.PaymentDto;
import com.totem.food.application.ports.in.dtos.payment.PaymentFilterDto;
import com.totem.food.application.ports.in.mappers.payment.IPaymentMapper;
import com.totem.food.application.ports.out.persistence.commons.ISearchRepositoryPort;
import com.totem.food.application.ports.out.persistence.commons.ISearchUniqueRepositoryPort;
import com.totem.food.application.ports.out.persistence.payment.PaymentModel;
import com.totem.food.application.usecases.annotations.UseCase;
import com.totem.food.application.usecases.commons.ISearchUniqueUseCase;
import com.totem.food.application.usecases.commons.ISearchUseCase;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
@UseCase
public class SearchPaymentUseCase implements ISearchUseCase<PaymentFilterDto, Optional<PaymentDto>> {

    private final ISearchRepositoryPort<PaymentFilterDto, Optional<PaymentModel>> iSearchRepositoryPort;
    private final IPaymentMapper iPaymentMapper;

    @Override
    public Optional<PaymentDto> items(PaymentFilterDto filterDto) {
        return iSearchRepositoryPort.findAll(filterDto).map(iPaymentMapper::toDto);
    }

}
