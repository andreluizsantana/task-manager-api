package com.project.taskhub.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record RegisterUserRequestDTO(
        @NotEmpty(message = "Nome obrigatório.") String name,
        @NotEmpty(message = "Email obrigatório.") String email,
        @NotEmpty(message = "Senha obrigatório.") String password) {}
