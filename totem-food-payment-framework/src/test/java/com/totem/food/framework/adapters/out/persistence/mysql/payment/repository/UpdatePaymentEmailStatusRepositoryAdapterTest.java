package com.totem.food.framework.adapters.out.persistence.mysql.payment.repository;

import com.totem.food.application.exceptions.InvalidInput;
import com.totem.food.application.ports.out.persistence.payment.PaymentEmailUpdateModel;
import com.totem.food.domain.payment.PaymentDomain;
import com.totem.food.framework.adapters.out.persistence.mysql.payment.entity.PaymentEntity;
import com.totem.food.framework.adapters.out.persistence.mysql.payment.mapper.IPaymentEntityMapper;
import lombok.SneakyThrows;
import mocks.entity.PaymentEntityMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdatePaymentEmailStatusRepositoryAdapterTest {

    @Mock
    private UpdatePaymentEmailStatusRepositoryAdapter.PaymentRepositoryMongoDB repository;

    @Spy
    private IPaymentEntityMapper iPaymentEntityMapper;

    private UpdatePaymentEmailStatusRepositoryAdapter iUpdateRepositoryPort;

    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        iUpdateRepositoryPort = new UpdatePaymentEmailStatusRepositoryAdapter(repository, iPaymentEntityMapper);
    }

    @SneakyThrows
    @AfterEach
    void tearDown() {
        autoCloseable.close();
    }

    @Test
    void updateItem() {

        //## Mock - Object
        final var paymentEmailUpdateModel = PaymentEmailUpdateModel.builder()
                .id(1)
                .email(1)
                .build();
        final var paymentEntity = PaymentEntityMock.getPaymentEntity(PaymentDomain.PaymentStatus.PENDING);

        //## Given
        when(repository.updateEmail(anyInt(), anyInt())).thenReturn(1);
        when(repository.findById(anyInt())).thenReturn(Optional.ofNullable(paymentEntity));
        when(iPaymentEntityMapper.toModelEmail(any(PaymentEntity.class))).thenReturn(paymentEmailUpdateModel);

        //## When
        var result = iUpdateRepositoryPort.updateItem(paymentEmailUpdateModel);

        //## Then
        assertEquals(paymentEmailUpdateModel, result);
    }

    @Test
    void updateItemWhenInvalidInput() {

        //## Mock - Object
        final var paymentEmailUpdateModel = PaymentEmailUpdateModel.builder()
                .id(1)
                .email(1)
                .build();

        //## Given
        when(repository.updateEmail(anyInt(), anyInt())).thenReturn(0);

        //## When
        var result = Assertions.assertThrows(InvalidInput.class,
                () -> iUpdateRepositoryPort.updateItem(paymentEmailUpdateModel));

        //## Then
        assertEquals("Invalid update process for payment 1", result.getMessage());
    }
}