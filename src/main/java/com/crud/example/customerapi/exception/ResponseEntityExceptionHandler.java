package com.crud.example.customerapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;

@RestControllerAdvice
public class ResponseEntityExceptionHandler {

    /* Handle a Constraint Violation to return a
    Bad Request instead of Internal Server Error */
    @ExceptionHandler
    public ResponseEntity<Object> handle(ConstraintViolationException exception) {
        ConstraintViolation constraintViolation = new ArrayList<>(exception.getConstraintViolations()).get(0);
        String errorMessage = constraintViolation.getPropertyPath() + " " + constraintViolation.getMessage();
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }
}

