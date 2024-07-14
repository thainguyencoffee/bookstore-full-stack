package com.bookstore.backend.core.email;

import com.bookstore.backend.book.Book;
import com.bookstore.backend.book.BookService;
import com.bookstore.backend.purchaseorder.LineItem;
import com.bookstore.backend.purchaseorder.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.Locale;

@Service
public class EmailService {

    private final JavaMailSender emailSender;
    private final BookService bookService;
    private static String from;

    public EmailService(@Value("${spring.mail.username}") String from, JavaMailSender emailSender, BookService bookService) {
        this.emailSender = emailSender;
        this.bookService = bookService;
        from = from;
    }

    public void sendConfirmationEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from); // Địa chỉ Gmail của bạn
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            emailSender.send(message);
        } catch (Exception e) {
            throw new SendEmailFailureException("Failed to send email.");
        }
    }

    public static String buildEmailVerifyBody(long otp) {
        return "Mã xác thực của bạn là (Thời hạn 5 phút): " + otp;
    }

    public String buildEmailBody(Order order, boolean notifyUpdate) {
        var userInfo = order.getUserInformation();
        var lineItemsString = new StringBuilder();
        for (LineItem lineItem : order.getLineItems()) {
            Book book = bookService.findByIsbn(lineItem.getIsbn());
            lineItemsString.append(book.getTitle())
                    .append(" x").append(lineItem.getQuantity()).append(" - ")
                    .append(NumberFormat.getCurrencyInstance(new Locale("vi", "VN"))
                            .format(book.getPrice())).append(" VND \n");
        }
        StringBuilder result;
        if (notifyUpdate) {
            result = new StringBuilder().append("Chào ").append(userInfo.getFullName()).append(", \n\n")
                    .append("Đơn hàng của bạn đã được cập nhật: \n\n");
        } else {
            result = new StringBuilder().append("Chào ").append(userInfo.getFullName()).append(", \n\n")
                    .append("Cảm ơn bạn đã đặt hàng tại Bookstore! \n\n");
        }
        return result.append("Đây là thông tin đơn hàng của bạn: \n\n")
                .append("Mã đơn hàng: ").append(order.getId()).append(" \n")
                .append("Kiểu nhận hàng: ").append(order.getPaymentMethod()).append(" \n")
                .append("Ngày đặt: ").append(order.getCreatedDate()).append(" \n")
                .append("Tổng tiền: ").append(NumberFormat.getCurrencyInstance(new Locale("vi", "VN"))
                        .format(order.getTotalPrice())).append(" VND \n\n")
                .append("Danh sách sản phẩm: \n")
                .append(lineItemsString).append("\n\n")
                .append("Thông tin địa chỉ nhận hàng: \n")
                .append("Địa chỉ: ").append(userInfo.getAddress()).append(" \n")
                .append("City: ").append(userInfo.getCity()).append(" , zipCode: ").append(userInfo.getZipCode()).append(" \n\n")
                .append("Chúng tôi sẽ liên hệ với bạn sớm để xác nhận và giao hàng. \n")
                .append("Trân trọng, \n")
                .append("Bookstore support!").toString();
    }

}
