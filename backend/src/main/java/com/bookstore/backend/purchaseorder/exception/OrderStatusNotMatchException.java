package com.bookstore.backend.purchaseorder.exception;

import com.bookstore.backend.purchaseorder.OrderStatus;

import java.util.UUID;

public class OrderStatusNotMatchException extends RuntimeException {
    public OrderStatusNotMatchException(UUID orderId, OrderStatus expectedStatus, OrderStatus actualStatus) {
        super(String.format("Order %s status must be %s, not %s", orderId, expectedStatus, actualStatus));
    }
}
