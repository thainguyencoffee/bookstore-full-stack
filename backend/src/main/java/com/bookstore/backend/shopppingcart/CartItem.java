package com.bookstore.backend.shopppingcart;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("cart_items")
@Getter
@Setter
public class CartItem {
    @Id
    private Long id;
    private UUID cartId;
    @NotBlank(message = "The isbn of book must not be null or blank.")
    @Pattern(regexp = "^([0-9]{10}|[0-9]{13})$", message = "The ISBN must be valid")
    private String isbn;
    private int quantity;
}
