package com.bookstore.backend.orders.web.guest;

import com.bookstore.backend.orders.Order;
import com.bookstore.backend.orders.OrderService;
import com.bookstore.backend.orders.dto.OrderUpdateDto;
import com.bookstore.backend.orders.web.user.OrderController;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/guest-orders")
@RequiredArgsConstructor
public class OrderGuestController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    @GetMapping("/{orderId}")
    public Order getOrderById(@PathVariable UUID orderId) {
        log.info("OrderController attempt retrieve order by guest.");
        return orderService.findByIdAndUsername(orderId, "guest");
    }

    @PatchMapping("/{orderId}")
    public Order updateOrder(@PathVariable UUID orderId, @RequestBody OrderUpdateDto orderUpdateDto) {
        log.info("OrderController attempt update order by id. {}", orderUpdateDto.getUserInformation().toString());
        return orderService.updateOrder(orderId, orderUpdateDto);
    }

}
