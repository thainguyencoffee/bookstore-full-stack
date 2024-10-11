package com.bookstorefullstack.bookstore.purchaseorder.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record PaymentRequest(
        @NotNull(message = "PurchaseOrder ID is required")
        UUID orderId
) {
}
