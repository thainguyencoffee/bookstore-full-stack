package com.bookstore.resourceserver.purchaseorder.vnpay;

import com.bookstore.resourceserver.core.config.BookstoreProperties;
import com.bookstore.resourceserver.purchaseorder.Order;
import com.bookstore.resourceserver.purchaseorder.OrderService;
import com.bookstore.resourceserver.purchaseorder.OrderStatus;
import com.bookstore.resourceserver.core.exception.purchaseorder.OrderStatusNotMatchException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VNPayService {

    private final OrderService orderService;
    private final BookstoreProperties bookstoreProperties;

    public String generatePaymentUrl(HttpServletRequest request, UUID orderId) {
        Order order = orderService.findById(orderId);
        if (!order.getStatus().equals(OrderStatus.WAITING_FOR_PAYMENT)
                && !order.getStatus().equals(OrderStatus.PAYMENT_FAILED)) {
            throw new OrderStatusNotMatchException(orderId, order.getStatus(), OrderStatus.WAITING_FOR_PAYMENT, OrderStatus.PAYMENT_FAILED);
        }
        long finalPrice = order.getTotalPrice().getDiscountedPrice() * 100L;
        String bankCode = request.getParameter("bankCode");
        Map<String, String> vnpParamsMap = getVNPayPayload(orderId);
        vnpParamsMap.put("vnp_Amount", String.valueOf(finalPrice));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_IpAddr", VNPayUtils.getIpAddress(request));
        //build query url
        String queryUrl = VNPayUtils.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtils.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtils.hmacSHA512(bookstoreProperties.vnPay().secretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        return bookstoreProperties.vnPay().apiUrl() + "?" + queryUrl;
    }

    private Map<String, String> getVNPayPayload(UUID orderId) {
        var vnpParamsMap = new HashMap<String, String>();
        vnpParamsMap.put("vnp_Version", bookstoreProperties.vnPay().version());
        vnpParamsMap.put("vnp_Command", bookstoreProperties.vnPay().command());
        vnpParamsMap.put("vnp_TmnCode", bookstoreProperties.vnPay().tmnCode());
        vnpParamsMap.put("vnp_CurrCode", "VND");
        vnpParamsMap.put("vnp_TxnRef",  orderId.toString());
        vnpParamsMap.put("vnp_OrderInfo", "Thanh toan don hang:" + orderId);
        vnpParamsMap.put("vnp_OrderType", bookstoreProperties.vnPay().orderType());
        vnpParamsMap.put("vnp_Locale", "vn");
        vnpParamsMap.put("vnp_ReturnUrl", bookstoreProperties.vnPay().returnUrl());
        vnpParamsMap.putAll(getTime());
        return vnpParamsMap;
    }

    private static Map<String, String> getTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        String vnpCreateDate = formatter.format(now);
        ZonedDateTime fifteenMinutesLater = now.plusMinutes(15);
        String vnpExpireDate = formatter.format(fifteenMinutesLater);

        Map<String, String> vnpParamsMap = new HashMap<>();
        vnpParamsMap.put("vnp_CreateDate", vnpCreateDate);
        vnpParamsMap.put("vnp_ExpireDate", vnpExpireDate);
        return vnpParamsMap;
    }
}

