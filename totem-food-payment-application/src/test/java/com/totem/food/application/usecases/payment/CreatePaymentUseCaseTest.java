package com.totem.food.application.usecases.payment;

import com.totem.food.application.exceptions.ElementNotFoundException;
import com.totem.food.application.ports.in.dtos.context.XUserIdentifierContextDto;
import com.totem.food.application.ports.in.dtos.payment.PaymentCreateDto;
import com.totem.food.application.ports.in.dtos.payment.PaymentQRCodeDto;
import com.totem.food.application.ports.in.mappers.payment.IPaymentMapper;
import com.totem.food.application.ports.out.internal.customer.CustomerFilterRequest;
import com.totem.food.application.ports.out.internal.customer.CustomerResponse;
import com.totem.food.application.ports.out.internal.order.OrderFilterRequest;
import com.totem.food.application.ports.out.internal.order.OrderResponseRequest;
import com.totem.food.application.ports.out.persistence.commons.ICreateRepositoryPort;
import com.totem.food.application.ports.out.persistence.commons.IUpdateRepositoryPort;
import com.totem.food.application.ports.out.persistence.payment.PaymentModel;
import com.totem.food.application.ports.out.web.ISendRequestPort;
import com.totem.food.application.usecases.commons.IContextUseCase;
import com.totem.food.domain.exceptions.InvalidStatusException;
import com.totem.food.domain.payment.PaymentDomain;
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

import static com.totem.food.application.enums.OrderStatusEnum.NEW;
import static com.totem.food.application.enums.OrderStatusEnum.WAITING_PAYMENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreatePaymentUseCaseTest {

    @Mock
    private ICreateRepositoryPort<PaymentModel> iCreateRepositoryPort;

    @Mock
    private IUpdateRepositoryPort<PaymentModel> iUpdateRepositoryPort;

    @Spy
    private IPaymentMapper iPaymentMapper = Mappers.getMapper(IPaymentMapper.class);

    @Mock
    private ISendRequestPort<OrderFilterRequest, Optional<OrderResponseRequest>> iSearchUniqueOrderRepositoryPort;

    @Mock
    private ISendRequestPort<CustomerFilterRequest, Optional<CustomerResponse>> iSearchUniqueCustomerRepositoryPort;

    @Mock
    private ISendRequestPort<PaymentModel, PaymentQRCodeDto> iSendRequest;

    @Mock
    private IContextUseCase<XUserIdentifierContextDto, String> iContextUseCase;

    private CreatePaymentUseCase createPaymentUseCase;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        createPaymentUseCase = new CreatePaymentUseCase(iCreateRepositoryPort, iUpdateRepositoryPort, iPaymentMapper,
            iSearchUniqueOrderRepositoryPort, iSearchUniqueCustomerRepositoryPort, iSendRequest, iContextUseCase);
    }

    @SneakyThrows
    @AfterEach
    void tearDown() {
        closeable.close();
    }


    @Test
    void createItemWhenSearchUniqueOrderElementNotFoundException() {

        //## Mock - Objects and Value
        var paymentCreateDto = new PaymentCreateDto();
        paymentCreateDto.setOrderId("1");

        //## Given
        when(iSearchUniqueOrderRepositoryPort.sendRequest(any(OrderFilterRequest.class)))
            .thenReturn(Optional.empty());

        //## When
        var exception = assertThrows(ElementNotFoundException.class,
            () -> createPaymentUseCase.createItem(paymentCreateDto));

        //## Then
        assertEquals(String.format("Order [%s] not found", paymentCreateDto.getOrderId()), exception.getMessage());
    }

    @Test
    void createItem() {

        //## Mock - Objects and Value
        String uuid = UUID.randomUUID().toString();
        var orderResponseRequest = OrderResponseRequest.builder()
            .status(WAITING_PAYMENT.toString())
            .build();

        var paymentCreateDto = new PaymentCreateDto();
        paymentCreateDto.setOrderId(uuid);

        var customerResponse = new CustomerResponse();
        customerResponse.setCpf(uuid);

        var paymentModel = PaymentModelMock.getPaymentStatusPendingMock();

        var paymentQRCode = new PaymentQRCodeDto();
        paymentQRCode.setQrcodeBase64("00020101021243650016COM.MERCADOLIBRE" +
            "020130636d0e1a0bb-609c-4afe-8376-" +
            "4a55369083945204000053039865802BR5909" +
            "Test Test6009SAO PAULO62070503***6304AA96");

        //## Given
        when(iSearchUniqueOrderRepositoryPort.sendRequest(any(OrderFilterRequest.class)))
            .thenReturn(Optional.of(orderResponseRequest));
        when(iContextUseCase.getContext()).thenReturn(uuid);
        when(iSearchUniqueCustomerRepositoryPort.sendRequest(any(CustomerFilterRequest.class))).thenReturn(Optional.of(customerResponse));
        when(iPaymentMapper.toModel(any(PaymentDomain.class))).thenReturn(paymentModel);
        when(iCreateRepositoryPort.saveItem(any(PaymentModel.class))).thenReturn(paymentModel);
        when(iSendRequest.sendRequest(any(PaymentModel.class))).thenReturn(paymentQRCode);

        paymentModel.setQrcodeBase64(paymentQRCode.getQrcodeBase64());
        when(iUpdateRepositoryPort.updateItem(any(PaymentModel.class))).thenReturn(paymentModel);

        //## When
        var result = createPaymentUseCase.createItem(paymentCreateDto);

        //## Then
        assertNotNull(result);
        assertEquals(paymentQRCode.getQrcodeBase64(), result.getQrcodeBase64());
    }

    @Test
    void createItemWhenSearchUniqueCustomerElementNotFoundException() {

        //## Mock - Objects and Value
        String uuid = UUID.randomUUID().toString();
        var orderResponseRequest = OrderResponseRequest.builder()
            .status(WAITING_PAYMENT.toString())
            .build();

        var paymentCreateDto = new PaymentCreateDto();
        paymentCreateDto.setOrderId(uuid);

        var customerResponse = new CustomerResponse();
        customerResponse.setCpf(uuid);

        //## Given
        when(iSearchUniqueOrderRepositoryPort.sendRequest(any(OrderFilterRequest.class)))
            .thenReturn(Optional.of(orderResponseRequest));
        when(iContextUseCase.getContext()).thenReturn(uuid);
        when(iSearchUniqueCustomerRepositoryPort.sendRequest(any(CustomerFilterRequest.class))).thenReturn(Optional.empty());

        //## When
        var exception = assertThrows(ElementNotFoundException.class,
            () -> createPaymentUseCase.createItem(paymentCreateDto));

        //## Then
        assertEquals(String.format("Customer [%s] not found", uuid), exception.getMessage());
    }

    @Test
    void createItemWhenInvalidStatusException() {

        //## Mock - Objects and Value
        var orderResponseRequest = OrderResponseRequest.builder()
            .status(NEW.toString())
            .build();
        var paymentCreateDto = new PaymentCreateDto();
        paymentCreateDto.setOrderId("1");

        //## Given
        when(iSearchUniqueOrderRepositoryPort.sendRequest(any(OrderFilterRequest.class)))
            .thenReturn(Optional.of(orderResponseRequest));

        //## When
        var exception = assertThrows(InvalidStatusException.class,
            () -> createPaymentUseCase.createItem(paymentCreateDto));

        //## Then
        assertEquals(String.format("Invalid Order status [%s] expected to be [%s]", NEW, WAITING_PAYMENT), exception.getMessage());
    }

}