package com.bookstore.backend.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bookstore")
public record BookstoreProperties(
        VNPayProperties vnPay,
        String edgeUrl
) {
    public record VNPayProperties(
            String apiUrl,
            String tmnCode,
            String secretKey,
            String returnUrl,
            String version,
            String command,
            String orderType
    ) {}

}
