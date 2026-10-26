package com.api_agendamentos.api_agendamentos.dto;

import com.api_agendamentos.api_agendamentos.entity.PerfilUsuario;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        PerfilUsuario perfil
) {}