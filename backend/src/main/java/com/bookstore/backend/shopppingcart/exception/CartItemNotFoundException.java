package com.bookstore.backend.shopppingcart.exception;

public class CartItemNotFoundException extends RuntimeException{
    public CartItemNotFoundException(String isbn) {
        super("Could not find cart item with ISBN " + isbn);
    }
}
