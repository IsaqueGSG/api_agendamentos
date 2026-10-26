package com.api_agendamentos.api_agendamentos.dto;

import com.api_agendamentos.api_agendamentos.entity.TipoEspaco;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EspacoCreateRequest(
        @NotBlank @Size(max = 120) String nome,
        @NotNull TipoEspaco tipo,
        @NotNull @Min(1) Integer capacidade,
        String recursos
) {}