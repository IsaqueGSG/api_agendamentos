package com.api_agendamentos.api_agendamentos.dto;

public record LoginRequest(
        String email,
        String senha
) {}