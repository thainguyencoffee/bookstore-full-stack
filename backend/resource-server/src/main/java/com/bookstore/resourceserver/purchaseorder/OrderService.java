package com.bookstore.resourceserver.purchaseorder;

import com.bookstore.resourceserver.book.ebook.EBook;
import com.bookstore.resourceserver.book.ebook.impl.EBookServiceImpl;
import com.bookstore.resourceserver.book.printbook.PrintBook;
import com.bookstore.resourceserver.book.printbook.impl.PrintBookServiceImpl;
import com.bookstore.resourceserver.core.exception.BookNotEnoughInventoryException;
import com.bookstore.resourceserver.core.email.EmailService;
import com.bookstore.resourceserver.core.exception.CustomNoResultException;
import com.bookstore.resourceserver.core.valuetype.Price;
import com.bookstore.resourceserver.purchaseorder.dto.LineItemRequest;
import com.bookstore.resourceserver.purchaseorder.dto.OrderRequest;
import com.bookstore.resourceserver.purchaseorder.dto.OrderUpdateDto;
import com.bookstore.resourceserver.core.exception.purchaseorder.OrderStatusNotMatchException;
import com.bookstore.resourceserver.core.exception.purchaseorder.OtpExpiredException;
import com.bookstore.resourceserver.core.exception.purchaseorder.OtpIncorrectException;
import com.bookstore.resourceserver.purchaseorder.valuetype.BookType;
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
    private final EBookServiceImpl eBookService;
    private final PrintBookServiceImpl printBookService;
    private final EmailService emailService;

    public Order findById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new CustomNoResultException(Order.class, CustomNoResultException.Identifier.ID, id));
    }

    public Order findByIdAndUsername(UUID id, String username) {
        return orderRepository.findByIdAndCreatedBy(id, username)
                .orElseThrow(() -> new CustomNoResultException(Order.class, CustomNoResultException.Identifier.ID, id));
    }

    @Transactional
    public Order submitOrder(OrderRequest orderRequest) {
        List<LineItem> lineItems = new ArrayList<>();
        for (LineItemRequest lineItemRequest : orderRequest.getLineItems()) {
            LineItem lineItem = convertLineItemRequestToLineItem(lineItemRequest);
            lineItems.add(lineItem);
        }
        Order order = Order.createOrder(lineItems, orderRequest);
        orderRepository.save(order);
        log.error("ID: " + order.getId());
        /*===== ORDER WAITING FOR PAYMENT =====*/
        return order;
    }

    @Transactional
    public Order buildAcceptedOrder(UUID orderId) {
        Order order = findById(orderId);
        if (!order.getStatus().equals(OrderStatus.WAITING_FOR_PAYMENT)
                && !order.getStatus().equals(OrderStatus.WAITING_FOR_ACCEPTANCE)) {
            throw new OrderStatusNotMatchException(orderId, order.getStatus(), OrderStatus.WAITING_FOR_PAYMENT, OrderStatus.WAITING_FOR_ACCEPTANCE);
        }
        for (LineItem lineItem : order.getLineItems()) {
            if (lineItem.getBookType().equals(BookType.EBOOK)) {
                var eBook = eBookService.findByIsbn(lineItem.getIsbn());
                eBook.getProperties().setPublicationDate(Instant.now());
                int currentPurchases = eBook.getProperties().getPurchases();
                eBook.getProperties().setPurchases(currentPurchases + lineItem.getQuantity());
                eBookService.save(eBook);
            } else {
                var printBook = printBookService.findByIsbn(lineItem.getIsbn());
                if (printBook.getInventory() < lineItem.getQuantity()) {
                    throw new BookNotEnoughInventoryException(printBook.getIsbn());
                }
                // Update book's inventory and purchases
                printBook.setInventory(printBook.getInventory() - lineItem.getQuantity());
                printBook.getProperties().setPublicationDate(Instant.now());
                int currentPurchases = printBook.getProperties().getPurchases();
                printBook.getProperties().setPurchases(currentPurchases + lineItem.getQuantity());
                printBookService.save(printBook);
            }
        }
        order.setStatus(OrderStatus.ACCEPTED);
        /*===== CREATED ORDER =====*/
        orderRepository.save(order);
        String emailBody = emailService.buildEmailBody(order, false);
        emailService.sendConfirmationEmail(order.getUserInformation().getEmail(), "The order has been successfully accepted", emailBody);
        return order;
    }


    @Transactional
    public Order buildOrderWithStatus(UUID orderId, OrderStatus status) {
        Order order = findById(orderId);
        order.setStatus(status);
        orderRepository.save(order);
        return order;
    }

    @Transactional
    public Order createOtp(UUID orderId) {
        Order order = findById(orderId);
        long otp = (long) Math.floor(Math.random() * 900_000L) + 100_000L;
        order.setOtp(otp);
        order.setOtpExpiredAt(Instant.now().plus(5, ChronoUnit.MINUTES));
        orderRepository.save(order);
        return order;
    }

    @Transactional
    public Order verifyOtp(UUID orderId, long otp) {
        Order order = orderRepository.findByIdAndOtp(orderId, otp)
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

    private LineItem convertLineItemRequestToLineItem(LineItemRequest lineItemRequest) {
        String isbn = lineItemRequest.getIsbn();
        BookType bookType = lineItemRequest.getBookType();
        Price price;
        if (bookType.equals(BookType.EBOOK)) {
            EBook eBook = eBookService.findByIsbn(isbn);
            price = eBook.getProperties().getPrice();
        } else {
            PrintBook printBook = printBookService.findByIsbn(isbn);
            price = printBook.getProperties().getPrice();
            log.error("price: " + price.toString());
            if (printBook.getInventory() < lineItemRequest.getQuantity()) {
                throw new BookNotEnoughInventoryException(lineItemRequest.getIsbn());
            }
        }

        LineItem lineItem = new LineItem();
        lineItem.setBookType(bookType);
        lineItem.setQuantity(lineItemRequest.getQuantity());
        lineItem.setPrice(price);
        lineItem.setIsbn(lineItemRequest.getIsbn());
        return lineItem;
    }

    public Page<Order> findAllByCreatedBy(String username, Pageable pageable) {
        return orderRepository.findAllByCreatedBy(username, pageable);
    }

    public Order updateOrder(UUID orderId, OrderUpdateDto orderUpdateDto) {
        Order order = findById(orderId);
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

}
