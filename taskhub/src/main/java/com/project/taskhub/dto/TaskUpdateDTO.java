package com.project.taskhub.dto;

import com.project.taskhub.entity.enums.StatusTask;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskUpdateDTO(
	@NotBlank(message = "O título não pode estar vazio") @Size(max = 120, message = "O título deve ter no máximo 120 caracteres") String titulo,

	@NotBlank(message = "A descrição não pode estar vazia") String descricao,

	StatusTask status) {
}
