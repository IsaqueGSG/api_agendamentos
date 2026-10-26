package com.api_agendamentos.api_agendamentos.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record ReservaAvulsaRequest(
        @NotNull Long espacoId,
        @NotNull LocalDateTime dataInicio,
        @NotNull LocalDateTime dataFim
) {}