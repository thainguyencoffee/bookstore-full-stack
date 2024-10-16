package com.bookstorefullstack.bookstore.purchaseorder.web;

import com.bookstorefullstack.bookstore.core.email.EmailService;
import com.bookstorefullstack.bookstore.purchaseorder.PurchaseOrder;
import com.bookstorefullstack.bookstore.purchaseorder.OrderService;
import com.bookstorefullstack.bookstore.purchaseorder.OrderStatus;
import com.bookstorefullstack.bookstore.purchaseorder.dto.OtpRequestDto;
import com.bookstorefullstack.bookstore.core.exception.purchaseorder.OrderStatusNotMatchException;
import com.bookstorefullstack.bookstore.purchaseorder.web.user.OrderController;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("email")
@RequiredArgsConstructor
public class EmailController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;
    private final EmailService emailService;

    @GetMapping("/orders/{orderId}/send-opt")
    public ResponseEntity<?> verifyOrder(@PathVariable UUID orderId) {
        log.info("EmailController attempt verify purchaseOrder by id.");
        PurchaseOrder purchaseOrder = orderService.createOtp(orderId);
        if (purchaseOrder.getStatus().equals(OrderStatus.WAITING_FOR_ACCEPTANCE)) {
            String body = EmailService.buildEmailVerifyBody(purchaseOrder.getOtp());
            emailService.sendConfirmationEmail(purchaseOrder.getUserInformation().getEmail(), "Xác thực đơn hàng", body);
            return ResponseEntity.ok().build();
        }
        throw new OrderStatusNotMatchException(orderId, OrderStatus.WAITING_FOR_ACCEPTANCE, purchaseOrder.getStatus());
    }

    @PostMapping("/orders/{orderId}/verify-otp")
    public PurchaseOrder verifyOtp(@PathVariable UUID orderId, @RequestBody OtpRequestDto otpRequestDto) {
        log.info("OrderController attempt verify otp by id.");
        return orderService.verifyOtp(orderId, otpRequestDto.getOtp());
    }

}
