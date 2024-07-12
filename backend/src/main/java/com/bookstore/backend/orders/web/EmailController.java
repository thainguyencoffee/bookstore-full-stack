package com.bookstore.backend.orders.web;

import com.bookstore.backend.core.email.EmailService;
import com.bookstore.backend.orders.Order;
import com.bookstore.backend.orders.OrderService;
import com.bookstore.backend.orders.OrderStatus;
import com.bookstore.backend.orders.dto.OtpRequestDto;
import com.bookstore.backend.orders.exception.OrderStatusNotMatchException;
import com.bookstore.backend.orders.web.user.OrderController;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/email/")
@RequiredArgsConstructor
public class EmailController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;
    private final EmailService emailService;

    @GetMapping("/orders/{orderId}/send-opt")
    public ResponseEntity<?> verifyOrder(@PathVariable UUID orderId) {
        log.info("EmailController attempt verify order by id.");
        Order order = orderService.createOtp(orderId);
        if (order.getStatus().equals(OrderStatus.WAITING_FOR_ACCEPTANCE)) {
            String body = EmailService.buildEmailVerifyBody(order.getOtp());
            emailService.sendConfirmationEmail(order.getUserInformation().getEmail(), "Xác thực đơn hàng", body);
            return ResponseEntity.ok().build();
        }
        throw new OrderStatusNotMatchException(orderId, OrderStatus.WAITING_FOR_ACCEPTANCE, order.getStatus());
    }

    @PostMapping("/orders/{orderId}/verify-otp")
    public Order verifyOtp(@PathVariable UUID orderId, @RequestBody OtpRequestDto otpRequestDto) {
        log.info("OrderController attempt verify otp by id.");
        return orderService.verifyOtp(orderId, otpRequestDto.getOtp());
    }

}
