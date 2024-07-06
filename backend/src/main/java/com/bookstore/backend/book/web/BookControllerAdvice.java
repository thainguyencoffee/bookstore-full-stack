package com.bookstore.backend.book.web;

import com.bookstore.backend.book.Book;
import com.bookstore.backend.book.exception.BookNotEnoughInventoryException;
import com.bookstore.backend.book.exception.BookNotFoundException;
import com.bookstore.backend.core.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Book controller advice.
 */
@RestControllerAdvice
class BookControllerAdvice {

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

    /**
     * Handle method argument not valid api error.
     *
     * @param ex the ex
     * @return the api error
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<ApiError.ErrorInfo> errorInfors = ex.getBindingResult()
                .getFieldErrors().stream()
                .map(fieldError ->
                        new ApiError.ErrorInfo(
                                fieldError.getObjectName(),
                                fieldError.getField(),
                                fieldError.getRejectedValue(),
                                fieldError.getDefaultMessage())
                ).collect(Collectors.toList());
        return new ApiError(errorInfors);
    }

    /**
     * Handle missing servlet response parameter api error.
     *
     * @param ex the ex
     * @return the api error
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingServletResponseParameter(MissingServletRequestParameterException ex) {
        ApiError.ErrorInfo errorInfo = new ApiError.ErrorInfo();
        errorInfo.setProperty(ex.getParameterName());
        errorInfo.setMessage(ex.getMessage());
        return ApiError.builder()
                .errors(List.of(errorInfo)).build();
    }

}
