package com.bookstore.backend.core.exception;

import com.bookstore.backend.book.Book;
import com.bookstore.backend.core.ApiError;
import com.bookstore.backend.core.email.SendEmailFailureException;
import com.bookstore.backend.core.exception.purchaseorder.OrderStatusNotMatchException;
import com.bookstore.backend.core.exception.purchaseorder.OtpExpiredException;
import com.bookstore.backend.core.exception.purchaseorder.OtpIncorrectException;
import com.bookstore.backend.core.exception.shoppingcart.ShoppingCartAlreadyExistingException;
import com.bookstore.backend.shopppingcart.ShoppingCart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class GeneralExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomNoResultException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNoResultException(CustomNoResultException e) {
        ApiError.ErrorInfo errorInfo = new ApiError.ErrorInfo();
        errorInfo.setMessage(e.getMessage());
        errorInfo.setEntity(e.getClazz().getSimpleName());
        return new ApiError(List.of(errorInfo));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        List<ApiError.ErrorInfo> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            FieldError fieldError = ((FieldError) error);
            errors.add(new ApiError.ErrorInfo(error.getObjectName(), fieldError.getField(), fieldError.getRejectedValue(), error.getDefaultMessage()));
        });
        return new ResponseEntity<>(new ApiError(errors), headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ApiError.ErrorInfo errorInfo = new ApiError.ErrorInfo();
        errorInfo.setProperty(ex.getParameterName());
        errorInfo.setMessage(ex.getMessage());
        return new ResponseEntity<>(new ApiError(List.of(errorInfo)), headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ApiError.ErrorInfo errorInfo = new ApiError.ErrorInfo();
        errorInfo.setProperty(ex.getPropertyName());
        errorInfo.setMessage(ex.getPropertyName() + " should be of type " + Objects.requireNonNull(ex.getRequiredType()).getSimpleName());
        return new ResponseEntity<>(new ApiError(List.of(errorInfo)), headers, status);
    }

    @ExceptionHandler(BookNotEnoughInventoryException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleBookNotEnoughInventoryException(BookNotEnoughInventoryException ex) {
        ApiError.ErrorInfo errorInfo = new ApiError.ErrorInfo();
        errorInfo.setEntity(Book.class.getSimpleName());
        errorInfo.setMessage(ex.getMessage());
        return new ResponseEntity<>(new ApiError(List.of(errorInfo)), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SendEmailFailureException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleSendEmailFailureException(SendEmailFailureException ex) {
        ApiError.ErrorInfo errorInfo = new ApiError.ErrorInfo();
        errorInfo.setMessage(ex.getMessage());
        return new ResponseEntity<>(new ApiError(List.of(errorInfo)), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(OrderStatusNotMatchException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleOrderStatusNotMatch(OrderStatusNotMatchException ex) {
        ApiError.ErrorInfo errorInfo = new ApiError.ErrorInfo();
        errorInfo.setMessage(ex.getMessage());
        return new ResponseEntity<>(new ApiError(List.of(errorInfo)), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(OtpExpiredException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleOtpExpired(OtpExpiredException ex) {
        ApiError.ErrorInfo errorInfo = new ApiError.ErrorInfo();
        errorInfo.setMessage(ex.getMessage());
        return new ResponseEntity<>(new ApiError(List.of(errorInfo)), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OtpIncorrectException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleOtpIncorrect(OtpIncorrectException ex) {
        ApiError.ErrorInfo errorInfo = new ApiError.ErrorInfo();
        errorInfo.setMessage(ex.getMessage());
        return new ResponseEntity<>(new ApiError(List.of(errorInfo)), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ShoppingCartAlreadyExistingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleShoppingCartAlreadyException(ShoppingCartAlreadyExistingException ex) {
        ApiError.ErrorInfo errorInfo = new ApiError.ErrorInfo();
        errorInfo.setEntity(ShoppingCart.class.getSimpleName());
        errorInfo.setMessage(ex.getMessage());
        return new ResponseEntity<>(new ApiError(List.of(errorInfo)), HttpStatus.BAD_REQUEST);
    }
}
