package com.project.taskhub.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.project.taskhub.entity.TaskGroup;
import com.project.taskhub.entity.enums.StatusTask;
import com.project.taskhub.entity.enums.TipoRecorrencia;

public record TaskResponseDTO(

	Long id, String titulo, String descricao, StatusTask status, TipoRecorrencia tipoRecorrencia, LocalDateTime criadoEm, LocalDateTime atualizadoEm, Integer ocorrencia, LocalDate dataExecucao,
	TaskGroup taskGroup) {
}