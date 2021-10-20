package com.kuzmin.orderservice.service;

import com.kuzmin.orderservice.client.BookClient;
import com.kuzmin.orderservice.domain.book.dto.Book;
import com.kuzmin.orderservice.domain.order.Order;
import com.kuzmin.orderservice.domain.order.OrderStatus;
import com.kuzmin.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final BookClient bookClient;
    private final OrderRepository orderRepository;

    public Flux<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Mono<Order> getOrder(Long id) {
        return orderRepository.findById(id);
    }

    public Mono<Order> submitOrder(String isbn, int quantity) {
        return bookClient.getBookByIsbn(isbn)
                .flatMap(book -> Mono.just(buildAcceptedOrder(book, quantity)))
                .defaultIfEmpty(buildRejectedOrder(isbn, quantity))
                .flatMap(orderRepository::save);
    }

    private Order buildAcceptedOrder(Book book, int quantity) {
        return new Order(book.getIsbn(),
                book.getTitle() + " - " + book.getAuthor(),
                book.getPrice(), quantity, OrderStatus.ACCEPTED);
    }

    private Order buildRejectedOrder(String isbn, int quantity) {
        return new Order(isbn, quantity, OrderStatus.REJECTED);
    }
}
