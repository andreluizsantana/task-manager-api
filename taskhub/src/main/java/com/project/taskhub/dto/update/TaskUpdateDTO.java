package com.project.taskhub.dto.update;

import com.project.taskhub.entity.enums.StatusTask;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record TaskUpdateDTO(
        @Size(max = 120, message = "O título deve ter no máximo 120 caracteres") String title,
        @Size(max = 1000, message = "A descrição deve ter no máximo 1000 caracteres")
                String description,
        StatusTask status,
        LocalDate executionDate) {}
