package com.api_agendamentos.api_agendamentos.services;

import com.api_agendamentos.api_agendamentos.dto.UsuarioCreateRequest;
import com.api_agendamentos.api_agendamentos.dto.UsuarioResponse;
import com.api_agendamentos.api_agendamentos.entity.PerfilUsuario;
import com.api_agendamentos.api_agendamentos.entity.Usuario;
import com.api_agendamentos.api_agendamentos.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public UsuarioResponse criar(UsuarioCreateRequest req) {
        if (repository.findByEmail(req.email()).isPresent()) {
            throw new IllegalArgumentException("E-mail já cadastrado no sistema.");
        }

        PerfilUsuario perfil;
        String email = req.email().toLowerCase();

        if (email.endsWith("@aluno.ifsp.edu.br")) {
            perfil = PerfilUsuario.ALUNO;
        } else if (email.endsWith("@ifsp.edu.br")) {
            perfil = PerfilUsuario.PROFESSOR;
        } else {
            throw new IllegalArgumentException("Domínio de e-mail não permitido para cadastro.");
        }

        Usuario usuario = Usuario.builder()
                .nome(req.nome())
                .email(req.email())
                .senha(req.senha())
                .perfil(perfil)
                .build();

        usuario = repository.save(usuario);

        return new UsuarioResponse(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getPerfil());
    }
}