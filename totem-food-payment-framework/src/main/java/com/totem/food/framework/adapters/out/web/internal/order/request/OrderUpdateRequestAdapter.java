package com.totem.food.framework.adapters.out.web.internal.order.request;

import com.totem.food.application.ports.out.internal.order.OrderUpdateRequest;
import com.totem.food.application.ports.out.web.ISendRequestPort;
import org.springframework.stereotype.Component;

@Component
public class OrderUpdateRequestAdapter implements ISendRequestPort<OrderUpdateRequest, Boolean> {

    @Override
    public Boolean sendRequest(OrderUpdateRequest item) {
        return Boolean.FALSE;
    }
}
