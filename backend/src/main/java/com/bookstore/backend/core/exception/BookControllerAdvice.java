package com.bookstore.backend.core.exception;

import com.bookstore.backend.book.Book;
import com.bookstore.backend.book.exception.BookNotEnoughInventoryException;
import com.bookstore.backend.book.exception.BookNotFoundException;
import com.bookstore.backend.core.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * The type Book controller advice.
 */
@RestControllerAdvice
class BookControllerAdvice implements ControllerAdviceConfig {

    /**
     * Handle book not enough inventory exception api error.
     *
     * @param ex the ex
     * @return the api error
     */
    @ExceptionHandler(BookNotEnoughInventoryException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBookNotEnoughInventoryException(BookNotEnoughInventoryException ex) {
        ApiError.ErrorInfo errorInfo = new ApiError.ErrorInfo();
        errorInfo.setEntity(Book.class.getSimpleName());
        errorInfo.setMessage(ex.getMessage());
        return ApiError.builder()
                .errors(List.of(errorInfo)).build();
    }

    /**
     * Handle book not found api error.
     *
     * @param ex the ex
     * @return the api error
     */
    @ExceptionHandler(BookNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleBookNotFound(BookNotFoundException ex) {
        ApiError.ErrorInfo errorInfo = new ApiError.ErrorInfo();
        errorInfo.setEntity(Book.class.getSimpleName());
        errorInfo.setMessage(ex.getMessage());
        return ApiError.builder()
                .errors(List.of(errorInfo)).build();
    }

}
