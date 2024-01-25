package com.totem.food.framework.adapters.out.web.internal.order.request;

import com.totem.food.application.ports.out.internal.order.OrderFilterRequest;
import com.totem.food.application.ports.out.internal.order.OrderResponseRequest;
import com.totem.food.application.ports.out.web.ISendRequestPort;
import com.totem.food.framework.adapters.out.web.internal.order.client.OrderMicroServiceClientApi;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@AllArgsConstructor
@Component
public class OrderSearchRequestAdapter implements ISendRequestPort<OrderFilterRequest, Optional<OrderResponseRequest>> {

    private final OrderMicroServiceClientApi orderMicroServiceClientApi;

    @Override
    public Optional<OrderResponseRequest> sendRequest(OrderFilterRequest item) {
        return Optional.of(orderMicroServiceClientApi.getOrderById(item.getOrderId()))
                .map(ResponseEntity::getBody);
    }
}
