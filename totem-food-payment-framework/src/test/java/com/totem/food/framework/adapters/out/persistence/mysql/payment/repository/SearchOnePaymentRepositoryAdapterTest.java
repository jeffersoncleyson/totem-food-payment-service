package com.totem.food.framework.adapters.out.persistence.mysql.payment.repository;

import com.totem.food.application.ports.in.dtos.payment.PaymentFilterDto;
import com.totem.food.application.ports.out.persistence.commons.ISearchRepositoryPort;
import com.totem.food.application.ports.out.persistence.payment.PaymentModel;
import com.totem.food.framework.adapters.out.persistence.mysql.payment.entity.PaymentEntity;
import com.totem.food.framework.adapters.out.persistence.mysql.payment.mapper.IPaymentEntityMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.totem.food.application.enums.OrderStatusEnum.WAITING_PAYMENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchOnePaymentRepositoryAdapterTest {

    @Mock
    private SearchOnePaymentRepositoryAdapter.PaymentRepositoryMongoDB repository;

    @Spy
    private IPaymentEntityMapper iPaymentEntityMapper = Mappers.getMapper(IPaymentEntityMapper.class);

    private ISearchRepositoryPort<PaymentFilterDto, Optional<PaymentModel>> iSearchRepositoryPort;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        iSearchRepositoryPort = new SearchOnePaymentRepositoryAdapter(repository, iPaymentEntityMapper);
    }


    @SneakyThrows
    @AfterEach
    void tearDown() {
        closeable.close();
    }

    @Test
    void findAll() {

        //## Mock - Object and Value
        int id = 1;
        String orderId = "123";
        var paymentFilterDto = PaymentFilterDto.builder()
            .orderId(orderId)
            .status(WAITING_PAYMENT.getKey())
            .build();
        var paymentEntity = PaymentEntity.builder()
            .id(id)
            .build();

        //## Given
        when(repository.findByFilter(anyString(), anyString())).thenReturn(paymentEntity);

        //## When
        var result = iSearchRepositoryPort.findAll(paymentFilterDto);

        //## Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        verify(iPaymentEntityMapper, times(1)).toModel(any(PaymentEntity.class));
    }

    @Test
    void findAllWhenReturnEmpty() {

        //## Given - Mock
        var paymentFilterDto = PaymentFilterDto.builder().orderId("123").build();

        //## When
        var result = iSearchRepositoryPort.findAll(paymentFilterDto);

        //## Then
        assertThat(result).isEmpty();
        verify(repository, never()).findByFilter(anyString(), anyString());
    }

}