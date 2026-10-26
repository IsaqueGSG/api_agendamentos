package com.api_agendamentos.api_agendamentos.controller;

import com.api_agendamentos.api_agendamentos.dto.CancelarReservaRequest;
import com.api_agendamentos.api_agendamentos.dto.CheckInRequest;
import com.api_agendamentos.api_agendamentos.dto.ReservaAvulsaRequest;
import com.api_agendamentos.api_agendamentos.dto.ReservaRecorrenteRequest;
import com.api_agendamentos.api_agendamentos.entity.Reserva;
import com.api_agendamentos.api_agendamentos.services.ReservaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService service;

    public ReservaController(ReservaService service) {
        this.service = service;
    }

    @PostMapping("/avulsa")
    @ResponseStatus(HttpStatus.CREATED)
    public Reserva criarReservaAvulsa(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody ReservaAvulsaRequest req) {
        return service.criarReservaAvulsa(
                authorization,
                req.espacoId(),
                req.dataInicio(),
                req.dataFim()
        );
    }

    @PostMapping("/recorrente")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Reserva> criarReservaRecorrente(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody ReservaRecorrenteRequest req) {
        return service.criarReservaRecorrente(
                authorization,
                req.espacoId(),
                req.dataInicio(),
                req.dataFim(),
                req.dataLimite()
        );
    }

    @PostMapping("/cancelar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelarReserva(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody CancelarReservaRequest req) {
        service.cancelarReserva(req.reservaId(), authorization);
    }

    @PostMapping("/check-in")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void realizarCheckIn(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody CheckInRequest req) {
        service.realizarCheckIn(req.reservaId(), authorization);
    }


    // rotas do gestor
    @GetMapping("/pendentes")
    public List<Reserva> listarPendentes(@RequestHeader(value = "Authorization", required = false) String authorization) {
        return service.listarPendentes(authorization);
    }

    @PostMapping("/aprovar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void aprovarReserva(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody Map<String, Long> request) {
        service.gerenciarAprovacao(request.get("reservaId"), authorization, true);
    }

    @PostMapping("/rejeitar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rejeitarReserva(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody Map<String, Long> request) {
        service.gerenciarAprovacao(request.get("reservaId"), authorization, false);
    }

    //relatorios
    @GetMapping("/relatorios/ocupacao")
    public List<Reserva> relatorioOcupacao(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam LocalDateTime inicio,
            @RequestParam LocalDateTime fim) {
        return service.relatorioOcupacao(authorization, inicio, fim);
    }

    @GetMapping("/relatorios/faltas")
    public List<Reserva> relatorioFaltas(@RequestHeader(value = "Authorization", required = false) String authorization) {
        return service.relatorioFaltas(authorization);
    }

    @GetMapping("/relatorios/usuario")
    public List<Reserva> relatorioPorUsuario(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam Long usuarioAlvoId) {
        return service.relatorioPorUsuario(authorization, usuarioAlvoId);
    }


}