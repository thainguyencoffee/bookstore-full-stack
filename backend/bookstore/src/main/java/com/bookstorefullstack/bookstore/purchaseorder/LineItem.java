package com.bookstorefullstack.bookstore.purchaseorder;

import com.bookstorefullstack.bookstore.core.valuetype.Price;
import com.bookstorefullstack.bookstore.purchaseorder.valuetype.BookType;
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

@Table("line_item")
@Getter
@Setter
public class LineItem {

    @Id
    private Long id;
    private VariantBookRef variantBookRef;
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

    public BookType getBookType() {
        return variantBookRef.getBookType();
    }

    public String getIsbn() {
        return variantBookRef.getIsbn();
    }

    public void setVariantBook(String isbn, String title) {
        this.variantBookRef = new VariantBookRef(isbn, title);
    }

    public void setBookType(BookType bookType) {
        this.variantBookRef.setBookType(bookType);
    }

}
