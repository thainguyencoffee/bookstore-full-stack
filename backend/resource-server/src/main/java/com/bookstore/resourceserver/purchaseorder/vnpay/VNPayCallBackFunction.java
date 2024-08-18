package com.bookstore.resourceserver.purchaseorder.vnpay;

import com.bookstore.resourceserver.purchaseorder.PurchaseOrder;
import com.bookstore.resourceserver.purchaseorder.OrderService;

import jakarta.servlet.http.HttpServletRequest;

@FunctionalInterface
public interface VNPayCallBackFunction {
    PurchaseOrder handle(OrderService orderService, HttpServletRequest request);
}
