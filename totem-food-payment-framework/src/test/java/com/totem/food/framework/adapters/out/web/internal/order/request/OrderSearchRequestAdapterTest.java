package com.totem.food.framework.adapters.out.web.internal.order.request;

import com.totem.food.application.ports.out.internal.order.OrderFilterRequest;
import com.totem.food.application.ports.out.internal.order.OrderResponseRequest;
import com.totem.food.framework.adapters.out.web.internal.order.client.OrderMicroServiceClientApi;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderSearchRequestAdapterTest {

    @Mock
    private OrderMicroServiceClientApi orderMicroServiceClientApi;

    private OrderSearchRequestAdapter orderSearchRequestAdapter;

    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        orderSearchRequestAdapter = new OrderSearchRequestAdapter(orderMicroServiceClientApi);
    }

    @SneakyThrows
    @AfterEach
    void tearDown() {
        autoCloseable.close();
    }

    @Test
    void testSendRequest() {

        //## Mock - Object and Value
        String uuid = UUID.randomUUID().toString();
        var orderFilterRequest = OrderFilterRequest.builder().orderId(uuid).customerId(uuid).build();
        var orderResponseRequest = OrderResponseRequest.builder().id(uuid).build();

        //## Given
        when(orderMicroServiceClientApi.getOrderById(orderFilterRequest.getOrderId(), orderFilterRequest.getCustomerId()))
                .thenReturn(ResponseEntity.of(Optional.of(orderResponseRequest)));

        //## When
        var response = orderSearchRequestAdapter.sendRequest(orderFilterRequest);

        //## Then
        assertNotNull(response);
        assertThat(response).isPresent().contains(orderResponseRequest);
        verify(orderMicroServiceClientApi, times(1)).getOrderById(anyString(), anyString());
    }

}