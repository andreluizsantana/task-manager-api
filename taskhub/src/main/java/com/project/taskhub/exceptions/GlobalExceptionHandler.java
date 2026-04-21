package com.project.taskhub.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(TaskNotFoundException.class)
    // Lembrar de usar Map -> extrair status e message separado
    public ResponseEntity<Map<String, String>> handleTaskService(TaskNotFoundException e) {
	Map<String, String> erro = new HashMap<>();
	erro.put("status", "404");
	erro.put("message", e.getMessage());
	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(TaskRecurrenceException.class)
    public ResponseEntity<Map<String, String>> handleTaskRecurrence(TaskRecurrenceException e) {
	Map<String, String> erro = new HashMap<>();
	erro.put("status", "400");
	erro.put("message", e.getMessage());
	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

}
