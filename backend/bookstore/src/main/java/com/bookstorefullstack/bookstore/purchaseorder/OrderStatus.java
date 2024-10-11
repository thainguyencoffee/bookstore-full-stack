package com.bookstorefullstack.bookstore.purchaseorder;

public enum OrderStatus {
    ACCEPTED,
    WAITING_FOR_PAYMENT,
    WAITING_FOR_ACCEPTANCE,
    REJECTED,
    DISPATCHED,
    CANCELLED,
    PAYMENT_FAILED,
}
