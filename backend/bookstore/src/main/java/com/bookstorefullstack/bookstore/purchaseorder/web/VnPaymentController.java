package com.bookstorefullstack.bookstore.purchaseorder.web;

import com.bookstorefullstack.bookstore.core.config.BookstoreProperties;
import com.bookstorefullstack.bookstore.purchaseorder.PurchaseOrder;
import com.bookstorefullstack.bookstore.purchaseorder.OrderService;
import com.bookstorefullstack.bookstore.purchaseorder.OrderStatus;
import com.bookstorefullstack.bookstore.purchaseorder.dto.PaymentRequest;
import com.bookstorefullstack.bookstore.purchaseorder.dto.PaymentUrlDto;
import com.bookstorefullstack.bookstore.purchaseorder.vnpay.VNPayService;
import com.bookstorefullstack.bookstore.purchaseorder.vnpay.VNPayStatusCodeEnum;
import com.bookstorefullstack.bookstore.purchaseorder.web.user.OrderController;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("payment/vn-pay")
@RequiredArgsConstructor
public class VnPaymentController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;
    private final VNPayService vnPayService;
    private final BookstoreProperties bookstoreProperties;

    @PostMapping("/payment-url")
    @ResponseStatus(HttpStatus.OK)
    public PaymentUrlDto pay(HttpServletRequest req, @RequestBody PaymentRequest paymentRequest) {
        log.info("VnPaymentController attempt generate to paymentUrl with (orderId={}, req: {})", paymentRequest.orderId(), req);
        String paymentUrl = vnPayService.generatePaymentUrl(req, paymentRequest.orderId());
        orderService.buildOrderWithStatus(paymentRequest.orderId(), OrderStatus.WAITING_FOR_PAYMENT);
        log.info("VNPayController result of processing to generate to paymentUrl is (result={}, paymentUrl={})",
                paymentUrl != null ? "true" : "false", paymentUrl);
        return new PaymentUrlDto(paymentUrl);
    }

    // callback
    @GetMapping("/return")
    public ResponseEntity<Void> payCallbackHandler(HttpServletRequest request) {
        log.info("VnPaymentController attempt to handle payment callback with (request={})", request);
        String status = request.getParameter("vnp_ResponseCode");
        log.info("status is {}", status);
        PurchaseOrder order = Optional.ofNullable(VNPayStatusCodeEnum.isMember(status))
                .map(vnPayStatusCodeEnum -> vnPayStatusCodeEnum.handleCallback(orderService, request))
                .orElseThrow(() -> new RuntimeException("Status code of VNPAY not matched!!!"));
        URI paymentDetailUrl = URI.create(bookstoreProperties.reverseProxyUrl() + "/order-detail-callback/" + order.getId());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(paymentDetailUrl);
        return new ResponseEntity<>(httpHeaders, HttpStatus.FOUND);
    }

}
