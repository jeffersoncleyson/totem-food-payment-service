package com.totem.food.framework.adapters.out.web.internal.customer.client;

import com.totem.food.application.ports.out.internal.customer.CustomerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "CustomerMicroServiceClientApi", url = "${ms.internal.customer.url}")
public interface CustomerMicroServiceClientApi {

    @GetMapping(value = "/v1/totem/customer/{cpf}", produces = "application/json")
    ResponseEntity<CustomerResponse> getCustomerByCpf(@PathVariable("cpf") String cpf);

}
