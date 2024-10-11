package com.bookstorefullstack.bookstore.purchaseorder;

import com.bookstorefullstack.bookstore.book.Book;
import com.bookstorefullstack.bookstore.book.BookService;
import com.bookstorefullstack.bookstore.book.EBook;
import com.bookstorefullstack.bookstore.book.PrintBook;
import com.bookstorefullstack.bookstore.core.exception.BookNotEnoughInventoryException;
import com.bookstorefullstack.bookstore.core.email.EmailService;
import com.bookstorefullstack.bookstore.core.exception.CustomNoResultException;
import com.bookstorefullstack.bookstore.purchaseorder.dto.OrderRequest;
import com.bookstorefullstack.bookstore.purchaseorder.dto.OrderRequest.LineItemRequest;
import com.bookstorefullstack.bookstore.purchaseorder.dto.OrderUpdateDto;
import com.bookstorefullstack.bookstore.core.exception.purchaseorder.OrderStatusNotMatchException;
import com.bookstorefullstack.bookstore.core.exception.purchaseorder.OtpExpiredException;
import com.bookstorefullstack.bookstore.core.exception.purchaseorder.OtpIncorrectException;
import com.bookstorefullstack.bookstore.purchaseorder.valuetype.BookType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final EmailService emailService;
    private final BookService bookService;

    public PurchaseOrder findById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new CustomNoResultException(PurchaseOrder.class, CustomNoResultException.Identifier.ID, id));
    }

    public PurchaseOrder findByIdAndUsername(UUID id, String username) {
        return orderRepository.findByIdAndCreatedBy(id, username)
                .orElseThrow(() -> new CustomNoResultException(PurchaseOrder.class, CustomNoResultException.Identifier.ID, id));
    }

    @Transactional
    public PurchaseOrder submitOrder(OrderRequest orderRequest) {
        List<LineItem> lineItems = new ArrayList<>();
        for (LineItemRequest lineItemRequest : orderRequest.getLineItems()) {
            LineItem lineItem = convertLineItemRequestToLineItem(lineItemRequest);
            lineItems.add(lineItem);
        }
        PurchaseOrder order = createOrder(lineItems, orderRequest);
        orderRepository.save(order);
        log.error("ID: " + order.getId());
        /*===== ORDER WAITING FOR PAYMENT =====*/
        return order;
    }

    @Transactional
    public PurchaseOrder buildAcceptedOrder(UUID orderId) {
        PurchaseOrder order = findById(orderId);
        if (!order.getStatus().equals(OrderStatus.WAITING_FOR_PAYMENT)
                && !order.getStatus().equals(OrderStatus.WAITING_FOR_ACCEPTANCE)) {
            throw new OrderStatusNotMatchException(orderId, order.getStatus(), OrderStatus.WAITING_FOR_PAYMENT, OrderStatus.WAITING_FOR_ACCEPTANCE);
        }
        for (LineItem lineItem : order.getLineItems()) {
            String isbn = lineItem.getIsbn();
            BookType bookType = lineItem.getBookType();

            Book book = bookService.findByIsbn(isbn);

            if (bookType.equals(BookType.EBOOK)) {
                var eBook = book.getEBook();
                int currentPurchases = eBook.getProperties().getPurchases();
                eBook.getProperties().setPurchases(currentPurchases + lineItem.getQuantity());

            } else {
                var printBook = book.getPrintBook();
                if (printBook.getInventory() < lineItem.getQuantity()) {
                    throw new BookNotEnoughInventoryException("");
                }
                // Update book's inventory and purchases
                printBook.setInventory(printBook.getInventory() - lineItem.getQuantity());
                int currentPurchases = printBook.getProperties().getPurchases();
                printBook.getProperties().setPurchases(currentPurchases + lineItem.getQuantity());
            }

            bookService.save(book);
        }
        order.setStatus(OrderStatus.ACCEPTED);
        /*===== CREATED ORDER =====*/
        orderRepository.save(order);
        String emailBody = emailService.buildEmailBody(order, false);
        emailService.sendConfirmationEmail(order.getUserInformation().getEmail(), "The order has been successfully accepted", emailBody);
        return order;
    }

    @Transactional
    public PurchaseOrder buildOrderWithStatus(UUID orderId, OrderStatus status) {
        PurchaseOrder order = findById(orderId);
        order.setStatus(status);
        orderRepository.save(order);
        return order;
    }

    @Transactional
    public PurchaseOrder createOtp(UUID orderId) {
        PurchaseOrder order = findById(orderId);
        long otp = (long) Math.floor(Math.random() * 900_000L) + 100_000L;
        order.setOtp(otp);
        order.setOtpExpiredAt(Instant.now().plus(5, ChronoUnit.MINUTES));
        orderRepository.save(order);
        return order;
    }

    @Transactional
    public PurchaseOrder verifyOtp(UUID orderId, long otp) {
        PurchaseOrder order = orderRepository.findByIdAndOtp(orderId, otp)
                .orElseThrow(() -> new OtpIncorrectException(orderId));
        if(!order.getStatus().equals(OrderStatus.WAITING_FOR_ACCEPTANCE)) {
            throw new OrderStatusNotMatchException(orderId, order.getStatus(), OrderStatus.WAITING_FOR_ACCEPTANCE);
        }
        Instant otpExpiredAt = order.getOtpExpiredAt();
        if (otpExpiredAt.isBefore(Instant.now())) {
            throw new OtpExpiredException(orderId);
        }
        return buildAcceptedOrder(order.getId());
    }

    public Page<PurchaseOrder> findAllByCreatedBy(String username, Pageable pageable) {
        return orderRepository.findAllByCreatedBy(username, pageable);
    }

    public PurchaseOrder updateOrder(UUID orderId, OrderUpdateDto orderUpdateDto) {
        PurchaseOrder order = findById(orderId);
        order.setUserInformation(orderUpdateDto.getUserInformation());
        if (!orderUpdateDto.getCreatedBy().isEmpty()) {
            order.setCreatedBy(orderUpdateDto.getCreatedBy());
        }
        if (!orderUpdateDto.getLastModifiedBy().isEmpty()) {
            order.setLastModifiedBy(orderUpdateDto.getLastModifiedBy());
        }
        orderRepository.save(order);
        String emailBody = emailService.buildEmailBody(order, true);
        emailService.sendConfirmationEmail(order.getUserInformation().getEmail(), "The order updated success fully", emailBody);
        return order;
    }

    private LineItem convertLineItemRequestToLineItem(LineItemRequest lineItemRequest) {
        var isbn = lineItemRequest.getIsbn();
        // find book by isbn (should cache book because many query duplicate)
        var book = bookService.findByIsbn(isbn);
        var bookType = lineItemRequest.getBookType();
        var lineItem = new LineItem();

        if (bookType.equals(BookType.EBOOK)) {
            EBook eBook = book.getEBook();
            lineItem.setPrice(eBook.getProperties().getPrice());
            lineItem.setBookType(BookType.EBOOK);
        } else {
            PrintBook printBook = book.getPrintBook();
            lineItem.setPrice(printBook.getProperties().getPrice());
            // if book is print book then check inventory
            if (printBook.getInventory() < lineItemRequest.getQuantity()) {
                throw new BookNotEnoughInventoryException(lineItemRequest.getIsbn());
            }
            lineItem.setBookType(BookType.PRINT_BOOK);
        }
        lineItem.setVariantBook(book.getIsbn(), book.getTitle());
        lineItem.setQuantity(lineItemRequest.getQuantity());
        return lineItem;
    }

    private PurchaseOrder createOrder(List<LineItem> lineItems, OrderRequest orderRequest) {
        PurchaseOrder order = new PurchaseOrder();
        order.setUserInformation(orderRequest.getUserInformation());
        order.setAddress(orderRequest.getAddressInformation());
        order.setPaymentMethod(orderRequest.getPaymentMethod());
        if (orderRequest.getPaymentMethod() == PaymentMethod.VNPAY) {
            order.setStatus(OrderStatus.WAITING_FOR_PAYMENT);
        } else {
            order.setStatus(OrderStatus.WAITING_FOR_ACCEPTANCE);
        }
        order.setLineItems(lineItems);
        return order;
    }

}
