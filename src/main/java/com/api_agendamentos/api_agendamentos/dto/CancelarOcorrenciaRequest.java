package com.api_agendamentos.api_agendamentos.dto;

import jakarta.validation.constraints.NotNull;

public record CancelarOcorrenciaRequest(
        @NotNull Long ocorrenciaId,
        @NotNull Long usuarioId
) {}