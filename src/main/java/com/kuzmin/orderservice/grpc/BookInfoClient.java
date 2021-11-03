package com.kuzmin.orderservice.grpc;

import bookcatalogue.BookCatalogueGrpc;
import bookcatalogue.ProductInfo;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.kuzmin.orderservice.domain.book.dto.Book;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class BookInfoClient {
    private BookCatalogueGrpc.BookCatalogueBlockingStub stub;
    private ManagedChannel channel;
    private static final Logger LOG = LoggerFactory.getLogger(BookInfoClient.class);

    @Value("${grpc.host:localhost}")
    private String gRpcHost;

    @Value("${grpc.port:50051}")
    private int gRpcPort;

    protected Map<String, ?> getRetryingServiceConfig() {
        return new Gson()
                .fromJson(
                        new JsonReader(
                                new InputStreamReader(
                                        Objects.requireNonNull(BookInfoClient.class.getResourceAsStream(
                                                "retrying_service_config.json")),
                                        UTF_8)),
                        Map.class);
    }

    public Mono<Book> getBook(String isbn) {
        ProductInfo.BookIsbn bookIsbn = ProductInfo.BookIsbn.newBuilder().setIsbn(isbn).build();
        try {
            ProductInfo.Book product = stub.getBook(bookIsbn);
            return Mono.just(mapProductToBook(product));
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode().equals(Status.NOT_FOUND.getCode())
                    || e.getStatus().getCode().equals(Status.UNAVAILABLE.getCode())) {
                return Mono.empty();
            } else throw e;
        }
    }

    private Book mapProductToBook(ProductInfo.Book product) {
        Book book = new Book();
        book.setIsbn(product.getIsbn());
        book.setAuthor(product.getAuthor());
        book.setTitle(product.getTitle());
        book.setPrice(product.getPrice());
        book.setQuantity(product.getQuantity());
        return book;
    }

    public void start() {
        channel = ManagedChannelBuilder
                .forAddress(gRpcHost, gRpcPort).usePlaintext()
                .disableServiceConfigLookUp()
                .defaultServiceConfig(getRetryingServiceConfig())
                .enableRetry()
                .build();

        stub = BookCatalogueGrpc.newBlockingStub(channel);
        LOG.info("gRPC client connected to {}:{}", gRpcHost, gRpcPort);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
        LOG.info("gRPC client disconnected successfully.");
    }
}
