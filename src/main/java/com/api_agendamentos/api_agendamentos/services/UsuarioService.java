package com.api_agendamentos.api_agendamentos.services;

import com.api_agendamentos.api_agendamentos.dto.LoginRequest;
import com.api_agendamentos.api_agendamentos.dto.LoginResponse;
import com.api_agendamentos.api_agendamentos.dto.UsuarioCreateRequest;
import com.api_agendamentos.api_agendamentos.dto.UsuarioResponse;
import com.api_agendamentos.api_agendamentos.entity.PerfilUsuario;
import com.api_agendamentos.api_agendamentos.entity.Usuario;
import com.api_agendamentos.api_agendamentos.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final JwtService jwtService;

    public UsuarioService(UsuarioRepository repository, JwtService jwtService) {
        this.repository = repository;
        this.jwtService = jwtService;
    }

    @Transactional
    public UsuarioResponse criar(UsuarioCreateRequest req) {
        if (repository.findByEmail(req.email()).isPresent()) {
            throw new IllegalArgumentException("E-mail já cadastrado no sistema.");
        }

        PerfilUsuario perfil;
        String email = req.email().toLowerCase();
        //gera email de aluno
        if (email.endsWith("@aluno.ifsp.edu.br")) {
            perfil = PerfilUsuario.ALUNO;

        //gera email de professor
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

    public LoginResponse login(LoginRequest req) {
        String email = req == null || req.email() == null ? "" : req.email().trim();
        String senha = req == null || req.senha() == null ? "" : req.senha();

        Usuario usuario = email.isEmpty() ? null : repository.findByEmail(email).orElse(null);

        // Compara sempre, mesmo sem usuário, para o tempo de resposta não revelar se o e-mail existe.
        String senhaCadastrada = usuario != null ? usuario.getSenha() : "";
        boolean senhaConfere = MessageDigest.isEqual(
                senhaCadastrada.getBytes(StandardCharsets.UTF_8),
                senha.getBytes(StandardCharsets.UTF_8));

        // Mesma resposta para e-mail inexistente e senha errada: não indica onde está o erro.
        if (usuario == null || !senhaConfere) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        return new LoginResponse(jwtService.gerarToken(usuario));
    }
}