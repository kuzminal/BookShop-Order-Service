package com.kuzmin.orderservice.client;

import com.kuzmin.orderservice.config.BookClientProperties;
import com.kuzmin.orderservice.domain.book.dto.Book;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class BookClient {
    private final WebClient webClient;

    public BookClient(BookClientProperties bookClientProperties, WebClient.Builder
            webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl(bookClientProperties.getCatalogServiceUrl().toString())
                .build();
    }

    public Mono<Book> getBookByIsbn(String isbn) {
        return webClient.get().uri(isbn)
                .retrieve()
                .bodyToMono(Book.class);
    }
}
