package com.api_agendamentos.api_agendamentos.dto;

import java.time.LocalDateTime;

import com.api_agendamentos.api_agendamentos.entity.StatusAgendamentos;

public record AgendamentoResponse(
		Long id,
		String titulo,
		String descricao,
		LocalDateTime datainicio,
		LocalDateTime datafim,
		StatusAgendamentos status,
		String usuario,
		LocalDateTime criadoEm,
		LocalDateTime atualizadoEm
) {

}

