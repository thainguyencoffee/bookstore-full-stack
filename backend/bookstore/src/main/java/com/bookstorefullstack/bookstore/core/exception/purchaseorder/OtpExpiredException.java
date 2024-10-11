package com.bookstorefullstack.bookstore.core.exception.purchaseorder;

import java.util.UUID;

public class OtpExpiredException extends RuntimeException {
    public OtpExpiredException(UUID orderId) {
        super("PurchaseOrder with id " + orderId + " has expired OTP");
    }
}
