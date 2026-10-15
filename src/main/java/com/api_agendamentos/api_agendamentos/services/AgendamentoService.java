package com.api_agendamentos.api_agendamentos.services;

import com.api_agendamentos.api_agendamentos.dto.AgendamentoCreateRequest;
import com.api_agendamentos.api_agendamentos.dto.AgendamentoResponse;
import com.api_agendamentos.api_agendamentos.dto.AgendamentoUpdateRequest;
import com.api_agendamentos.api_agendamentos.entity.Agendamento;
import com.api_agendamentos.api_agendamentos.entity.StatusAgendamentos;
import com.api_agendamentos.api_agendamentos.mapper.AgendamentoMapper;
import com.api_agendamentos.api_agendamentos.repository.AgendamentoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AgendamentoService {

    private final AgendamentoRepository repository;

    public AgendamentoService( AgendamentoRepository repo){
        this.repository = repo;
    }

    @Transactional
    public AgendamentoResponse criar(@Valid AgendamentoCreateRequest req){

        validarIntervalo(req.dataInicio(), req.dataFim());
        checarConflito(req.usuario(), req.dataInicio(), req.dataFim(), null);

        Agendamento entity = AgendamentoMapper.toEntity(req);
        entity = repository.save(entity);
        return AgendamentoMapper.toResponse(entity);
    }

    @Transactional
    public AgendamentoResponse atualizar(Long id, AgendamentoUpdateRequest req){
        Agendamento entity = repository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Agendamento nao encontrado"));

        AgendamentoMapper.merge(entity, req);

        validarIntervalo(req.dataInicio(), req.dataFim());
        checarConflito(entity.getUsuario(), req.dataInicio(), req.dataFim(), entity.getId());

        entity = repository.save(entity);

        return AgendamentoMapper.toResponse(entity);
    }

    @Transactional
    public AgendamentoResponse concluir(Long id ){
        Agendamento entity = repository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Agendamento nao encontrado"));

        entity.setStatus( StatusAgendamentos.CONCLUIDO );
        entity = repository.save(entity);
        return AgendamentoMapper.toResponse(entity);
    }

    @Transactional
    public AgendamentoResponse buscarPorId(Long id ){
        Agendamento entity = repository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Agendamento nao encontrado"));
        return AgendamentoMapper.toResponse(entity);
    }

    @Transactional
    public AgendamentoResponse cancelar(Long id ){
        Agendamento entity = repository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Agendamento nao encontrado"));

        entity.setStatus( StatusAgendamentos.CANCELADO );
        entity = repository.save(entity);
        return AgendamentoMapper.toResponse(entity);
    }



    private void validarIntervalo(LocalDateTime inicio, LocalDateTime fim){
        if(inicio == null || fim == null || inicio.isAfter(fim)) {
            throw new IllegalArgumentException("A data de início deve ser anterior à data de fim");
        }
    }

    private void checarConflito(String usuario, LocalDateTime inicio, LocalDateTime fim, Long id){
        if(repository.ExisteConflito(usuario, inicio, fim, id)){
            throw new IllegalArgumentException("Ja existe um agendamento nesse periodo");
        }
    }

}
