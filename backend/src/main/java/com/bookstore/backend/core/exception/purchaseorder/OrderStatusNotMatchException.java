package com.bookstore.backend.core.exception.purchaseorder;

import com.bookstore.backend.purchaseorder.OrderStatus;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class OrderStatusNotMatchException extends RuntimeException {

    public OrderStatusNotMatchException(UUID orderId, OrderStatus actualStatus, OrderStatus ... expectedStatus) {
        super(String.format("Order %s status must be %s, not %s",
                orderId,
                buildExpectedStatus(Arrays.stream(expectedStatus).toList()),
                actualStatus));
    }

    private static String buildExpectedStatus(List<OrderStatus> expectedStatus) {
        StringBuilder builder = new StringBuilder();
        for (OrderStatus status : expectedStatus) {
            builder.append(status);
            builder.append(", ");
        }
        return builder.substring(0, builder.length() - 2);
    }

}
