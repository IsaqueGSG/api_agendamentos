package com.api_agendamentos.api_agendamentos.dto;

import com.api_agendamentos.api_agendamentos.entity.PerfilUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioCreateRequest(
        @NotBlank @Size(max = 120) String nome,
        @NotBlank @Email @Size(max = 120) String email,
        @NotBlank @Size(min = 8, max = 255) String senha,
        @NotNull PerfilUsuario perfil
) {}