package com.example.demo.handler;

import com.example.demo.controller.AuthorController;
import com.example.demo.exception.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice (assignableTypes = AuthorController.class)
public class AuthorControllerExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handle(EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
    }

}
