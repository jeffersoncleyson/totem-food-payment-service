package com.totem.food.framework.adapters.out.web.internal.customer.request;

import com.totem.food.application.ports.out.internal.customer.CustomerFilterRequest;
import com.totem.food.application.ports.out.internal.customer.CustomerResponse;
import com.totem.food.application.ports.out.web.ISendRequestPort;
import com.totem.food.framework.adapters.out.web.internal.customer.client.CustomerMicroServiceClientApi;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@AllArgsConstructor
@Component
public class CustomerRequestAdapter implements ISendRequestPort<CustomerFilterRequest, Optional<CustomerResponse>> {

    private final CustomerMicroServiceClientApi customerMicroServiceClientApi;

    @Override
    public Optional<CustomerResponse> sendRequest(CustomerFilterRequest item) {
        return Optional.of(customerMicroServiceClientApi.getCustomerByCpf(item.getCustomer()))
                .map(ResponseEntity::getBody);
    }
}
