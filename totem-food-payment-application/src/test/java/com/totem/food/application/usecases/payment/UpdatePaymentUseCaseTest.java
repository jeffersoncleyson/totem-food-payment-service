package com.totem.food.application.usecases.payment;

import com.totem.food.application.ports.in.dtos.event.PaymentEventMessageDto;
import com.totem.food.application.ports.in.dtos.payment.PaymentElementDto;
import com.totem.food.application.ports.in.dtos.payment.PaymentFilterDto;
import com.totem.food.application.ports.in.mappers.payment.IPaymentMapper;
import com.totem.food.application.ports.out.email.EmailNotificationDto;
import com.totem.food.application.ports.out.event.ISendEventPort;
import com.totem.food.application.ports.out.internal.customer.CustomerFilterRequest;
import com.totem.food.application.ports.out.internal.customer.CustomerResponse;
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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

    @Mock
    private ISendEventPort<PaymentEventMessageDto, Boolean> sendPaymentEventPort;

    @Mock
    private ISendEventPort<EmailNotificationDto, Boolean> sendEmailEventPort;

    @Mock
    private ISendRequestPort<CustomerFilterRequest, Optional<CustomerResponse>> iSearchUniqueCustomerRepositoryPort;

    private UpdatePaymentUseCase updatePaymentUseCase;

    private AutoCloseable closeable;

    @Mock
    private Environment environment;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        when(environment.getActiveProfiles()).thenReturn(new String[]{"default"});
        updatePaymentUseCase = new UpdatePaymentUseCase(
                iPaymentMapper,
                iUpdateRepositoryPort,
                iSearchOrderModel,
                iUpdateOrderRepositoryPort,
                iSearchRepositoryPort,
                iSendRequest,
                sendPaymentEventPort,
                sendEmailEventPort,
                iSearchUniqueCustomerRepositoryPort,
                environment
        );

    }

    @SneakyThrows
    @AfterEach
    void tearDown() {
        closeable.close();
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

        var customerResponse = new CustomerResponse();
        customerResponse.setId(uuid);

        //## Given
        when(iSearchRepositoryPort.findAll(any(PaymentFilterDto.class))).thenReturn(List.of(paymentModel));
        when(iSendRequest.sendRequest(any(Integer.class))).thenReturn(paymentElementDto);
        when(iPaymentMapper.toDomain(any(PaymentModel.class))).thenReturn(paymentDomain);

        paymentModel.updateStatus(PaymentDomain.PaymentStatus.COMPLETED);
        when(iPaymentMapper.toModel(any(PaymentDomain.class))).thenReturn(paymentModel);
        when(iSearchUniqueCustomerRepositoryPort.sendRequest(any(CustomerFilterRequest.class)))
                .thenReturn(Optional.of(customerResponse));


        //## When
        Boolean result = updatePaymentUseCase.updateItem(paymentFilter, paymentModel.getCustomer());

        //## Then
        assertTrue(result);

        verify(iUpdateRepositoryPort, times(1)).updateItem(any(PaymentModel.class));
        verify(sendPaymentEventPort, times(1)).sendMessage(any(PaymentEventMessageDto.class));
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

}