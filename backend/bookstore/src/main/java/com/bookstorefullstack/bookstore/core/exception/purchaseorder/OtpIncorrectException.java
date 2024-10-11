package com.bookstorefullstack.bookstore.core.exception.purchaseorder;

import java.util.UUID;

public class OtpIncorrectException extends RuntimeException {
    public OtpIncorrectException(UUID orderId) {
        super("Otp is incorrect for order with id: " + orderId);
    }
}
