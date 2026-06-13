package com.praveen.apipnrgateway.helper;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionHandlers {

    @ExceptionHandler({AirlineException.class})
    public ResponseEntity<ErrorResponse> handleException(AirlineException airlineException){
        return new ResponseEntity<>(ErrorResponse.builder().message(airlineException.getMessage()).timestamp(LocalDateTime.now().toString()).build(),airlineException.getHttpStatus());
    }
}
