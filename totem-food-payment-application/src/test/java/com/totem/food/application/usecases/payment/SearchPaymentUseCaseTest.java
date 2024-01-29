package com.totem.food.application.usecases.payment;

import com.totem.food.application.ports.in.dtos.payment.PaymentDto;
import com.totem.food.application.ports.in.dtos.payment.PaymentFilterDto;
import com.totem.food.application.ports.in.mappers.payment.IPaymentMapper;
import com.totem.food.application.ports.out.persistence.commons.ISearchRepositoryPort;
import com.totem.food.application.ports.out.persistence.payment.PaymentModel;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchPaymentUseCaseTest {

    @Mock
    private ISearchRepositoryPort<PaymentFilterDto, Optional<PaymentModel>> iSearchRepositoryPort;

    @Mock
    private IPaymentMapper iPaymentMapper = Mappers.getMapper(IPaymentMapper.class);

    private SearchPaymentUseCase searchPaymentUseCase;

    private AutoCloseable autoCloseable;

    @BeforeEach
    public void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        searchPaymentUseCase = new SearchPaymentUseCase(iSearchRepositoryPort, iPaymentMapper);
    }

    @SneakyThrows
    @AfterEach
    void down() {
        autoCloseable.close();
    }

    @Test
    void items() {

        //## Mock - Object and Value
        var paymentModel = new PaymentModel();
        var paymentDto = new PaymentDto();

        //## Given
        when(iSearchRepositoryPort.findAll(any(PaymentFilterDto.class))).thenReturn(Optional.of(paymentModel));
        when(iPaymentMapper.toDto(any(PaymentModel.class))).thenReturn(paymentDto);

        //## When
        var result = searchPaymentUseCase.items(new PaymentFilterDto());

        //## Then
        assertThat(result).isPresent();
        verify(iSearchRepositoryPort, times(1)).findAll(any(PaymentFilterDto.class));
        verify(iPaymentMapper, times(1)).toDto(any(PaymentModel.class));
    }

}