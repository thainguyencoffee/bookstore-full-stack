package com.bookstore.backend.core.email;

public class SendEmailFailureException extends RuntimeException {
    public SendEmailFailureException(String msg) {
        super(msg);
    }
}
