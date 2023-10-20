package com.n3lx.minidrive.web.support.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;

@RestControllerAdvice
@Order(0)
public class RestExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException exception) {
        var errorMessage = RestErrorMessage.builder()
                .timestamp(Timestamp.from(Instant.now()))
                .message(exception.getMessage())
                .build();

        return new ResponseEntity<>(errorMessage, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(FileAlreadyExistsException.class)
    public ResponseEntity<Object> handleFileAlreadyExistsException(FileAlreadyExistsException exception) {
        var filename = Paths.get(exception.getFile()).getFileName().toString();
        var errorMessage = RestErrorMessage.builder()
                .timestamp(Timestamp.from(Instant.now()))
                .message("File \"" + filename + "\" already exists")
                .build();

        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchFileException.class)
    public ResponseEntity<Object> handleNoSuchFileException(NoSuchFileException exception) {
        var filename = Paths.get(exception.getFile()).getFileName().toString();
        var errorMessage = RestErrorMessage.builder()
                .timestamp(Timestamp.from(Instant.now()))
                .message("File \"" + filename + "\" does not exist")
                .build();

        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException exception) {
        var violationMessages = new HashMap<>();

        for (var violation : exception.getConstraintViolations()) {
            violationMessages.put(String.valueOf(violation.getPropertyPath()), violation.getMessage());
        }

        var formattedMessage = violationMessages
                .toString()
                .substring(1,violationMessages.toString().length()-1)
                .replace("="," ");

        var errorMessage = RestErrorMessage.builder()
                .timestamp(Timestamp.from(Instant.now()))
                .message(formattedMessage)
                .build();

        return new ResponseEntity<>(errorMessage, HttpStatus.FORBIDDEN);
    }

}
