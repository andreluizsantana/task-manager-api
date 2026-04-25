package com.project.taskhub.dto;

import java.time.LocalDate;

import com.project.taskhub.entity.enums.StatusTask;

public record TaskUpdateDTO(String titulo, String descricao, StatusTask status, LocalDate dataExecucao) {
}
