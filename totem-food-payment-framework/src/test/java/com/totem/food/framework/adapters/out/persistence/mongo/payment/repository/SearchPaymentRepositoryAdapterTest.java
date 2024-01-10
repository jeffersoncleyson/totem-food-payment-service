package com.totem.food.framework.adapters.out.persistence.mongo.payment.repository;

import com.totem.food.application.ports.in.dtos.payment.PaymentFilterDto;
import com.totem.food.application.ports.out.persistence.commons.ISearchRepositoryPort;
import com.totem.food.application.ports.out.persistence.payment.PaymentModel;
import com.totem.food.domain.payment.PaymentDomain;
import com.totem.food.framework.adapters.out.persistence.mongo.payment.mapper.IPaymentEntityMapper;
import lombok.SneakyThrows;
import mocks.entity.PaymentEntityMock;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.List;

import static com.totem.food.domain.payment.PaymentDomain.PaymentStatus.PENDING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchPaymentRepositoryAdapterTest {

    @Mock
    private SearchPaymentRepositoryAdapter.PaymentRepositoryMongoDB repository;
    @Spy
    private IPaymentEntityMapper iPaymentMapper = Mappers.getMapper(IPaymentEntityMapper.class);

    private ISearchRepositoryPort<PaymentFilterDto, List<PaymentModel>> iSearchRepositoryPort;
    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        iSearchRepositoryPort = new SearchPaymentRepositoryAdapter(repository, iPaymentMapper);
    }

    @SneakyThrows
    @AfterEach
    void tearDown() {
        autoCloseable.close();
    }

    @Test
    void findAllWithFilterStatusdAndTimeLastOrders() {

        //## Given
        final var entities = List.of(PaymentEntityMock.getPaymentEntity(PaymentDomain.PaymentStatus.PENDING));
        final var paymentFilter = PaymentFilterDto.builder()
                .status(PENDING.name())
                .timeLastOrders(ZonedDateTime.now().minusMinutes(30))
                .build();

        //## Given Mocks
        when(repository.findByStatusAndModifiedAtAfter(paymentFilter.getStatus(), paymentFilter.getTimeLastOrders()))
                .thenReturn(entities);

        //## When
        final var paymentDomains = iSearchRepositoryPort.findAll(paymentFilter);

        //## Then
        verify(repository, times(1)).findByStatusAndModifiedAtAfter(paymentFilter.getStatus(), paymentFilter.getTimeLastOrders());
        verify(iPaymentMapper, times(1)).toModel(entities);

        final var paymentEntityConverted = iPaymentMapper.toEntity(paymentDomains.get(0));
        assertThat(paymentEntityConverted)
                .usingRecursiveComparison()
                .ignoringFields("order.cpf")
                .isEqualTo(entities.get(0));

    }

    @Test
    void findAllWithFilterOrderIdAndToken() {

        //## Given
        final var paymentEntity = PaymentEntityMock.getPaymentEntity(PaymentDomain.PaymentStatus.PENDING);
        final var paymentFilter = PaymentFilterDto.builder()
                //@todo - refact
                //.orderId(paymentEntity.getOrder().getId())
                .token(paymentEntity.getToken())
                .build();

        //## Given Mocks
        when(repository.findByFilter(new ObjectId(paymentFilter.getOrderId()), paymentFilter.getToken()))
                .thenReturn(paymentEntity);

        //## When
        final var paymentDomain = iSearchRepositoryPort.findAll(paymentFilter);

        //## Then
        verify(repository, times(1)).findByFilter(new ObjectId(paymentFilter.getOrderId()), paymentFilter.getToken());
        verify(iPaymentMapper, times(1)).toModel(paymentEntity);

        final var paymentEntityConverted = iPaymentMapper.toEntity(paymentDomain.get(0));
        assertThat(paymentEntityConverted)
                .usingRecursiveComparison()
                .ignoringFields("order.cpf")
                .isEqualTo(paymentEntity);

    }

    @Test
    void findAllWithFilterOrderIdAndStatus() {

        //## Given
        final var paymentEntity = PaymentEntityMock.getPaymentEntity(PaymentDomain.PaymentStatus.PENDING);
        final var paymentFilter = PaymentFilterDto.builder()
//@todo - refact
//                .orderId(paymentEntity.getOrder().getId())
//                .status(OrderStatusEnumDomain.IN_PREPARATION.key)
                .build();

        //## Given Mocks
        when(repository.findPaymentByOrderAndStatus(new ObjectId(paymentFilter.getOrderId()), paymentFilter.getStatus()))
                .thenReturn(paymentEntity);

        //## When
        final var paymentDomain = iSearchRepositoryPort.findAll(paymentFilter);

        //## Then
        verify(repository, times(1)).findPaymentByOrderAndStatus(new ObjectId(paymentFilter.getOrderId()), paymentFilter.getStatus());
        verify(iPaymentMapper, times(1)).toModel(paymentEntity);

        final var paymentEntityConverted = iPaymentMapper.toEntity(paymentDomain.get(0));
        assertThat(paymentEntityConverted)
                .usingRecursiveComparison()
                .ignoringFields("order.cpf")
                .isEqualTo(paymentEntity);

    }

    @Test
    void findAllNoResults() {

        //## Given
        final var paymentFilter = PaymentFilterDto.builder().build();

        //## When
        final var paymentModels = iSearchRepositoryPort.findAll(paymentFilter);

        //## Then
        assertEquals(paymentModels, List.of());
        verify(repository, times(0)).findByStatusAndModifiedAtAfter(any(), any());
        verify(repository, times(0)).findByFilter(any(), any());
        verify(repository, times(0)).findPaymentByOrderAndStatus(any(), any());
        verify(iPaymentMapper, times(0)).toModel(anyList());

    }
}