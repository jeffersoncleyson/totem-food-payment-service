package com.totem.food.framework.adapters.out.web.internal.order.request;

import com.totem.food.application.enums.OrderStatusEnum;
import com.totem.food.application.ports.out.internal.order.OrderResponseRequest;
import com.totem.food.application.ports.out.internal.order.OrderUpdateRequest;
import com.totem.food.framework.adapters.out.web.internal.order.client.OrderMicroServiceClientApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static com.totem.food.application.enums.OrderStatusEnum.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderUpdateRequestAdapterTest {

    @InjectMocks
    private OrderUpdateRequestAdapter orderUpdateRequestAdapter;

    @Mock
    private OrderMicroServiceClientApi orderMicroServiceClientApi;

    @Test
    void testSendRequest() {

        //## Mock - Object and Value
        String uuid = UUID.randomUUID().toString();
        var orderUpdateRequest = OrderUpdateRequest.builder().orderId(uuid).status(RECEIVED.toString()).build();
        var orderResponseRequest = OrderResponseRequest.builder().id(uuid).status(RECEIVED.toString()).build();

        //## Given
        when(orderMicroServiceClientApi.updateOrder(orderUpdateRequest.getOrderId(), orderUpdateRequest.getStatus()))
            .thenReturn(ResponseEntity.ok(orderResponseRequest));

        //## When
        Boolean result = orderUpdateRequestAdapter.sendRequest(orderUpdateRequest);

        //## Then
        assertTrue(result);
        Mockito.verify(orderMicroServiceClientApi, times(1)).updateOrder(orderUpdateRequest.getOrderId(), orderUpdateRequest.getStatus());
    }

}