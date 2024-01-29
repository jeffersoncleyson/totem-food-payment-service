package com.totem.food.framework.adapters.out.web.internal.customer.request;

import com.totem.food.application.ports.out.internal.customer.CustomerFilterRequest;
import com.totem.food.application.ports.out.internal.customer.CustomerResponse;
import com.totem.food.framework.adapters.out.web.internal.customer.client.CustomerMicroServiceClientApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerRequestAdapterTest {

    @Mock
    CustomerMicroServiceClientApi customerMicroServiceClientApi;

    @InjectMocks
    CustomerRequestAdapter customerRequestAdapter;

    @Test
    void testSendRequest_whenCpfIsValid() {

        //## Mock - Objects and Value
        String cpf = UUID.randomUUID().toString();
        var customerFilterRequest = CustomerFilterRequest.builder().customer(cpf).build();

        var customerResponse = new CustomerResponse();
        customerResponse.setCpf(cpf);

        //## Given
        when(customerMicroServiceClientApi.getCustomerByCpf(anyString())).thenReturn(ResponseEntity.of(Optional.of(customerResponse)));

        //## When
        var response = customerRequestAdapter.sendRequest(customerFilterRequest);

        //## Then
        assertThat(response).isPresent().contains(customerResponse);
        verify(customerMicroServiceClientApi, times(1)).getCustomerByCpf(anyString());
    }

}