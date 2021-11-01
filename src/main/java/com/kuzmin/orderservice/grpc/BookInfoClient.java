package com.kuzmin.orderservice.grpc;

import bookcatalogue.BookCatalogueGrpc;
import bookcatalogue.ProductInfo;
import com.kuzmin.orderservice.domain.book.dto.Book;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class BookInfoClient {
    @Value("${grpc.port:50051}")
    private int gRpcPort = 0;

    @Value("${grpc.host:localhost}")
    private String gRpcHost = "";

    public Mono<Book> getBook(String isbn) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(gRpcHost, gRpcPort).usePlaintext()
                .build();
        BookCatalogueGrpc.BookCatalogueBlockingStub stub =
                BookCatalogueGrpc.newBlockingStub(channel);
        System.out.println(isbn);
        ProductInfo.BookIsbn bookIsbn = ProductInfo.BookIsbn.newBuilder().setIsbn(isbn).build();
        try {
            ProductInfo.Book product = stub.getBook(bookIsbn);
            System.out.println(product.toString());
            channel.shutdown();
            Book book = new Book();
            book.setIsbn(product.getIsbn());
            book.setAuthor(product.getAuthor());
            book.setTitle(product.getTitle());
            book.setPrice(product.getPrice());
            book.setQuantity(product.getQuantity());
            return Mono.just(book);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().equals(Status.NOT_FOUND)) {
                return Mono.empty();
            } else throw e;
        }
    }
}
