package com.kuzmin.orderservice.service;

import com.kuzmin.orderservice.domain.book.dto.Book;
import com.kuzmin.orderservice.domain.order.Order;
import com.kuzmin.orderservice.domain.order.OrderStatus;
import com.kuzmin.orderservice.grpc.BookInfoClient;
import com.kuzmin.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final BookInfoClient bookInfoClient;
    private final OrderRepository orderRepository;

    public Mono<Order> getOrder(Long id) {
        return orderRepository.findById(id);
    }

    public Mono<Order> submitOrder(String isbn, int quantity) {
        return bookInfoClient.getBook(isbn)
                .flatMap(book ->
                {
                    if (book.getQuantity() >= quantity) {
                        return Mono.just(buildAcceptedOrder(book, quantity));
                    } else {
                        return Mono.just(buildRejectedOrder(book));
                    }
                })
                .defaultIfEmpty(buildRejectedOrder(isbn, quantity))
                .flatMap(orderRepository::save);
    }

    public void updateOrderStatus(Long orderId, OrderStatus status) {
        orderRepository.findById(orderId)
                .map(existingOrder ->
                        new Order(
                                existingOrder.bookIsbn(),
                                existingOrder.bookName(),
                                existingOrder.bookPrice(),
                                existingOrder.quantity(),
                                status,
                                existingOrder.id(),
                                existingOrder.createdDate(),
                                existingOrder.lastModifiedDate(),
                                existingOrder.createdBy(),
                                existingOrder.lastModifiedBy(),
                                existingOrder.version()
                        ))
                .flatMap(orderRepository::save)
                .subscribe();
    }

    public Flux<Order> getAllOrders(String userId) {
        return orderRepository.findAllByCreatedBy(userId);
    }

    public Mono<Void> deleteOrder(Long id) {
        return getOrder(id).flatMap(orderRepository::delete);
    }

    private Order buildAcceptedOrder(Book book, int quantity) {
        return Order.build(book.getIsbn(),
                book.getTitle() + " - " + book.getAuthor(),
                book.getPrice(), quantity, OrderStatus.ACCEPTED);
    }

    public static Order buildRejectedOrder(String isbn, int quantity) {
        return Order.build(isbn, quantity, OrderStatus.REJECTED);
    }

    public Order buildRejectedOrder(Book book) {
        return Order.build(book.getIsbn(), book.getTitle(), book.getPrice(), book.getQuantity(), OrderStatus.REJECTED);
    }
}
