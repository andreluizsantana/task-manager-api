package com.project.taskhub.dto;

import com.project.taskhub.entity.enums.TipoRecorrencia;

public record TaskGroupResponseDTO(Long id, TipoRecorrencia frequencia, Integer totalRecorrencia) {
}
