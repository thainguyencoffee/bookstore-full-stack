package com.bookstore.backend.core.exception;

import com.bookstore.backend.core.ApiError;
import com.bookstore.backend.shopppingcart.ShoppingCart;
import com.bookstore.backend.shopppingcart.exception.ShoppingCartAlreadyExistingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
class ShoppingCartControllerAdvice implements ControllerAdviceConfig {

    @ExceptionHandler(ShoppingCartAlreadyExistingException.class)
    public ApiError handleDataIntegrityViolation(ShoppingCartAlreadyExistingException ex) {
        ApiError.ErrorInfo errorInfo = new ApiError.ErrorInfo();
        errorInfo.setEntity(ShoppingCart.class.getSimpleName());
        errorInfo.setMessage(ex.getMessage());
        return ApiError.builder()
                .errors(List.of(errorInfo))
                .build();
    }

}
