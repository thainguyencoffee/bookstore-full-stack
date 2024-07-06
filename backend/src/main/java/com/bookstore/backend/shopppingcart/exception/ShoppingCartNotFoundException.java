package com.bookstore.backend.shopppingcart.exception;

import java.util.UUID;

public class ShoppingCartNotFoundException extends RuntimeException {
    public ShoppingCartNotFoundException(UUID cartId) {
        super("Shopping cart with id " + cartId + " not found");
    }
}
