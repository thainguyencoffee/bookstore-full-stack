package com.bookstorefullstack.bookstore.core.exception.shoppingcart;

public class ShoppingCartAlreadyExistingException extends RuntimeException{
    public ShoppingCartAlreadyExistingException(String username) {
        super("Shopping cart already exists: " + username);
    }
}
