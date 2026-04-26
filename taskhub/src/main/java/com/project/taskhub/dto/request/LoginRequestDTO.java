package com.project.taskhub.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record LoginRequestDTO(@NotEmpty(message = "Email obrigatório.") String email, @NotEmpty(message = "Senha obrigatória.") String password) {

}
