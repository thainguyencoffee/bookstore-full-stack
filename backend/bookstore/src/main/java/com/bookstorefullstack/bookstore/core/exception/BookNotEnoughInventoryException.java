package com.bookstorefullstack.bookstore.core.exception;

public class BookNotEnoughInventoryException extends RuntimeException {
    public BookNotEnoughInventoryException(String isbn) {
        super("Not enough inventory for book with isbn " + isbn);
    }
}
