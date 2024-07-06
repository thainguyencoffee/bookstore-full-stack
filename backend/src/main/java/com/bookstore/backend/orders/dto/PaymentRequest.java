package com.bookstore.backend.orders.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

public record PaymentRequest(
        @NotNull(message = "Order ID is required")
        UUID orderId,
        @NotBlank(message = "Bank code is required")
        String bankCode
) {
}
