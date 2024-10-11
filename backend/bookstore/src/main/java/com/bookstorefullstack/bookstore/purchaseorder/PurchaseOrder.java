package com.bookstorefullstack.bookstore.purchaseorder;

import com.bookstorefullstack.bookstore.core.valuetype.AddressInformation;
import com.bookstorefullstack.bookstore.core.valuetype.Price;
import com.bookstorefullstack.bookstore.core.valuetype.UserInformation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@Table("purchase_order")
public class PurchaseOrder {
    @Id
    private UUID id;
//    @Embedded.Empty(prefix = "total_")
//    private Price totalPrice;
    private OrderStatus status;
    private PaymentMethod paymentMethod;
    private List<LineItem> lineItems = new ArrayList<>();
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

    @AccessType(AccessType.Type.PROPERTY)
    @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL, prefix = "total_")
    public Price getTotalPrice() {
        return this.lineItems.stream().map(LineItem::getTotalPrice).reduce(new Price(), (acc, price) -> {
            acc.addOriginalPrice(price.getOriginalPrice());
            acc.addDiscountedPrice(price.getDiscountedPrice());
            return acc;
        });
    }

//    private static Price calculateTotalPrice(List<LineItem> lineItems) {
//        Set<String> currency = new HashSet<>();
//        Long originalTotalPrice = 0L;
//        Long discountedTotalPrice = 0L;
//
//        for (LineItem lineItem : lineItems) {
//            currency.add(lineItem.getPrice().getCurrencyPrice());
//            originalTotalPrice += lineItem.getTotalPrice().getOriginalPrice();
//            discountedTotalPrice += lineItem.getTotalPrice().getDiscountedPrice();
//        }
//        if (currency.size() == 1) {
//            return new Price(originalTotalPrice, discountedTotalPrice, currency.iterator().next());
//        }
//        else throw new IllegalArgumentException("More than one currency found");
//    }
//
//    private static String getCurrency(List<LineItem> lineItems) {
//        Set<String> currency = new HashSet<>();
//        lineItems.forEach(lineItem -> currency.add(lineItem.getPrice().getCurrencyPrice()));
//        if (currency.size() == 1) return currency.iterator().next();
//        else throw new IllegalArgumentException("More than one currency found");
//    }

}