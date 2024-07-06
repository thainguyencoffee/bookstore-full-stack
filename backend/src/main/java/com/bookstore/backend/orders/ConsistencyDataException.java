package com.bookstore.backend.orders;

public class ConsistencyDataException extends RuntimeException{

    public ConsistencyDataException(String cause) {
        super("Consistency data exception: " + cause);
    }

}
