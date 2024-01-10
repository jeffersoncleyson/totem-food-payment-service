package com.totem.food.framework.adapters.out.web.internal.customer.request;

import com.totem.food.application.ports.out.internal.customer.CustomerFilterRequest;
import com.totem.food.application.ports.out.internal.customer.CustomerResponseRequest;
import com.totem.food.application.ports.out.internal.order.OrderUpdateRequest;
import com.totem.food.application.ports.out.web.ISendRequestPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomerRequestAdapter implements ISendRequestPort<CustomerFilterRequest, Optional<CustomerResponseRequest>> {

    @Override
    public Optional<CustomerResponseRequest> sendRequest(CustomerFilterRequest item) {
        return Optional.empty();
    }
}
