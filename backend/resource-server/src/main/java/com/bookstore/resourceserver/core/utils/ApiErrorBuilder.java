package com.bookstore.resourceserver.core.utils;

import com.bookstore.resourceserver.core.ApiError;

import java.lang.annotation.Annotation;

public interface ApiErrorBuilder {

    ApiErrorBuilder addInvalid(String property,
                               Class<? extends Annotation> annotation,
                               Object invalidValue,
                               String attribute);

    ApiError build();
}
