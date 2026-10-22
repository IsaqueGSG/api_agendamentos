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
    public Reserva criarReservaAvulsa(@Valid @RequestBody ReservaAvulsaRequest req) {
        return service.criarReservaAvulsa(
                req.usuarioId(),
                req.espacoId(),
                req.dataInicio(),
                req.dataFim()
        );
    }

    @PostMapping("/recorrente")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Reserva> criarReservaRecorrente(@Valid @RequestBody ReservaRecorrenteRequest req) {
        return service.criarReservaRecorrente(
                req.usuarioId(),
                req.espacoId(),
                req.dataInicio(),
                req.dataFim(),
                req.dataLimite()
        );
    }

    @PostMapping("/cancelar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelarReserva(@Valid @RequestBody CancelarReservaRequest req) {
        service.cancelarReserva(req.reservaId(), req.usuarioId());
    }

    @PostMapping("/check-in")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void realizarCheckIn(@Valid @RequestBody CheckInRequest req) {
        service.realizarCheckIn(req.reservaId(), req.usuarioId());
    }


    // rotas do gestor
    @GetMapping("/pendentes")
    public List<Reserva> listarPendentes(@RequestParam Long usuarioId) {
        return service.listarPendentes(usuarioId);
    }

    @PostMapping("/aprovar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void aprovarReserva(@RequestBody Map<String, Long> request) {
        service.gerenciarAprovacao(request.get("reservaId"), request.get("usuarioId"), true);
    }

    @PostMapping("/rejeitar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rejeitarReserva(@RequestBody Map<String, Long> request) {
        service.gerenciarAprovacao(request.get("reservaId"), request.get("usuarioId"), false);
    }
}