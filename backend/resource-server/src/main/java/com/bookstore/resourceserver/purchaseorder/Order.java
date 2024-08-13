package com.bookstore.resourceserver.purchaseorder;

import com.bookstore.resourceserver.core.valuetype.AddressInformation;
import com.bookstore.resourceserver.core.valuetype.Price;
import com.bookstore.resourceserver.purchaseorder.dto.OrderRequest;
import com.bookstore.resourceserver.core.valuetype.UserInformation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Table("orders")
@Getter
@Setter
@NoArgsConstructor
public class Order  {
    @Id
    private UUID id;
    @Embedded.Empty(prefix = "total_")
    private Price totalPrice;
    private OrderStatus status;
    private PaymentMethod paymentMethod;
    @MappedCollection(idColumn = "order_id")
    private Set<LineItem> lineItems = new HashSet<>();
    @Embedded.Nullable
    private UserInformation userInformation;
    @Embedded.Nullable
    private AddressInformation address;
    private Long otp;
    private Instant otpExpiredAt;
    @CreatedDate
    private Instant createdDate;
    @CreatedBy
    private String createdBy;
    @LastModifiedDate
    private Instant lastModifiedDate;
    @LastModifiedBy
    private String lastModifiedBy;

    @Version
    private int version;

    public static Order createOrder(List<LineItem> lineItems, OrderRequest orderRequest) {
        Order order = new Order();
        order.setUserInformation(orderRequest.getUserInformation());
        order.setAddress(orderRequest.getAddressInformation());
        order.setPaymentMethod(orderRequest.getPaymentMethod());
        if (orderRequest.getPaymentMethod() == PaymentMethod.VNPAY) {
            order.setStatus(OrderStatus.WAITING_FOR_PAYMENT);
        } else {
            order.setStatus(OrderStatus.WAITING_FOR_ACCEPTANCE);
        }
        for (LineItem lineItem : lineItems) {
            order.getLineItems().add(lineItem);
            lineItem.setOrderId(order.getId());

        }
        order.setTotalPrice(calculateTotalPrice(lineItems));
        return order;
    }


    private static Price calculateTotalPrice(List<LineItem> lineItems) {
        Set<String> currency = new HashSet<>();
        Long originalTotalPrice = 0L;
        Long discountedTotalPrice = 0L;

        for (LineItem lineItem : lineItems) {
            currency.add(lineItem.getPrice().getCurrencyPrice());
            originalTotalPrice += lineItem.getTotalPrice().getOriginalPrice();
            discountedTotalPrice += lineItem.getTotalPrice().getDiscountedPrice();
        }
        if (currency.size() == 1) {
            return new Price(originalTotalPrice, discountedTotalPrice, currency.iterator().next());
        }
        else throw new IllegalArgumentException("More than one currency found");
    }

    private static String getCurrency(List<LineItem> lineItems) {
        Set<String> currency = new HashSet<>();
        lineItems.forEach(lineItem -> currency.add(lineItem.getPrice().getCurrencyPrice()));
        if (currency.size() == 1) return currency.iterator().next();
        else throw new IllegalArgumentException("More than one currency found");
    }

}