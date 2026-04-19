package com.project.taskhub.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<String> handleTaskService(TaskNotFoundException e) {
	final String erro = e.getMessage();
	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

}
