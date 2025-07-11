package com.example.demo.handler;

import com.example.demo.controller.BookController;
import com.example.demo.exception.PathVariableAndRequestBodyInconsistentException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(assignableTypes = BookController.class)
public class BookControllerExceptionHandler {
    @ExceptionHandler(PathVariableAndRequestBodyInconsistentException.class)
    public ResponseEntity<String> handle(PathVariableAndRequestBodyInconsistentException e) {
        return ResponseEntity.badRequest().body("Path id and request body id are different");
    }
}
