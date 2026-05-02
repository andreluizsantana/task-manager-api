package com.project.taskhub.exceptions;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.project.taskhub.dto.response.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(TaskNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleTaskNotFound(TaskNotFoundException e) {
    ErrorResponse erro =
        new ErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND.value(), LocalDateTime.now());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
  }

  @ExceptionHandler(TaskRecurrenceException.class)
  public ResponseEntity<ErrorResponse> handleTaskRecurrence(TaskRecurrenceException e) {
    ErrorResponse erro =
        new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
  }

  // Generica
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleTask(Exception e) {
    ErrorResponse erro =
        new ErrorResponse(
            e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
  }
}
