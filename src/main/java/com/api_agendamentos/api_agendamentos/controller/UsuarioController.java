package com.api_agendamentos.api_agendamentos.controller;

import com.api_agendamentos.api_agendamentos.dto.LoginRequest;
import com.api_agendamentos.api_agendamentos.dto.LoginResponse;
import com.api_agendamentos.api_agendamentos.dto.UsuarioCreateRequest;
import com.api_agendamentos.api_agendamentos.dto.UsuarioResponse;
import com.api_agendamentos.api_agendamentos.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse criar(@Valid @RequestBody UsuarioCreateRequest req) {
        return service.criar(req);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest req) {
        return service.login(req);
    }
}