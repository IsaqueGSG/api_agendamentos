package com.api_agendamentos.api_agendamentos.dto;

import jakarta.validation.constraints.NotNull;

public record CheckInRequest(
        @NotNull Long reservaId,
        @NotNull Long usuarioId
) {}