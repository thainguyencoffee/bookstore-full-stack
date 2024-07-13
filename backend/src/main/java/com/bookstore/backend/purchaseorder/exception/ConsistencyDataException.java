package com.bookstore.backend.purchaseorder.exception;

public class ConsistencyDataException extends RuntimeException{

    public ConsistencyDataException(String cause) {
        super("Consistency data exception: " + cause);
    }

}
