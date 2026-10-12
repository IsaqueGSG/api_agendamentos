package com.api_agendamentos.api_agendamentos.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AgendamentoCreateRequest(
	@NotBlank @Size(max = 120) String titulo,
	@Size(max = 4000) String descricao,
	@NotNull LocalDateTime dataInicio,
	@NotNull LocalDateTime dataFim,
	@NotNull @Size(max = 80) String usuario

) {
	
}
