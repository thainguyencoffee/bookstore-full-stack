package com.bookstore.backend.core.exception;

import com.bookstore.backend.book.Book;
import com.bookstore.backend.core.ApiError;
import com.bookstore.backend.orders.exception.ConsistencyDataException;
import com.bookstore.backend.orders.exception.OrderNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * The type Book controller advice.
 */
@RestControllerAdvice
public class OrderControllerAdvice implements ControllerAdviceConfig {

    @ExceptionHandler(ConsistencyDataException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleRuntimeException(ConsistencyDataException ex) {
        ApiError.ErrorInfo errorInfo = new ApiError.ErrorInfo();
        errorInfo.setMessage(ex.getMessage());
        return ApiError.builder()
                .errors(List.of(errorInfo)).build();
    }

    /**
     * Handle order not found api error.
     *
     * @param ex the ex
     * @return the api error
     */
    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleOrderNotFound(OrderNotFoundException ex) {
        ApiError.ErrorInfo errorInfo = new ApiError.ErrorInfo();
        errorInfo.setEntity(Book.class.getSimpleName());
        errorInfo.setMessage(ex.getMessage());
        return ApiError.builder()
                .errors(List.of(errorInfo)).build();
    }

}
