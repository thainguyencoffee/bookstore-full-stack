package com.bookstore.resourceserver.core.email;

import com.bookstore.resourceserver.book.Book;
import com.bookstore.resourceserver.book.BookService;
import com.bookstore.resourceserver.purchaseorder.PurchaseOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;
    private final BookService bookService;
    @Value("${spring.mail.username}")
    private String from;

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

    public String buildEmailBody(PurchaseOrder order, boolean notifyUpdate) {
        var userInfo = order.getUserInformation();
        var addressInfo = order.getAddress();
        var lineItemsString = new StringBuilder();
//        for (LineItem lineItem : order.getLineItems()) {
//            var book = bookService.findByIsbn(lineItem.getIsbn());
//            Price price;
//            if (lineItem.getBookType().equals(BookType.EBOOK)) {
//                EBook eBook = eBookService.findByIsbn(lineItem.getIsbn());
//                price = eBook.getProperties().getPrice();
//            } else {
//                PrintBook printBook = printBookService.findByIsbn(lineItem.getIsbn());
//                price = printBook.getProperties().getPrice();
//            }
//            lineItemsString.append(book.getTitle())
//                    .append(" x").append(lineItem.getQuantity()).append(" - ")
//                    .append(NumberFormat.getCurrencyInstance(new Locale("vi", "VN"))
//                            .format(price.getDiscountedPrice())).append(price.getCurrencyPrice()).append(" \n");
//        }
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
                .append("Địa chỉ: ").append(addressInfo.getAddress()).append(" \n")
                .append("City: ").append(addressInfo.getCity()).append(" , zipCode: ").append(addressInfo.getZipCode()).append(" \n\n")
                .append("Chúng tôi sẽ liên hệ với bạn sớm để xác nhận và giao hàng. \n")
                .append("Trân trọng, \n")
                .append("Bookstore support!").toString();
    }

    public String buildPromotionEmailBody() {
        StringBuilder body = new StringBuilder();
        body.append("<h1>Chào mừng đến với Bookstore!</h1>");
        body.append("<p>Chúng tôi rất vui khi thông báo rằng bạn đã đăng ký nhận thông tin từ Bookstore.");
        body.append("<p>Đừng bỏ lỡ cơ hội khám phá những đầu sách tuyệt vời tại Bookstore! Cảm ơn bạn đã quan tâm.</p>");
        body.append("<p>Trân trọng,</p>");
        body.append("<p>Bookstore Team</p>");

        return body.toString();
    }

    public String buildPromotionalDealOfTheDayEmailBody(List<Book> bestSellerBooks, List<Book> freeProducts) {
        String baseUrl = "http://localhost:6969/vue-ui/book-detail/";

        StringBuilder body = new StringBuilder();
        body.append("<h1>Chào mừng đến với Bookstore!</h1>");

        body.append("<p>Chúng tôi rất vui khi thông báo rằng bạn đã đăng ký nhận thông tin từ Bookstore. " +
                "Dưới đây là một số thông tin mà chúng tôi muốn chia sẻ với bạn:</p>");

        if (!bestSellerBooks.isEmpty()) {
            body.append("<p>Dưới đây là một số tựa sách bán chạy nhất của chúng tôi:</p>");
            for (Book book : bestSellerBooks) {
                body.append("<p><a href='").append(baseUrl).append(book.getIsbn()).append("'>")
                        .append(book.getTitle()).append("</a></p>");
            }
        } else {
            body.append("<p>Hiện tại chúng tôi không có sách bán chạy nào.</p>");
        }

        if (!freeProducts.isEmpty()) {
            body.append("<p>Ngoài ra, hãy xem các sản phẩm miễn phí tuyệt vời của chúng tôi!</p>");
            body.append("<table>");
            body.append("<tr><th>Tiêu đề</th><th>Mô tả</th></tr>");
            for (Book freeBook : freeProducts) {
                body.append("<tr><td>").append(freeBook.getTitle()).append("</td>")
                        .append("<td>").append(freeBook.getDescription()).append("</td></tr>");
            }
            body.append("</table>");
        } else {
            body.append("<p>Hiện tại chúng tôi không có sản phẩm miễn phí nào.</p>");
        }

        body.append("<p>Đừng bỏ lỡ cơ hội khám phá những đầu sách tuyệt vời tại Bookstore! Cảm ơn bạn đã quan tâm.</p>");
        body.append("<p>Trân trọng,</p>");
        body.append("<p>Bookstore Team</p>");

        return body.toString();
    }

}
