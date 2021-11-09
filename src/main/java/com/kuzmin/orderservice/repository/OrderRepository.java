package com.kuzmin.orderservice.repository;

import com.kuzmin.orderservice.domain.order.Order;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface OrderRepository extends ReactiveCrudRepository<Order,Long> {
    Flux<Order> findAllByCreatedBy(String userId);
}
