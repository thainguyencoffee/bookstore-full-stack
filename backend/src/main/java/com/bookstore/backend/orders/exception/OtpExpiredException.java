package com.bookstore.backend.orders.exception;

import java.util.UUID;

public class OtpExpiredException extends RuntimeException {
    public OtpExpiredException(UUID orderId) {
        super("Order with id " + orderId + " has expired OTP");
    }
}
