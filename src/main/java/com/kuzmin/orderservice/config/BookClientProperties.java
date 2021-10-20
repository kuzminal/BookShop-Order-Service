package com.kuzmin.orderservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;
import java.net.URI;

@ConfigurationProperties(prefix = "bookshop")
@Data
public class BookClientProperties {
    @NotNull
    private URI catalogServiceUrl;
}
