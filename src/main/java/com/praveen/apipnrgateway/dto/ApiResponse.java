package com.praveen.apipnrgateway.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private ResponseStatus status;
    private String message;     // Human-readable message
    private Instant timestamp;  // Crucial for production debugging & log tracing
    private T data;             // The actual payload (Invoice, List, etc.)

    // --- Success Helper Methods ---

    public static <T> ResponseEntity<ApiResponse<T>> ok(T data, String message) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse<>(ResponseStatus.SUCCESS, message, Instant.now(), data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(T data, String message) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(ResponseStatus.SUCCESS, message, Instant.now(), data));
    }

    // --- Error Helper Methods ---

    public static <T> ResponseEntity<ApiResponse<T>> badRequest(String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(ResponseStatus.FAILURE, message, Instant.now(), null));
    }

    public static <T> ResponseEntity<ApiResponse<T>> notFound(String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(ResponseStatus.FAILURE, message, Instant.now(), null));
    }

    // Overloaded badRequest if you *do* want to pass validation error details in 'data'
    public static <T> ResponseEntity<ApiResponse<T>> badRequest(T data, String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(ResponseStatus.FAILURE, message, Instant.now(), data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(String message) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(ResponseStatus.FAILURE, message, Instant.now(), null));
    }
}