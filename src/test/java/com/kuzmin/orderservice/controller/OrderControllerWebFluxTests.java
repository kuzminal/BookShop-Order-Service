package com.kuzmin.orderservice.controller;

import com.kuzmin.orderservice.domain.order.Order;
import com.kuzmin.orderservice.domain.order.OrderStatus;
import com.kuzmin.orderservice.domain.order.dto.OrderRequest;
import com.kuzmin.orderservice.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@WebFluxTest(OrderController.class)
public class OrderControllerWebFluxTests {
    @Autowired
    private WebTestClient webClient;
    @MockBean
    private OrderService orderService;
    @MockBean
    ReactiveJwtDecoder reactiveJwtDecoder;

    @Test
    void whenBookNotAvailableThenRejectOrder() {
        var orderRequest = new OrderRequest("1234567890", 3);
        var expectedOrder = OrderService.buildRejectedOrder(orderRequest.getIsbn(),
                orderRequest.getQuantity());
        given(orderService.submitOrder(orderRequest.getIsbn(), orderRequest.getQuantity()))
                .willReturn(Mono.just(expectedOrder));
        webClient
                .mutateWith(mockJwt()
                        .authorities(new SimpleGrantedAuthority("ROLE_customer")))
                .mutateWith(csrf())
                .post()
                .uri("/orders/")
                .bodyValue(orderRequest)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(Order.class).value(actualOrder -> {
                    assertThat(actualOrder).isNotNull();
                    assertThat(actualOrder.status()).isEqualTo(OrderStatus.REJECTED);
                });
    }
}
