package com.bookstore.resourceserver.purchaseorder.vnpay;

import com.bookstore.resourceserver.purchaseorder.Order;
import com.bookstore.resourceserver.purchaseorder.OrderService;

import jakarta.servlet.http.HttpServletRequest;

@FunctionalInterface
public interface VNPayCallBackFunction {
    Order handle(OrderService orderService, HttpServletRequest request);
}
