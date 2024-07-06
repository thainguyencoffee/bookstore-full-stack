package com.bookstore.backend.orders.vnpay;

import com.bookstore.backend.orders.Order;
import com.bookstore.backend.orders.OrderService;

import javax.servlet.http.HttpServletRequest;

@FunctionalInterface
public interface VNPayCallBackFunction {
    Order handle(OrderService orderService, HttpServletRequest request);
}
