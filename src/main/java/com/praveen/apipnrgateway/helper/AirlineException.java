package com.praveen.apipnrgateway.helper;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AirlineException extends RuntimeException {
    private String message;
    private HttpStatus httpStatus;
    public AirlineException(String message,HttpStatus httpStatus,Throwable throwable){
        super(message,throwable);
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public AirlineException(String message,HttpStatus httpStatus){
        super(message);
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public static AirlineException notFound(String message,Exception e){
        return new AirlineException(message,HttpStatus.NOT_FOUND,e);
    }

    public static AirlineException notFound(String message){
        return new AirlineException(message,HttpStatus.NOT_FOUND);
    }

    public static AirlineException serverError(String message,Exception e){
        return new AirlineException(message,HttpStatus.INTERNAL_SERVER_ERROR,e);
    }

    public static AirlineException serverError(String message){
        return new AirlineException(message,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static AirlineException badRequest(String message,Exception e){
        return new AirlineException(message,HttpStatus.BAD_REQUEST,e);
    }

    public static AirlineException badRequest(String message){
        return new AirlineException(message,HttpStatus.BAD_REQUEST);
    }

    public static AirlineException forbidden(String message){
        return new AirlineException(message,HttpStatus.FORBIDDEN);
    }

    public static AirlineException unAuthorize(String message){
        return new AirlineException(message,HttpStatus.UNAUTHORIZED);
    }

    public static AirlineException conflict(String message,Exception e){
        return new AirlineException(message,HttpStatus.CONFLICT,e);
    }
}
