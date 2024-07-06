package com.bookstore.backend.core.exception;

import com.bookstore.backend.core.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;

public interface ControllerAdviceConfig {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    default ApiError handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
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

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    default ApiError handleMissingServletRequestParameter(MissingServletRequestParameterException ex) {
        ApiError.ErrorInfo errorInfo = new ApiError.ErrorInfo();
        errorInfo.setProperty(ex.getParameterName());
        errorInfo.setMessage(ex.getMessage());
        return ApiError.builder()
                .errors(List.of(errorInfo)).build();
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    default ApiError handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        ApiError.ErrorInfo errorInfo = new ApiError.ErrorInfo();
        errorInfo.setProperty(ex.getName());
        errorInfo.setMessage(ex.getName() + " should be of type " + ex.getRequiredType().getSimpleName());
        return ApiError.builder()
                .errors(List.of(errorInfo)).build();
    }
}
