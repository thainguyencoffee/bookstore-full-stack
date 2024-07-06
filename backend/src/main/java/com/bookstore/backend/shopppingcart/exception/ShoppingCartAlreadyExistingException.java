package com.bookstore.backend.shopppingcart.exception;

public class ShoppingCartAlreadyExistingException extends RuntimeException{
    public ShoppingCartAlreadyExistingException(String username) {
        super("Shopping cart already exists: " + username);
    }
}
