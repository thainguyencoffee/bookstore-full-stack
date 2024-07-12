package com.bookstore.backend.orders;

import com.bookstore.backend.core.email.EmailService;
import com.bookstore.backend.orders.dto.OrderRequest;
import com.bookstore.backend.orders.dto.OrderUpdateDto;
import com.bookstore.backend.orders.dto.OtpRequestDto;
import com.bookstore.backend.orders.dto.PaymentUrlDto;
import com.bookstore.backend.orders.exception.OrderStatusNotMatchException;
import com.bookstore.backend.orders.vnpay.VNPayService;
import com.bookstore.backend.orders.vnpay.VNPayStatusCodeEnum;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(path = "api/orders", produces = "application/json")
@RequiredArgsConstructor
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;
    private final VNPayService vnPayService;
    private final EmailService emailService;

    @GetMapping
    public Page<Order> getAllOrder(@AuthenticationPrincipal Jwt jwt, Pageable pageable) {
        log.info("OrderController attempt retrieve orders.");
        String username = jwt.getClaim(StandardClaimNames.PREFERRED_USERNAME).toString();
        return orderService.findAllByCreatedBy(username, pageable);
    }

    @GetMapping("/{orderId}")
    public Order getOrderById(@PathVariable UUID orderId) {
        log.info("OrderController attempt retrieve order by id.");
        return orderService.findById(orderId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order submitOrder(@Valid @RequestBody OrderRequest orderRequest) {
        return orderService.submitOrder(orderRequest);
    }

    @GetMapping("/{orderId}/send-opt")
    public ResponseEntity<?> verifyOrder(@PathVariable UUID orderId) {
        log.info("OrderController attempt verify order by id.");
        Order order = orderService.createOtp(orderId);
        if (order.getStatus().equals(OrderStatus.WAITING_FOR_ACCEPTANCE)) {
            String body = EmailService.buildEmailVerifyBody(order.getOtp());
            emailService.sendConfirmationEmail(order.getUserInformation().getEmail(), "Xác thực đơn hàng", body);
            return ResponseEntity.ok().build();
        }
        throw new OrderStatusNotMatchException(orderId, OrderStatus.WAITING_FOR_ACCEPTANCE, order.getStatus());
    }

    @PostMapping("/{orderId}/verify-otp")
    public Order verifyOtp(@PathVariable UUID orderId, @RequestBody OtpRequestDto otpRequestDto) {
        log.info("OrderController attempt verify otp by id.");
        return orderService.verifyOtp(orderId, otpRequestDto.getOtp());
    }

    @PatchMapping("/{orderId}")
    public Order updateOrder(@PathVariable UUID orderId, @RequestBody OrderUpdateDto orderUpdateDto) {
        log.info("OrderController attempt update order by id. {}", orderUpdateDto.getUserInformation().toString());
        return orderService.updateOrder(orderId, orderUpdateDto);
    }

    @PostMapping("/{orderId}/payment")
    @ResponseStatus(HttpStatus.OK)
    public PaymentUrlDto pay(HttpServletRequest req, @PathVariable UUID orderId) {
        log.info("VNPayController attempt generate to paymentUrl with (orderId={}, req: {})", orderId, req);
        String paymentUrl = vnPayService.generatePaymentUrl(req, orderId);
        orderService.buildOrderWithStatus(orderId, OrderStatus.WAITING_FOR_PAYMENT);
        log.info("VNPayController result of processing to generate to paymentUrl is (result={}, paymentUrl={})",
                paymentUrl != null ? "true" : "false", paymentUrl);
        return new PaymentUrlDto(paymentUrl);
    }

    @GetMapping("/payment/return")
    public ResponseEntity<Void> payCallbackHandler(HttpServletRequest request) {
        log.info("VNPayController attempt to handle payment callback with (request={})", request);
        String status = request.getParameter("vnp_ResponseCode");
        log.info("status is {}", status);
        Order order = Optional.ofNullable(VNPayStatusCodeEnum.isMember(status))
                .map(vnPayStatusCodeEnum -> vnPayStatusCodeEnum.handleCallback(orderService, request))
                .orElseThrow(() -> new RuntimeException("Status code of VNPAY not matched!!!"));
        URI paymentDetailUrl = URI.create("http://localhost:9000/order-detail-callback/" + order.getId());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(paymentDetailUrl);
        return new ResponseEntity<>(httpHeaders, HttpStatus.FOUND);
    }
}
