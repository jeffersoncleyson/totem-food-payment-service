package com.totem.food.framework.adapters.out.web.internal.order.request;

import com.totem.food.application.ports.out.internal.order.OrderResponseRequest;
import com.totem.food.application.ports.out.internal.order.OrderUpdateRequest;
import com.totem.food.application.ports.out.web.ISendRequestPort;
import com.totem.food.framework.adapters.out.web.internal.order.client.OrderMicroServiceClientApi;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@AllArgsConstructor
@Component
public class OrderUpdateRequestAdapter implements ISendRequestPort<OrderUpdateRequest, Boolean> {

    private final OrderMicroServiceClientApi orderMicroServiceClientApi;

    @Override
    public Boolean sendRequest(OrderUpdateRequest item) {
        return Optional.of(orderMicroServiceClientApi.updateOrder(item.getOrderId(), item.getStatus()))
                .map(ResponseEntity::getBody)
                .map(OrderResponseRequest::getStatus)
                .map(status -> item.getStatus().equals(status))
                .orElse(false);
    }
}
