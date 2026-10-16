package com.api_agendamentos.api_agendamentos.controller;

import com.api_agendamentos.api_agendamentos.dto.ReservaAvulsaRequest;
import com.api_agendamentos.api_agendamentos.dto.ReservaRecorrenteRequest;
import com.api_agendamentos.api_agendamentos.entity.Ocorrencia;
import com.api_agendamentos.api_agendamentos.services.ReservaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService service;

    public ReservaController(ReservaService service) {
        this.service = service;
    }

    @PostMapping("/avulsa")
    @ResponseStatus(HttpStatus.CREATED)
    public Ocorrencia criarReservaAvulsa(@Valid @RequestBody ReservaAvulsaRequest req) {
        return service.criarReservaAvulsa(
                req.usuarioId(),
                req.espacoId(),
                req.dataInicio(),
                req.dataFim()
        );
    }

    @PostMapping("/recorrente")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Ocorrencia> criarReservaRecorrente(@Valid @RequestBody ReservaRecorrenteRequest req) {
        return service.criarReservaRecorrente(
                req.usuarioId(),
                req.espacoId(),
                req.dataInicio(),
                req.dataFim(),
                req.dataLimite()
        );
    }
}