package com.bookstorefullstack.bookstore.purchaseorder.web.user;

import com.bookstorefullstack.bookstore.purchaseorder.PurchaseOrder;
import com.bookstorefullstack.bookstore.purchaseorder.OrderService;
import com.bookstorefullstack.bookstore.purchaseorder.dto.OrderRequest;
import com.bookstorefullstack.bookstore.purchaseorder.dto.OrderUpdateDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping(path = "orders", produces = "application/json")
@RequiredArgsConstructor
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    @GetMapping
    public Page<PurchaseOrder> getAllOrder(@AuthenticationPrincipal Jwt jwt, Pageable pageable) {
        log.info("OrderController attempt retrieve orders.");
        String username = jwt.getClaim(StandardClaimNames.PREFERRED_USERNAME).toString();
        return orderService.findAllByCreatedBy(username, pageable);
    }

    @GetMapping("/{orderId}")
    public PurchaseOrder getOrderById(@PathVariable UUID orderId, @AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaim(StandardClaimNames.PREFERRED_USERNAME).toString();
        log.info("OrderController attempt retrieve order by {}", username);
        return orderService.findByIdAndUsername(orderId, username);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PurchaseOrder submitOrder(@Valid @RequestBody OrderRequest orderRequest) {
        log.info("OrderController attempt submit order. {}", orderRequest.toString());
        return orderService.submitOrder(orderRequest);
    }

    @PatchMapping("/{orderId}")
    public ResponseEntity<PurchaseOrder> updateOrder(@PathVariable UUID orderId, @RequestBody OrderUpdateDto orderUpdateDto, @AuthenticationPrincipal Jwt jwt) {
        log.info("OrderController attempt update order by id. {}", orderUpdateDto.getUserInformation().toString());
        String username = jwt.getClaim(StandardClaimNames.PREFERRED_USERNAME).toString();
        if (username.equals(orderUpdateDto.getCreatedBy()) || username.equals("guest")) {
            return ResponseEntity.ok(orderService.updateOrder(orderId, orderUpdateDto));
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

}
