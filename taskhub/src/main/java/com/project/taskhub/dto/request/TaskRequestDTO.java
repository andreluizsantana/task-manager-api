package com.project.taskhub.dto.request;

import com.project.taskhub.entity.enums.TipoRecorrencia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record TaskRequestDTO(
        @NotBlank(message = "O título não pode estar vazio")
                @Size(max = 120, message = "O título deve ter no máximo 120 caracteres")
                String title,
        @NotBlank(message = "A descrição não pode estar vazia") String description,
        @NotNull(message = "O tipo de recorrência é obrigatório") TipoRecorrencia recurrenceType,
        Integer totalRecurrences,
        LocalDate executionDate) {}
