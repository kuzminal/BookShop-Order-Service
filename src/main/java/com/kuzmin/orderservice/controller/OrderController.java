package com.kuzmin.orderservice.controller;

import com.kuzmin.orderservice.domain.order.Order;
import com.kuzmin.orderservice.domain.order.dto.OrderRequest;
import com.kuzmin.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public Flux<Order> getAllOrders(@AuthenticationPrincipal Jwt jwt) {
        return orderService.getAllOrders(jwt.getSubject());
    }

    @GetMapping("{id}")
    public Mono<Order> getOrderById(@PathVariable Long id) {
        return orderService.getOrder(id);
    }

    @PostMapping
    public Mono<Order> submitOrder(@RequestBody @Valid OrderRequest orderRequest) {
        return orderService.submitOrder(orderRequest.getIsbn(), orderRequest.getQuantity());
    }

    @DeleteMapping("{id}")
    public Mono<Void> deleteOrderById(@PathVariable Long id) {
        return orderService.deleteOrder(id);
    }
}
