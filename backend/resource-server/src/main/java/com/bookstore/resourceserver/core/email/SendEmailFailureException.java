package com.bookstore.resourceserver.core.email;

public class SendEmailFailureException extends RuntimeException {
    public SendEmailFailureException(String msg) {
        super(msg);
    }
}
