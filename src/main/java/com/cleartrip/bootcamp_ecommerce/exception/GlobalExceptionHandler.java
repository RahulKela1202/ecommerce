package com.cleartrip.bootcamp_ecommerce.exception;

import com.cleartrip.bootcamp_ecommerce.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle general exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("Failure", null, e.getMessage()));
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ApiResponse<Object>> unauthorisedExecption(UnauthorizedAccessException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("Failure",null,e.getMessage()));
    }

    // Handle resource not found exceptions (e.g., User/Product not found)
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>("Failure", null, e.getMessage()));
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ApiResponse<Object>> conflictException(DuplicateException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>("Failure", null, e.getMessage()));
    }

    // Handle bad request exceptions (e.g., invalid input)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>("Failure", null, e.getMessage()));
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiResponse<Object>> invalidRequest(InvalidRequestException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                body(new ApiResponse<>("Failure", null, e.getMessage()));
    }

    // Handle validation errors (e.g., @Valid or @NotNull errors)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>("Failure", errors, "Validation failed"));
    }
}

