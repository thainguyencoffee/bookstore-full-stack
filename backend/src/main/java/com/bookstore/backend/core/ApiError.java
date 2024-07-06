package com.bookstore.backend.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ApiError {
    private List<ErrorInfo> errors;

    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ErrorInfo {
        private String entity;
        private String property;
        private Object invalidValue;
        private String message;
    }
}
