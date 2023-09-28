package com.n3lx.minidrive.web.support;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.sql.Timestamp;
import java.time.Instant;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleGenericException(Exception exception) {
        var errorMessage = RestErrorMessage.builder()
                .timestamp(Timestamp.from(Instant.now()))
                .message(exception.getMessage())
                .build();

        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

}
