package com.bookstore.backend.orders.exception;

public class ConsistencyDataException extends RuntimeException{

    public ConsistencyDataException(String cause) {
        super("Consistency data exception: " + cause);
    }

}
