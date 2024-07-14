package com.bookstore.backend.purchaseorder.vnpay;

import com.bookstore.backend.purchaseorder.Order;
import com.bookstore.backend.purchaseorder.OrderService;

import javax.servlet.http.HttpServletRequest;

@FunctionalInterface
public interface VNPayCallBackFunction {
    Order handle(OrderService orderService, HttpServletRequest request);
}
