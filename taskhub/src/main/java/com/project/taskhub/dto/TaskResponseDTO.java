package com.project.taskhub.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.project.taskhub.entity.TaskGroup;
import com.project.taskhub.entity.enums.StatusTask;

public record TaskResponseDTO(

	Long id, String titulo, String descricao, StatusTask status, LocalDateTime criadoEm, LocalDateTime atualizadoEm, Integer ocorrencia, LocalDate dataExecucao, TaskGroup taskGroup) {
}