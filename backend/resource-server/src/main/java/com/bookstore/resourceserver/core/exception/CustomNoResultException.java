package com.bookstore.resourceserver.core.exception;

import lombok.Getter;

@Getter
public class CustomNoResultException extends RuntimeException {

    private final Class<?> clazz;

    public CustomNoResultException(final Class<?> clazz, Identifier identifier, Object rejectValue) {
        super(buildMessageError(clazz, identifier, rejectValue));
        this.clazz = clazz;
    }

    private static String buildMessageError(final Class<?> clazz, Identifier identifier, Object rejectValue) {
        return "No result found for " +
                clazz.getSimpleName() +
                " with " +
                identifier +
                " " +
                rejectValue;
    }

    public static enum Identifier {
        ID, ISBN
    }

}