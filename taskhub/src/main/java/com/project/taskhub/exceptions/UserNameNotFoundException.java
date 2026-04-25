package com.project.taskhub.exceptions;

public class UserNameNotFoundException extends RuntimeException {

    public UserNameNotFoundException(String username) {
	super("Usuário não encontrado.");
    }
}
