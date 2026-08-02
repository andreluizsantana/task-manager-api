package com.project.taskhub.dto.response;

import com.project.taskhub.entity.enums.StatusTask;
import com.project.taskhub.entity.enums.TipoRecorrencia;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskResponseDTO(
        Long id,
        String title,
        String description,
        StatusTask status,
        TipoRecorrencia recurrenceType,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Integer occurrence,
        LocalDate executionDate,
        TaskGroupResponseDTO taskGroup) {}
