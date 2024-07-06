package com.bookstore.backend.orders;

import com.bookstore.backend.orders.dto.UserInformation;
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
    private Long totalPrice;
    private OrderStatus status;
    @MappedCollection(idColumn = "order_id")
    private Set<LineItem> lineItems = new HashSet<>();
    /*User information*/
    @Embedded(onEmpty = Embedded.OnEmpty.USE_NULL)
    private UserInformation userInformation;
    /*End user information*/

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

    public static Order createOrder(List<LineItem> lineItems, UserInformation userInformation) {
        Order order = new Order();
        order.setUserInformation(userInformation);
        order.setStatus(OrderStatus.WAITING_FOR_PAYMENT);
        Long totalPrice = 0L;
        for (LineItem lineItem : lineItems) {
            order.getLineItems().add(lineItem);
            lineItem.setOrderId(order.getId());
            totalPrice += lineItem.getTotalPrice();
        }
        order.setTotalPrice(totalPrice);
        return order;
    }

}