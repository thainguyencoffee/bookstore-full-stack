package com.bookstore.resourceserver.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {
    private List<ErrorInfo> errors = new ArrayList<>();

    public void addError(ErrorInfo err) {
        this.errors.add(err);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorInfo {
        private String entity;
        private String property;
        private Object invalidValue;
        private String message;
    }

}
