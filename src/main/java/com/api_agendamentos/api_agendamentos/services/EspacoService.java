package com.api_agendamentos.api_agendamentos.services;

import com.api_agendamentos.api_agendamentos.dto.EspacoCreateRequest;
import com.api_agendamentos.api_agendamentos.dto.EspacoResponse;
import com.api_agendamentos.api_agendamentos.entity.Espaco;
import com.api_agendamentos.api_agendamentos.entity.TipoEspaco;
import com.api_agendamentos.api_agendamentos.repository.EspacoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EspacoService {

    private final EspacoRepository repository;
    private final JwtService jwtService;

    public EspacoService(EspacoRepository repository, JwtService jwtService) {
        this.repository = repository;
        this.jwtService = jwtService;
    }

    @Transactional
    public EspacoResponse criar(String token, EspacoCreateRequest req) {
        jwtService.validar(token); // exige token válido (lança 401 caso contrário)

        Espaco espaco = Espaco.builder()
                .nome(req.nome())
                .tipo(req.tipo())
                .capacidade(req.capacidade())
                .recursos(req.recursos())
                .bloqueado(false)
                .build();

        espaco = repository.save(espaco);

        return mapearParaResponse(espaco);
    }

    public List<EspacoResponse> buscarEspacosLivres(
            String token,
            TipoEspaco tipo,
            Integer capacidadeMin,
            String recurso,
            LocalDateTime inicio,
            LocalDateTime fim
    ) {
        jwtService.validar(token); // exige token válido (lança 401 caso contrário)

        if (inicio == null || fim == null || inicio.isAfter(fim)) {
            throw new IllegalArgumentException("O intervalo de datas é inválido.");
        }

        List<Espaco> espacos = repository.buscarEspacosLivres(
                tipo,
                capacidadeMin,
                recurso,
                inicio,
                fim
        );

        return espacos.stream()
                .map(this::mapearParaResponse)
                .collect(Collectors.toList());
    }

    private EspacoResponse mapearParaResponse(Espaco espaco) {
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