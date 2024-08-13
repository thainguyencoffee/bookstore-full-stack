package com.bookstore.resourceserver.purchaseorder;

import com.bookstore.resourceserver.core.valuetype.Price;
import com.bookstore.resourceserver.purchaseorder.valuetype.BookType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Table("line_items")
@Getter
@Setter
public class LineItem {

    @Id
    private Long id;
    @NotNull(message = "Book is required")
    private UUID orderId;
    private String isbn;
    private BookType bookType;
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be greater than 0")
    private Integer quantity;
    @Embedded.Nullable
    private Price price;
    @Version
    private int version;

    @AccessType(AccessType.Type.PROPERTY)
    @Transient
    public Price getTotalPrice() {
        Price total = new Price();
        if (price.getDiscountedPrice() != null) {
            total.setDiscountedPrice(price.getDiscountedPrice() * quantity);
        }
        total.setOriginalPrice(price.getOriginalPrice() * quantity);
        return total;
    }

}
