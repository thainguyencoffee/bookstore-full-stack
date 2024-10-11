package com.bookstorefullstack.bookstore.core.utils;

import com.bookstorefullstack.bookstore.core.ApiError;

import java.lang.annotation.Annotation;

public interface ApiErrorBuilder {

    ApiErrorBuilder addInvalid(String property,
                               Class<? extends Annotation> annotation,
                               Object invalidValue,
                               String attribute);

    ApiError build();
}
