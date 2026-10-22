package com.project.taskhub.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequestDTO(
        @NotBlank(message = "Nome obrigatório.") @Size(max = 120) String name,
        @NotBlank(message = "Email obrigatório.")
                @Email(message = "Email inválido.")
                @Size(max = 254)
                String email,
        @NotBlank(message = "Senha obrigatória.") @Size(min = 8, max = 72) String password) {}
