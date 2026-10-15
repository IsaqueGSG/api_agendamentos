package com.api_agendamentos.api_agendamentos.controller;

import com.api_agendamentos.api_agendamentos.dto.AgendamentoCreateRequest;
import com.api_agendamentos.api_agendamentos.dto.AgendamentoResponse;
import com.api_agendamentos.api_agendamentos.dto.AgendamentoUpdateRequest;
import com.api_agendamentos.api_agendamentos.services.AgendamentoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/agendamentos")
public class AgendamentoController {

    private final AgendamentoService service;

    public AgendamentoController(AgendamentoService service){
        this.service = service;
    }

    @PostMapping
    public AgendamentoResponse criar(@Valid @RequestBody AgendamentoCreateRequest req){
        return service.criar(req);
    }

    @PutMapping("/{id}")
    public AgendamentoResponse atualizar(@PathVariable Long id, @Valid @RequestBody AgendamentoUpdateRequest req){
        return service.atualizar(id, req);
    }

    @PutMapping("/{id}/cancelar")
    public AgendamentoResponse cancelar(@PathVariable Long id){
        return service.cancelar(id);
    }

    @PutMapping("/{id}/concluir")
    public AgendamentoResponse concluir(@PathVariable Long id){
        return service.concluir(id);
    }

    @GetMapping("/{id}")
    public AgendamentoResponse buscarPorId(@PathVariable long id){
        return service.buscarPorId(id);
    }


}
