package com.project.taskhub.dto;

import com.project.taskhub.entity.enums.TipoRecorrencia;

public record TaskGroupResponseDTO(TipoRecorrencia frequencia, Integer totalRecorrencia) {
}
