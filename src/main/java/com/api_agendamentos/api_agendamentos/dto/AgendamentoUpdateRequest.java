package com.api_agendamentos.api_agendamentos.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Size;

public record AgendamentoUpdateRequest(
		@Size(max = 120) String titulo,
		@Size(max = 4000) String descricao,
		LocalDateTime dataInicio,
		LocalDateTime dataFim
) {

}
