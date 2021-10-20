package com.kuzmin.orderservice.repository;

import com.kuzmin.orderservice.domain.order.Order;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface OrderRepository extends ReactiveCrudRepository<Order,Long> {
}
