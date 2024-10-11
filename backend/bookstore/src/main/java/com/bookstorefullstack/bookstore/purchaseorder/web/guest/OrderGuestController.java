package com.bookstorefullstack.bookstore.purchaseorder.web.guest;

import com.bookstorefullstack.bookstore.purchaseorder.PurchaseOrder;
import com.bookstorefullstack.bookstore.purchaseorder.OrderService;
import com.bookstorefullstack.bookstore.purchaseorder.dto.OrderUpdateDto;
import com.bookstorefullstack.bookstore.purchaseorder.web.user.OrderController;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("guest-orders")
@RequiredArgsConstructor
public class OrderGuestController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    @GetMapping("/{orderId}")
    public PurchaseOrder getOrderById(@PathVariable UUID orderId) {
        log.info("OrderController attempt retrieve order by guest.");
        return orderService.findByIdAndUsername(orderId, "guest");
    }

    @PatchMapping("/{orderId}")
    public PurchaseOrder updateOrder(@PathVariable UUID orderId, @RequestBody OrderUpdateDto orderUpdateDto) {
        log.info("OrderController attempt update order by id. {}", orderUpdateDto.getUserInformation().toString());
        return orderService.updateOrder(orderId, orderUpdateDto);
    }

}
