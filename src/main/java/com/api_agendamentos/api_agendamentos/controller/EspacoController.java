package com.api_agendamentos.api_agendamentos.controller;

import com.api_agendamentos.api_agendamentos.dto.EspacoCreateRequest;
import com.api_agendamentos.api_agendamentos.dto.EspacoGetRequest;
import com.api_agendamentos.api_agendamentos.dto.EspacoResponse;
import com.api_agendamentos.api_agendamentos.entity.Espaco;
import com.api_agendamentos.api_agendamentos.entity.TipoEspaco;
import com.api_agendamentos.api_agendamentos.services.EspacoService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/espacos")
public class EspacoController {

    private final EspacoService service;

    public EspacoController(EspacoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EspacoResponse criar(@Valid @RequestBody EspacoCreateRequest req) {
        return service.criar(req);
    }

    @PostMapping("/livres")
    public List<EspacoResponse> buscarEspacosLivres(@Valid @RequestBody EspacoGetRequest req) {
        return service.buscarEspacosLivres(
                req.tipo(),
                req.capacidadeMin(),
                req.recurso(),
                req.inicio(),
                req.fim()
        );
    }

}