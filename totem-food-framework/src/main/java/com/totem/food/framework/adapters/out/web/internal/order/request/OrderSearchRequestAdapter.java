package com.totem.food.framework.adapters.out.web.internal.order.request;

import com.totem.food.application.ports.out.internal.order.OrderFilterRequest;
import com.totem.food.application.ports.out.internal.order.OrderResponseRequest;
import com.totem.food.application.ports.out.internal.order.OrderUpdateRequest;
import com.totem.food.application.ports.out.web.ISendRequestPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OrderSearchRequestAdapter implements ISendRequestPort<OrderFilterRequest, Optional<OrderResponseRequest>> {

    @Override
    public Optional<OrderResponseRequest> sendRequest(OrderFilterRequest item) {
        return Optional.empty();
    }
}
