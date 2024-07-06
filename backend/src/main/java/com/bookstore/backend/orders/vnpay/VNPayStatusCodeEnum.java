package com.bookstore.backend.orders.vnpay;


import com.bookstore.backend.orders.Order;
import com.bookstore.backend.orders.OrderService;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

public enum VNPayStatusCodeEnum {
    /**
     * Giao dịch thành công
     */
    SUCCESS("00", (orderService, request) ->
            orderService.buildAcceptedOrder(UUID.fromString(request.getParameter("vnp_TxnRef")))
    ),

    /**
     * Giao dịch chưa hoàn tất
     */
    PENDING("01", (orderService, request) -> null),

    /**
     * Giao dịch bị lỗi
     */
    FAILED("02", (orderService, request) -> null),

    /**
     * Giao dịch đảo (Khách hàng đã bị trừ tiền tại Ngân hàng nhưng GD chưa thành công ở VNPAY)
     * */
    REVERSED("03", (orderService, request) -> null),

    /**
     * VNPAY đang xử lý giao dịch này (GD hoàn tiền)
     * */
    REFUND_PROCESSING("04", (orderService, request) -> null),

    /**
     * VNPAY đã gửi yêu cầu hoàn tiền sang Ngân hàng (GD hoàn tiền)
     * */
    REFUND_REQUESTED("05", (orderService, request) -> null),

    /**
     * Trừ tiền thành công. Giao dịch bị nghi ngờ (liên quan tới lừa đảo, giao dịch bất thường).
     * */
    SUSPECTED_FRAUD("07", (orderService, request) -> null),

    /**
     * GD Hoàn trả bị từ chối
     * */
    REFUND_DENIED("08", (orderService, request) -> null),

    /**
     * Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng.
     * */
    CARD_NOT_REGISTERED("09", (orderService, request) -> null),

    /**
     * Giao dịch không thành công do: Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần
     * */
    CARD_AUTHENTICATION_FAILED("10", (orderService, request) -> null),

    /**
     * Giao dịch không thành công do: Đã hết hạn chờ thanh toán. Xin quý khách vui lòng thực hiện lại giao dịch.
     * */
    EXPIRED("11", (orderService, request) -> null),

    /**
     * Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng bị khóa.
     * */
    CARD_LOCKED("12", (orderService, request) -> null),

    /**
     * Giao dịch không thành công do Quý khách nhập sai mật khẩu xác thực giao dịch (OTP). Xin quý khách vui lòng thực hiện lại giao dịch.
     * */
    OTP_INCORRECT("13", (orderService, request) -> null),

    /**
     * Giao dịch không thành công do: Khách hàng hủy giao dịch
     * */
    CANCELLED("24", (orderService, request) -> null),

    /**
     * Giao dịch không thành công do: Tài khoản của quý khách không đủ số dư để thực hiện giao dịch.
     * */
    INSUFFICIENT_BALANCE("51", (orderService, request) -> null),

    /**
     * Giao dịch không thành công do: Tài khoản của Quý khách đã vượt quá hạn mức giao dịch trong ngày.
     * */
    EXCEED_LIMIT("65", (orderService, request) -> null),

    /**
     * Ngân hàng thanh toán đang bảo trì.
     * */
    BANK_MAINTENANCE("75", (orderService, request) -> null),

    /**
     * Giao dịch không thành công do: KH nhập sai mật khẩu thanh toán quá số lần quy định. Xin quý khách vui lòng thực hiện lại giao dịch
     * */
    PAYMENT_PASSWORD_INCORRECT("79", (orderService, request) -> null),

    /**
     * Các lỗi khác (lỗi còn lại, không có trong danh sách mã lỗi đã liệt kê)
     */
    OTHER_ERROR("99", (orderService, request) -> null),
    ;

    private final String code;
    private final VNPayCallBackFunction vnPayCallBackFunction;

    VNPayStatusCodeEnum(String code, VNPayCallBackFunction VNPayCallBackFunction) {
        this.code = code;
        this.vnPayCallBackFunction = VNPayCallBackFunction;
    }

    static public VNPayStatusCodeEnum isMember(String codeInput) {
        VNPayStatusCodeEnum[] vnPayStatusCodeEnums = VNPayStatusCodeEnum.values();
        for (VNPayStatusCodeEnum vnPayStatusCodeEnum : vnPayStatusCodeEnums)
            if (vnPayStatusCodeEnum.code.equals(codeInput))
                return vnPayStatusCodeEnum;
        return null;
    }

    public Order handleCallback(OrderService orderService, HttpServletRequest request) {
        return vnPayCallBackFunction.handle(orderService, request);
    }

}
