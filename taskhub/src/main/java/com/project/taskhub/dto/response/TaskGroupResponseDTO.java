package com.project.taskhub.dto.response;

import com.project.taskhub.entity.enums.TipoRecorrencia;

public record TaskGroupResponseDTO(Long id, TipoRecorrencia frequency, Integer totalRecurrences) {}
