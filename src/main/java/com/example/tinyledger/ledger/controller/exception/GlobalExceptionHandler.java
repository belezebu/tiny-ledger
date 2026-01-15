/* (C) 2026 */
package com.example.tinyledger.ledger.controller.exception;

import com.example.tinyledger.common.exception.EntityNotFoundException;
import com.example.tinyledger.common.exception.InvalidMoneyOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleEntityNotFound(EntityNotFoundException ex) {
        return ApiError.of(ApiError.ErrorCode.ENTITY_NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(InvalidMoneyOperationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleInvalidMoneyOperationException(InvalidMoneyOperationException ex) {
        return ApiError.of(ApiError.ErrorCode.INVALID_MONEY_OPERATION, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return ApiError.from(ApiError.ErrorCode.BAD_REQUEST, "Request body is invalid", ex.getBindingResult());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleRuntimeException(Exception ex) {
        LOGGER.error(ex.getMessage(), ex);
        return ApiError.of(ApiError.ErrorCode.INTERNAL_SERVER_ERROR, "Internal server error");
    }
}
