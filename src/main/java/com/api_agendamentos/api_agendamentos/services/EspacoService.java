package com.api_agendamentos.api_agendamentos.services;

import com.api_agendamentos.api_agendamentos.dto.EspacoCreateRequest;
import com.api_agendamentos.api_agendamentos.dto.EspacoResponse;
import com.api_agendamentos.api_agendamentos.entity.Espaco;
import com.api_agendamentos.api_agendamentos.repository.EspacoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class EspacoService {

    private final EspacoRepository repository;

    public EspacoService(EspacoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public EspacoResponse criar(EspacoCreateRequest req) {
        Espaco espaco = Espaco.builder()
                .nome(req.nome())
                .tipo(req.tipo())
                .capacidade(req.capacidade())
                .recursos(req.recursos())
                .bloqueado(false)
                .build();

        espaco = repository.save(espaco);

        return new EspacoResponse(
                espaco.getId(),
                espaco.getNome(),
                espaco.getTipo(),
                espaco.getCapacidade(),
                espaco.getRecursos(),
                espaco.isBloqueado()
        );
    }
}