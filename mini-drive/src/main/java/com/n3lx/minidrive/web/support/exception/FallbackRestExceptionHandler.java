package com.n3lx.minidrive.web.support.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.Timestamp;
import java.time.Instant;

@RestControllerAdvice
@Order(1)
@Slf4j
public class FallbackRestExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception exception) {
        log.debug("Exception has been handled by generic handler", exception);
        var errorMessage = RestErrorMessage.builder()
                .timestamp(Timestamp.from(Instant.now()))
                .message("Something went wrong, please contact the administrator " +
                        "and provide them with the details of your request so that it can be diagnosed")
                .build();

        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
