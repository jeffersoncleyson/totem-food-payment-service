package com.totem.food.framework.adapters.out.persistence.mysql.payment.repository;

import com.totem.food.application.ports.out.persistence.commons.ISearchUniqueRepositoryPort;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchUniquePaymentRepositoryAdapterTest {

    @Mock
    private SearchUniquePaymentRepositoryAdapter.PaymentRepositoryMongoDB repository;
    @Spy
    private IPaymentEntityMapper iPaymentEntityMapper = Mappers.getMapper(IPaymentEntityMapper.class);

    private ISearchUniqueRepositoryPort<Optional<PaymentModel>> iSearchUniqueRepositoryPort;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        iSearchUniqueRepositoryPort = new SearchUniquePaymentRepositoryAdapter(repository, iPaymentEntityMapper);
    }

    @SneakyThrows
    @AfterEach
    void tearDown() {
        closeable.close();
    }

    @Test
    void findById() {

        //## Mock - Object and Value
        var id = 1;
        var paymentEntity = PaymentEntity.builder().id(id).build();

        //## Given
        when(repository.findById(id)).thenReturn(Optional.of(paymentEntity));

        //## When
        var paymentDomain = iSearchUniqueRepositoryPort.findById(id);

        //## Then
        assertTrue(paymentDomain.isPresent());
        assertEquals(paymentDomain.get().getId(), id);
    }
}