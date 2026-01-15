package com.example.tinyledger.ledger.controller.exception;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public record ApiError(ErrorCode code, String message, List<ApiFieldError> details) {
    public static ApiError of(ErrorCode code, String message) {
        return new ApiError(code, message, List.of());
    }

    public static ApiError from(ErrorCode errorCode, String message, BindingResult bindingResult) {
        var errors = bindingResult.getFieldErrors().stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField, Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())))
                .entrySet()
                .stream()
                .map(entry -> new ApiError.ApiFieldError(entry.getKey(), entry.getValue()))
                .toList();

        return new ApiError(errorCode, message, errors);
    }

    public record ApiFieldError(String field, List<String> messages) {}

    public enum ErrorCode {
        ENTITY_NOT_FOUND,
        INVALID_MONEY_OPERATION,
        BAD_REQUEST,
        INTERNAL_SERVER_ERROR
    }
}
