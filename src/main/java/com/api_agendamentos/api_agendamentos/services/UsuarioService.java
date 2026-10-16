package com.api_agendamentos.api_agendamentos.services;

import com.api_agendamentos.api_agendamentos.dto.UsuarioCreateRequest;
import com.api_agendamentos.api_agendamentos.dto.UsuarioResponse;
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

        Usuario usuario = Usuario.builder()
                .nome(req.nome())
                .email(req.email())
                .senha(req.senha())
                .perfil(req.perfil())
                .build();

        usuario = repository.save(usuario);

        return new UsuarioResponse(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getPerfil());
    }
}