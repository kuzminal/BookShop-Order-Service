package com.kuzmin.orderservice.domain.order;

import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Table;

@Table("orders")
public record Order(
        String bookIsbn,
        String bookName,
        Double bookPrice,
        Integer quantity,
        OrderStatus status,
        @Id
        Long id,
        @CreatedDate
        Long createdDate,
        @LastModifiedDate
        Long lastModifiedDate,
        @CreatedBy
        String createdBy,
        @LastModifiedBy
        String lastModifiedBy,
        @Version
        int version
) {
    public static Order build(String isbn, int quantity, OrderStatus rejected) {
        return new Order(isbn, null, null, quantity, rejected,
                null, null, null, null, null, 0);
    }

    public static Order build(String bookIsbn, String bookName, Double bookPrice,
                              Integer quantity, OrderStatus status) {
        return new Order(bookIsbn, bookName, bookPrice, quantity, status,
                null, null, null, null, null, 0);
    }
}
