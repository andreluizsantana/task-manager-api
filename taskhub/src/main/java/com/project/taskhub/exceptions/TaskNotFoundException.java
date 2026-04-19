package com.project.taskhub.exceptions;

public class TaskNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TaskNotFoundException(Long id) {
	super("Task com ID " + id + " não encontrada.");

    }

}
