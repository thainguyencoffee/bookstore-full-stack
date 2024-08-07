package com.bookstore.resourceserver.shopppingcart;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Table("shopping_carts")
@Getter
@Setter
public class ShoppingCart {

    @Id
    private UUID id;
    @MappedCollection(idColumn = "cart_id")
    private Set<CartItem> cartItems = new HashSet<>();
    @CreatedBy
    private String createdBy;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedBy
    private String lastModifiedBy;
    @LastModifiedDate
    private Instant lastModifiedAt;
}
