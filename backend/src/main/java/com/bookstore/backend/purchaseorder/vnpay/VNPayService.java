package com.bookstore.backend.purchaseorder.vnpay;

import com.bookstore.backend.purchaseorder.Order;
import com.bookstore.backend.purchaseorder.OrderService;
import com.bookstore.backend.purchaseorder.OrderStatus;
import com.bookstore.backend.purchaseorder.exception.ConsistencyDataException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${bookstore.vnPay.api-url}")
    private String vnp_PayUrl;
    @Value("${bookstore.vnPay.return-url}")
    private String vnp_ReturnUrl;
    @Value("${bookstore.vnPay.tmn-code}")
    private String vnp_TmnCode ;
    @Value("${bookstore.vnPay.secret-key}")
    private String secret_Key;
    @Value("${bookstore.vnPay.version}")
    private String vnp_Version;
    @Value("${bookstore.vnPay.command}")
    private String vnp_Command;
    @Value("${bookstore.vnPay.order-type}")
    private String order_Type;

    public String generatePaymentUrl(HttpServletRequest request, UUID orderId) {
        Order order = orderService.findById(orderId);
        if (!order.getStatus().equals(OrderStatus.WAITING_FOR_PAYMENT)
                && !order.getStatus().equals(OrderStatus.PAYMENT_FAILED)) {
            throw new ConsistencyDataException("Order is not waiting for payment");
        }
        long finalPrice = order.getTotalPrice() * 100L;
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
        String vnpSecureHash = VNPayUtils.hmacSHA512(secret_Key, hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnp_PayUrl + "?" + queryUrl;
        return paymentUrl;
    }

    private Map<String, String> getVNPayPayload(UUID orderId) {
        var vnpParamsMap = new HashMap<String, String>();
        vnpParamsMap.put("vnp_Version", vnp_Version);
        vnpParamsMap.put("vnp_Command", vnp_Command);
        vnpParamsMap.put("vnp_TmnCode", vnp_TmnCode);
        vnpParamsMap.put("vnp_CurrCode", "VND");
        vnpParamsMap.put("vnp_TxnRef",  orderId.toString());
        vnpParamsMap.put("vnp_OrderInfo", "Thanh toan don hang:" +  orderId.toString());
        vnpParamsMap.put("vnp_OrderType", order_Type);
        vnpParamsMap.put("vnp_Locale", "vn");
        vnpParamsMap.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnpParamsMap.putAll(getTime());
        return vnpParamsMap;
    }

    private static Map<String, String> getTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        // Lấy thời gian hiện tại (ZonedDateTime) tại múi giờ "Asia/Ho_Chi_Minh"
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));

        String vnpCreateDate = formatter.format(now);

        // Tính thời gian hết hạn sau 15 phút (ZonedDateTime)
        ZonedDateTime fifteenMinutesLater = now.plusMinutes(15);
        String vnpExpireDate = formatter.format(fifteenMinutesLater);

        Map<String, String> vnpParamsMap = new HashMap<>();
        vnpParamsMap.put("vnp_CreateDate", vnpCreateDate);
        vnpParamsMap.put("vnp_ExpireDate", vnpExpireDate);
        return vnpParamsMap;
    }
}

