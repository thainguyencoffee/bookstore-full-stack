package com.bookstore.resourceserver.core.utils;

import com.bookstore.resourceserver.core.ApiError;

import java.lang.annotation.Annotation;

public class ApiErrorBuilderSpec implements ApiErrorBuilder {

    private final ApiError apiError;
    private final BeanValidationUtils beanValidationUtils;

    public ApiErrorBuilderSpec(BeanValidationUtils beanValidationUtils) {
        this.apiError = new ApiError();
        this.beanValidationUtils = beanValidationUtils;
    }

    @Override
    public ApiErrorBuilder addInvalid(String property, Class<? extends Annotation> annotation, Object invalidValue, String attribute) {
        String msg = beanValidationUtils.getMessageAttrOfAnnotation(property, annotation, attribute);
        String classSimpleName = beanValidationUtils.getClazz().getSimpleName();
        this.apiError.addError(new ApiError.ErrorInfo(classSimpleName.toLowerCase(), property, invalidValue, msg));
        return this;
    }

    public ApiError build() {
        return apiError;
    }

}
