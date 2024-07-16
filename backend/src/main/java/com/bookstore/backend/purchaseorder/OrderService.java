package com.bookstore.backend.purchaseorder;

import com.bookstore.backend.book.Book;
import com.bookstore.backend.book.BookService;
import com.bookstore.backend.book.exception.BookNotEnoughInventoryException;
import com.bookstore.backend.core.email.EmailService;
import com.bookstore.backend.purchaseorder.dto.LineItemRequest;
import com.bookstore.backend.purchaseorder.dto.OrderRequest;
import com.bookstore.backend.purchaseorder.dto.OrderUpdateDto;
import com.bookstore.backend.purchaseorder.exception.ConsistencyDataException;
import com.bookstore.backend.purchaseorder.exception.OrderNotFoundException;
import com.bookstore.backend.purchaseorder.exception.OrderStatusNotMatchException;
import com.bookstore.backend.purchaseorder.exception.OtpExpiredException;
import com.bookstore.backend.purchaseorder.exception.OtpIncorrectException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final BookService bookService;
    private final EmailService emailService;

    public Order findById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    public Order findByIdAndUsername(UUID id, String username) {
        return orderRepository.findByIdAndCreatedBy(id, username)
                .orElseThrow(() -> new OrderNotFoundException(id));
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
        /*===== ORDER WAITING FOR PAYMENT =====*/
        return order;
    }

    @Transactional
    public Order buildAcceptedOrder(UUID orderId) {
        Order order = findById(orderId);
        if (!order.getStatus().equals(OrderStatus.WAITING_FOR_PAYMENT)
                && !order.getStatus().equals(OrderStatus.WAITING_FOR_ACCEPTANCE)) {
            throw new ConsistencyDataException("Order's status is not waiting action");
        }
        for (LineItem lineItem : order.getLineItems()) {
            var book = bookService.findByIsbn(lineItem.getIsbn());
            if (book.getInventory() < lineItem.getQuantity()) {
                throw new BookNotEnoughInventoryException(book.getIsbn());
            }
            var bookUpdate = book;
            // Update book's inventory and purchases
            bookUpdate.setInventory(book.getInventory() - lineItem.getQuantity());
            bookUpdate.setPurchases(book.getPurchases() + lineItem.getQuantity());
            bookService.save(bookUpdate);
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
            throw new OrderStatusNotMatchException(orderId, OrderStatus.WAITING_FOR_ACCEPTANCE, order.getStatus());
        }
        Instant otpExpiredAt = order.getOtpExpiredAt();
        if (otpExpiredAt.isBefore(Instant.now())) {
            throw new OtpExpiredException(orderId);
        }
        return buildAcceptedOrder(order.getId());
    }

    private LineItem convertLineItemRequestToLineItem(LineItemRequest lineItemRequest) {
        Book book = bookService.findByIsbn(lineItemRequest.getIsbn());
        if (book.getInventory() < lineItemRequest.getQuantity()) {
            throw new BookNotEnoughInventoryException(lineItemRequest.getIsbn());
        }

        LineItem lineItem = new LineItem();
        lineItem.setQuantity(lineItemRequest.getQuantity());
        lineItem.setPrice(book.getPrice());
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
