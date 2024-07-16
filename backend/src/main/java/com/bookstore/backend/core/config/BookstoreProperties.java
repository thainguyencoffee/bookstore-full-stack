package com.bookstore.backend.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bookstore")
public record BookstoreProperties(
        CloudinaryProperties cloudinary,
        VNPayProperties vnPay,
        String gatewayUrl
) {
    public record CloudinaryProperties(
            String cloudName,
            String apiKey,
            String apiSecret
    ) {}

    record VNPayProperties(
            String apiUrl,
            String tmnCode,
            String secretKey,
            String returnUrl,
            String version,
            String command,
            String orderType
    ) {}

}
