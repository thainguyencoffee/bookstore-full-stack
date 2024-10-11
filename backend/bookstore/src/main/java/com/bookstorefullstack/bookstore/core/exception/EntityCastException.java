package com.bookstorefullstack.bookstore.core.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntityCastException extends RuntimeException {

    private Class<?> inputClass;
    private Class<?> expectedClass;
    public EntityCastException(Class<?> inputClass, Class<?> expectedClass) {
        super(inputClass.getSimpleName() + " can not be cast to " + expectedClass.getSimpleName());
    }

}
