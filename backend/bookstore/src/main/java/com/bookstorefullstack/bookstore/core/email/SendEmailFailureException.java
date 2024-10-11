package com.bookstorefullstack.bookstore.core.email;

public class SendEmailFailureException extends RuntimeException {
    public SendEmailFailureException(String msg) {
        super(msg);
    }
}
