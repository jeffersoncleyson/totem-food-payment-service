package com.totem.food.application.usecases.payment;

import com.totem.food.application.ports.in.dtos.payment.PaymentDto;
import com.totem.food.application.ports.in.dtos.payment.PaymentFilterDto;
import com.totem.food.application.ports.in.mappers.payment.IPaymentMapper;
import com.totem.food.application.ports.out.persistence.commons.ISearchRepositoryPort;
import com.totem.food.application.ports.out.persistence.commons.IUpdateRepositoryPort;
import com.totem.food.application.ports.out.persistence.payment.PaymentModel;
import lombok.SneakyThrows;
import mock.models.PaymentModelMock;
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
import java.util.UUID;

import static com.totem.food.domain.payment.PaymentDomain.PaymentStatus.CANCELED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdatePaymentStatusUseCaseTest {

    @Spy
    private IPaymentMapper iPaymentMapper = Mappers.getMapper(IPaymentMapper.class);

    @Mock
    private IUpdateRepositoryPort<PaymentModel> iUpdateRepositoryPort;

    @Mock
    private ISearchRepositoryPort<PaymentFilterDto, Optional<PaymentModel>> iSearchOnePaymentRepositoryPort;

    private UpdatePaymentStatusUseCase updatePaymentStatusUseCase;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        updatePaymentStatusUseCase = new UpdatePaymentStatusUseCase(iPaymentMapper, iUpdateRepositoryPort, iSearchOnePaymentRepositoryPort);
    }

    @SneakyThrows
    @AfterEach
    void tearDown() {
        closeable.close();
    }

    @Test
    void updateStatus() {

        //## Mock - Object
        var uuid = UUID.randomUUID().toString();
        var paymentModel = PaymentModelMock.getStatusCompletedToUpdatePaymentUseCase(uuid);
        var paymentDto = PaymentDto.builder().status(CANCELED.toString()).build();

        //## Given
        when(iSearchOnePaymentRepositoryPort.findAll(any(PaymentFilterDto.class))).thenReturn(Optional.of(paymentModel));
        when(iUpdateRepositoryPort.updateItem(any(PaymentModel.class))).thenReturn(paymentModel);
        when(iPaymentMapper.toDto(any(PaymentModel.class))).thenReturn(paymentDto);

        //## When
        var result = updatePaymentStatusUseCase.updateStatus(uuid, CANCELED.toString());

        //## Then
        assertNotNull(result);
        assertThat(result).usingRecursiveComparison().isEqualTo(paymentDto);
    }

    @Test
    void updateStatusWhenReturnNull() {

        //## Given
        when(iSearchOnePaymentRepositoryPort.findAll(any(PaymentFilterDto.class))).thenReturn(Optional.empty());

        //## When
        var result = updatePaymentStatusUseCase.updateStatus(UUID.randomUUID().toString(), CANCELED.toString());

        //## Then
        assertNull(result);
        verify(iUpdateRepositoryPort, never()).updateItem(any(PaymentModel.class));
        verify(iPaymentMapper, never()).toDto(any(PaymentModel.class));
    }

}