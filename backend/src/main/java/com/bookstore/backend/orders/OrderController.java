package com.bookstore.backend.orders;

import com.bookstore.backend.orders.dto.OrderRequest;
import com.bookstore.backend.orders.dto.PaymentUrlDto;
import com.bookstore.backend.orders.vnpay.VNPayService;
import com.bookstore.backend.orders.vnpay.VNPayStatusCodeEnum;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(path = "api/orders", produces = "application/json")
@RequiredArgsConstructor
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;
    private final VNPayService vnPayService;

    @GetMapping
    public List<Order> getAllOrder(@AuthenticationPrincipal Jwt jwt, Pageable pageable) {
        log.info("OrderController attempt retrieve orders.");
        String username = jwt.getClaim(StandardClaimNames.PREFERRED_USERNAME).toString();
        return orderService.findAllByCreatedBy(username, pageable).getContent();
    }

    @GetMapping("/{orderId}")
    public Order getOrderById(@PathVariable UUID orderId) {
        log.info("OrderController attempt retrieve order by id.");
        return orderService.findById(orderId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order submitOrder(@Valid @RequestBody OrderRequest orderRequest) {
        return orderService.submitOrder(orderRequest.getLineItems(), orderRequest.getUserInformation(), orderRequest.getPaymentMethod());
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
        URI paymentDetailUrl = URI.create("http://localhost:9000/payment-callback-detail/" + order.getId());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(paymentDetailUrl);
        return new ResponseEntity<>(httpHeaders, HttpStatus.FOUND);
    }
}
