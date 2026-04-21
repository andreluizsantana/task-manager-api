package com.project.taskhub.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TaskRecurrenceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TaskRecurrenceException(String message) {
	super(message);
    }
}