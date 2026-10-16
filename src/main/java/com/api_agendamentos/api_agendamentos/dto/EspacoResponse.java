package com.api_agendamentos.api_agendamentos.dto;

import com.api_agendamentos.api_agendamentos.entity.TipoEspaco;

public record EspacoResponse(
        Long id,
        String nome,
        TipoEspaco tipo,
        Integer capacidade,
        String recursos,
        boolean bloqueado
) {}