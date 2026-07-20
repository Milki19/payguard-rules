package com.payguard.demo.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ApiErrorResponse response = new ApiErrorResponse(
                "Bad Request",
                ex.getMessage()
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        StringBuilder message = new StringBuilder();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            if (message.length() > 0) {
                message.append("; ");
            }

            message.append(fieldError.getField())
                    .append(": ")
                    .append(fieldError.getDefaultMessage());
        }

        ApiErrorResponse response = new ApiErrorResponse(
                "Validation Failed",
                message.toString()
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ApiErrorResponse response = new ApiErrorResponse(
                "Not Found",
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalStateException(IllegalStateException ex) {
        ApiErrorResponse response = new ApiErrorResponse(
                "Rule Configuration Error",
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}
