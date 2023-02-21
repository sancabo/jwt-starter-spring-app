package com.jwt.app.web.controller;


import com.jwt.app.web.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice("com.jwt.app.web.controller")
public class JWTApiControllerAdvice {

    @ResponseStatus(HttpStatus.FORBIDDEN)  // 409
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exceptionHandler(RuntimeException ex, WebRequest request) {
        var response = new ErrorResponse();
        response.setCode(HttpStatus.FORBIDDEN.toString());
        //We don't want to expose implementation details to the user
        //This message should be tailored to something that they can understand
        response.setMessage(ex.getLocalizedMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
}
