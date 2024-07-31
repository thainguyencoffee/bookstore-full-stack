package com.bookstore.backend.core.exception.purchaseorder;

import java.util.UUID;

public class OtpExpiredException extends RuntimeException {
    public OtpExpiredException(UUID orderId) {
        super("Order with id " + orderId + " has expired OTP");
    }
}
