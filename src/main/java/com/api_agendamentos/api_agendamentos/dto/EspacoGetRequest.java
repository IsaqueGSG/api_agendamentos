package com.api_agendamentos.api_agendamentos.dto;

import com.api_agendamentos.api_agendamentos.entity.TipoEspaco;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record EspacoGetRequest(
        @NotNull LocalDateTime inicio,
        @NotNull LocalDateTime fim
) {}
