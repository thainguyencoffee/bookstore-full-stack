package com.bookstore.backend.orders.exception;

import com.bookstore.backend.orders.OrderStatus;

import java.util.UUID;

public class OrderStatusNotMatchException extends RuntimeException {
    public OrderStatusNotMatchException(UUID orderId, OrderStatus expectedStatus, OrderStatus actualStatus) {
        super(String.format("Order %s status must be %s, not %s", orderId, expectedStatus, actualStatus));
    }
}
