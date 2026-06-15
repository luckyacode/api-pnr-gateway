package com.praveen.apipnrgateway.helper;

import com.praveen.apipnrgateway.dto.ApiResponse;
import com.praveen.apipnrgateway.dto.ResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionHandlers {

    @ExceptionHandler({AirlineException.class})
    public ResponseEntity<ApiResponse<?>> handleException(AirlineException airlineException){
        return ResponseEntity.status(airlineException.getHttpStatus()).body(
                new ApiResponse<>(ResponseStatus.FAILURE,airlineException.getMessage(), Instant.now(),null));
    }
}
