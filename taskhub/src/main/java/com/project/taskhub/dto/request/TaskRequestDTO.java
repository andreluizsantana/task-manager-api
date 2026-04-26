package com.project.taskhub.dto.request;

import java.time.LocalDate;

import com.project.taskhub.entity.enums.StatusTask;
import com.project.taskhub.entity.enums.TipoRecorrencia;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskRequestDTO(@NotBlank(message = "O título não pode estar vazio") @Size(max = 120, message = "O título deve ter no máximo 120 caracteres") String titulo,

	@NotBlank(message = "A descrição não pode estar vazia") String descricao,

	@Enumerated(EnumType.STRING) @Column(nullable = false) StatusTask status,

	@Enumerated(EnumType.STRING) @Column(nullable = false) TipoRecorrencia tipoRecorrencia,

	@Column(nullable = false) Integer totalRecorrencia,

	LocalDate dataExecucao

) {
}