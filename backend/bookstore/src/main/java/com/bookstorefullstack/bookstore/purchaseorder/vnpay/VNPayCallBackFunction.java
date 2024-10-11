package com.bookstorefullstack.bookstore.purchaseorder.vnpay;

import com.bookstorefullstack.bookstore.purchaseorder.PurchaseOrder;
import com.bookstorefullstack.bookstore.purchaseorder.OrderService;

import jakarta.servlet.http.HttpServletRequest;

@FunctionalInterface
public interface VNPayCallBackFunction {
    PurchaseOrder handle(OrderService orderService, HttpServletRequest request);
}
