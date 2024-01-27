package com.totem.food.application.usecases.payment;

import com.totem.food.application.exceptions.ElementNotFoundException;
import com.totem.food.application.ports.in.dtos.payment.PaymentElementDto;
import com.totem.food.application.ports.in.dtos.payment.PaymentFilterDto;
import com.totem.food.application.ports.in.mappers.payment.IPaymentMapper;
import com.totem.food.application.ports.out.internal.order.OrderFilterRequest;
import com.totem.food.application.ports.out.internal.order.OrderResponseRequest;
import com.totem.food.application.ports.out.internal.order.OrderUpdateRequest;
import com.totem.food.application.ports.out.persistence.commons.ISearchRepositoryPort;
import com.totem.food.application.ports.out.persistence.commons.IUpdateRepositoryPort;
import com.totem.food.application.ports.out.persistence.payment.PaymentModel;
import com.totem.food.application.ports.out.web.ISendRequestPort;
import com.totem.food.domain.payment.PaymentDomain;
import lombok.SneakyThrows;
import mock.domain.PaymentDomainMock;
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
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdatePaymentUseCaseTest {

    @Spy
    private IPaymentMapper iPaymentMapper = Mappers.getMapper(IPaymentMapper.class);

    @Mock
    private IUpdateRepositoryPort<PaymentModel> iUpdateRepositoryPort;

    @Mock
    private ISendRequestPort<OrderFilterRequest, Optional<OrderResponseRequest>> iSearchOrderModel;

    @Mock
    private ISendRequestPort<OrderUpdateRequest, Boolean> iUpdateOrderRepositoryPort;

    @Mock
    private ISearchRepositoryPort<PaymentFilterDto, List<PaymentModel>> iSearchRepositoryPort;

    @Mock
    private ISendRequestPort<Integer, PaymentElementDto> iSendRequest;

    private UpdatePaymentUseCase updatePaymentUseCase;

    private AutoCloseable closeable;

    @Mock
    private Environment environment;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        when(environment.getActiveProfiles()).thenReturn(new String[]{"default"});
        updatePaymentUseCase = new UpdatePaymentUseCase(iPaymentMapper, iUpdateRepositoryPort, iSearchOrderModel, iUpdateOrderRepositoryPort,
            iSearchRepositoryPort, iSendRequest, environment);

    }

    @SneakyThrows
    @AfterEach
    void tearDown() {
        closeable.close();
    }

    @Test
    void updateItemWhenPaymentIsEmpty() {

        //## Mock - Object
        var uuid = UUID.randomUUID().toString();

        var paymentModel = PaymentModelMock.getStatusCompletedToUpdatePaymentUseCase(uuid);

        var paymentFilter = PaymentFilterDto.builder()
            .orderId(uuid)
            .token(paymentModel.getToken())
            .build();

        //## Given
        when(iSearchRepositoryPort.findAll(any(PaymentFilterDto.class))).thenReturn(List.of());

        //## When
        Boolean result = updatePaymentUseCase.updateItem(paymentFilter, paymentModel.getCustomer());

        //## Then
        assertFalse(result);
        verify(iSendRequest, never()).sendRequest(any(Integer.class));
        verify(iPaymentMapper, never()).toDomain(any(PaymentModel.class));
        verify(iSearchOrderModel, never()).sendRequest(any(OrderFilterRequest.class));
        verify(iPaymentMapper, never()).toModel(any(PaymentDomain.class));
        verify(iUpdateRepositoryPort, never()).updateItem(any(PaymentModel.class));
    }

    @Test
    void updateItem() {

        //## Mock - Object
        var uuid = UUID.randomUUID().toString();

        var paymentModel = PaymentModelMock.getStatusCompletedToUpdatePaymentUseCase(uuid);

        var paymentElementDto = PaymentElementDto.builder()
            .orderStatus("paid")
            .build();

        var paymentDomain = PaymentDomainMock.getPaymentToUpdatePaymentUseCase(PaymentDomain.PaymentStatus.PENDING);

        var paymentFilter = PaymentFilterDto.builder()
            .orderId(uuid)
            .token(paymentModel.getToken())
            .build();

        var orderResponseRequest = OrderResponseRequest.builder()
            .id(uuid)
            .status(paymentModel.getStatus().toString())
            .price(paymentModel.getPrice())
            .build();

        //## Given
        when(iSearchRepositoryPort.findAll(any(PaymentFilterDto.class))).thenReturn(List.of(paymentModel));
        when(iSendRequest.sendRequest(any(Integer.class))).thenReturn(paymentElementDto);
        when(iPaymentMapper.toDomain(any(PaymentModel.class))).thenReturn(paymentDomain);
        when(iSearchOrderModel.sendRequest(any(OrderFilterRequest.class))).thenReturn(Optional.ofNullable(orderResponseRequest));
        when(iPaymentMapper.toModel(any(PaymentDomain.class))).thenReturn(paymentModel);
        when(iUpdateRepositoryPort.updateItem(any(PaymentModel.class))).thenReturn(paymentModel);

        //## When
        Boolean result = updatePaymentUseCase.updateItem(paymentFilter, paymentModel.getCustomer());

        //## Then
        assertTrue(result);
    }

    @Test
    void updateItemWhenElementNotFoundException() {

        //## Mock - Object
        var uuid = UUID.randomUUID().toString();

        var paymentModel = PaymentModelMock.getStatusCompletedToUpdatePaymentUseCase(uuid);

        var paymentElementDto = PaymentElementDto.builder()
            .orderStatus("paid")
            .build();

        var paymentDomain = PaymentDomainMock.getPaymentToUpdatePaymentUseCase(PaymentDomain.PaymentStatus.PENDING);

        var paymentFilter = PaymentFilterDto.builder()
            .orderId(uuid)
            .token(paymentModel.getToken())
            .build();

        //## Given
        when(iSearchRepositoryPort.findAll(any(PaymentFilterDto.class))).thenReturn(List.of(paymentModel));
        when(iSendRequest.sendRequest(any(Integer.class))).thenReturn(paymentElementDto);
        when(iPaymentMapper.toDomain(any(PaymentModel.class))).thenReturn(paymentDomain);
        when(iSearchOrderModel.sendRequest(any(OrderFilterRequest.class))).thenReturn(Optional.empty());

        //## When
        var exception = assertThrows(ElementNotFoundException.class,
            () -> updatePaymentUseCase.updateItem(paymentFilter, paymentModel.getCustomer()));

        //## Then
        assertNotNull(exception.getMessage());
        assertEquals(String.format("Order with orderId: [%s] not found", paymentDomain.getOrder()), exception.getMessage());
    }

    @Test
    void updateItemWhenVerifyOrderPaidReturnFalse() {

        //## Mock - Object
        var uuid = UUID.randomUUID().toString();

        var paymentModel = PaymentModelMock.getStatusCompletedToUpdatePaymentUseCase(uuid);

        var paymentElementDto = PaymentElementDto.builder()
            .orderStatus("paid")
            .build();

        var paymentDomain = PaymentDomainMock.getPaymentToUpdatePaymentUseCase(PaymentDomain.PaymentStatus.COMPLETED);

        var paymentFilter = PaymentFilterDto.builder()
            .orderId(uuid)
            .token(paymentModel.getToken())
            .build();

        var orderResponseRequest = OrderResponseRequest.builder()
            .id(uuid)
            .status(paymentModel.getStatus().toString())
            .price(paymentModel.getPrice())
            .build();

        //## Given
        when(iSearchRepositoryPort.findAll(any(PaymentFilterDto.class))).thenReturn(List.of(paymentModel));
        when(iSendRequest.sendRequest(any(Integer.class))).thenReturn(paymentElementDto);
        when(iPaymentMapper.toDomain(any(PaymentModel.class))).thenReturn(paymentDomain);
        when(iSearchOrderModel.sendRequest(any(OrderFilterRequest.class))).thenReturn(Optional.ofNullable(orderResponseRequest));

        //## When
        Boolean result = updatePaymentUseCase.updateItem(paymentFilter, paymentModel.getCustomer());

        //## Then
        assertTrue(result);
    }

}