package com.totem.food.framework.adapters.out.web.internal.order.client;

import com.totem.food.application.ports.out.internal.order.OrderResponseRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "OrderMicroServiceClientApi", url = "${ms.internal.order.url}")
public interface OrderMicroServiceClientApi {

    @GetMapping(value = "/v1/totem/order/{id}", produces = "application/json")
    ResponseEntity<OrderResponseRequest> getOrderById(@PathVariable("id") String id);

    @PutMapping(value = "/v1/totem/order/{orderId}/status/{statusName}", produces = "application/json")
    ResponseEntity<OrderResponseRequest> updateOrder(@PathVariable(name = "orderId") String id, @PathVariable(name = "statusName") String status);

}
