package com.api_agendamentos.api_agendamentos.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record ReservaRecorrenteRequest(
        @NotNull Long espacoId,
        @NotNull LocalDateTime dataInicio,
        @NotNull LocalDateTime dataFim,
        @NotNull LocalDateTime dataLimite
) {}