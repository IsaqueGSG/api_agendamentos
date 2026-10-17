package com.api_agendamentos.api_agendamentos.services;

import com.api_agendamentos.api_agendamentos.entity.*;
import com.api_agendamentos.api_agendamentos.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservaService {

    private final OcorrenciaRepository ocorrenciaRepository;
    private final ReservaRepository reservaRepository;
    private final EspacoRepository espacoRepository;
    private final UsuarioRepository usuarioRepository;

    public ReservaService(OcorrenciaRepository ocorrenciaRepository,
                          ReservaRepository reservaRepository,
                          EspacoRepository espacoRepository,
                          UsuarioRepository usuarioRepository) {
        this.ocorrenciaRepository = ocorrenciaRepository;
        this.reservaRepository = reservaRepository;
        this.espacoRepository = espacoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Ocorrencia criarReservaAvulsa(Long usuarioId, Long espacoId, LocalDateTime inicio, LocalDateTime fim) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        Espaco espaco = espacoRepository.findById(espacoId)
                .orElseThrow(() -> new RuntimeException("Espaço não encontrado"));

        if (inicio.getHour() < 7 || fim.getHour() > 23 || inicio.getMinute() != 0 || fim.getMinute() != 0) {
            throw new IllegalArgumentException("As reservas devem ser em horas cheias, entre 7h e 23h.");
        }

        long horas = Duration.between(inicio, fim).toHours();
        if (horas <= 0 || horas > 4) {
            throw new IllegalArgumentException("A duração máxima permitida é de 4 horas.");
        }
        if (usuario.getPerfil() == PerfilUsuario.ALUNO) {
            if (espaco.getTipo() != TipoEspaco.SALA_ESTUDO) {
                throw new IllegalArgumentException("Alunos só podem reservar salas de estudo.");
            }
            if (horas > 2) {
                throw new IllegalArgumentException("Alunos podem reservar no máximo 2 horas por dia.");
            }
            long reservasFuturas = ocorrenciaRepository.contarReservasFuturasUsuario(usuarioId);
            if (reservasFuturas >= 2) {
                throw new IllegalArgumentException("Aluno atingiu o limite máximo de 2 reservas futuras.");
            }
        }

        if (espaco.isBloqueado()) {
            throw new IllegalArgumentException("Este espaço está bloqueado para manutenção.");
        }

        if (ocorrenciaRepository.existeConflitoEspaco(espacoId, inicio, fim)) {
            throw new IllegalArgumentException("Já existe uma ocorrência agendada para este espaço neste horário.");
        }


        Reserva reserva = Reserva.builder()
                .usuario(usuario)
                .espaco(espaco)
                .recorrente(false)
                .statusAprovacao("APROVADO")
                .criadoEm(LocalDateTime.now())
                .build();
        reserva = reservaRepository.save(reserva);

        Ocorrencia ocorrencia = Ocorrencia.builder()
                .reserva(reserva)
                .espaco(espaco)
                .dataInicio(inicio)
                .dataFim(fim)
                .status(StatusOcorrencia.AGENDADO)
                .build();

        return ocorrenciaRepository.save(ocorrencia);
    }

    @Transactional
    public List<Ocorrencia> criarReservaRecorrente(Long usuarioId, Long espacoId, LocalDateTime inicio, LocalDateTime fim, LocalDateTime dataLimite) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        Espaco espaco = espacoRepository.findById(espacoId)
                .orElseThrow(() -> new RuntimeException("Espaço não encontrado"));


        if (usuario.getPerfil() != PerfilUsuario.PROFESSOR) {
            throw new IllegalArgumentException("Apenas professores podem realizar reservas recorrentes.");
        }


        if (inicio.getHour() < 7 || fim.getHour() > 23 || inicio.getMinute() != 0 || fim.getMinute() != 0) {
            throw new IllegalArgumentException("As reservas devem ser em horas cheias, entre 7h e 23h.");
        }

        long horas = Duration.between(inicio, fim).toHours();
        if (horas <= 0 || horas > 4) {
            throw new IllegalArgumentException("A duração máxima permitida é de 4 horas.");
        }

        if (dataLimite.isBefore(inicio) || dataLimite.isAfter(inicio.plusMonths(6))) {
            throw new IllegalArgumentException("A data limite da recorrência não pode exceder 6 meses a partir da primeira data.");
        }

        if (espaco.isBloqueado()) {
            throw new IllegalArgumentException("Este espaço está bloqueado para manutenção.");
        }


        List<LocalDateTime[]> datasGeradas = new java.util.ArrayList<>();
        LocalDateTime proximaInicio = inicio;
        LocalDateTime proximaFim = fim;

        while (!proximaInicio.isAfter(dataLimite)) {

            if (ocorrenciaRepository.existeConflitoEspaco(espacoId, proximaInicio, proximaFim)) {
                throw new IllegalArgumentException("Conflito de horário detectado na data: " + proximaInicio + ". Nenhuma reserva recorrente foi criada.");
            }
            datasGeradas.add(new LocalDateTime[]{proximaInicio, proximaFim});

            // Avança exatamente 1 semana (7 dias)
            proximaInicio = proximaInicio.plusWeeks(1);
            proximaFim = proximaFim.plusWeeks(1);
        }


        Reserva reserva = Reserva.builder()
                .usuario(usuario)
                .espaco(espaco)
                .recorrente(true)
                .statusAprovacao("APROVADO")
                .criadoEm(LocalDateTime.now())
                .build();
        reserva = reservaRepository.save(reserva);

        List<Ocorrencia> ocorrenciasSalvas = new java.util.ArrayList<>();
        for (LocalDateTime[] par : datasGeradas) {
            Ocorrencia ocorrencia = Ocorrencia.builder()
                    .reserva(reserva)
                    .espaco(espaco)
                    .dataInicio(par[0])
                    .dataFim(par[1])
                    .status(StatusOcorrencia.AGENDADO)
                    .build();
            ocorrenciasSalvas.add(ocorrenciaRepository.save(ocorrencia));
        }

        return ocorrenciasSalvas;
    }

    @Transactional
    public void cancelarOcorrencia(Long ocorrenciaId, Long usuarioId) {
        Ocorrencia ocorrencia = ocorrenciaRepository.findById(ocorrenciaId)
                .orElseThrow(() -> new RuntimeException("Ocorrência não encontrada"));

        Usuario usuarioReq = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Validação
        boolean ehDono = ocorrencia.getReserva().getUsuario().getId().equals(usuarioId);
        boolean ehGestor = usuarioReq.getPerfil() == PerfilUsuario.GESTOR;

        if (!ehDono && !ehGestor) {
            throw new IllegalArgumentException("Você não tem permissão para cancelar esta reserva.");
        }

        if (ocorrencia.getStatus() == StatusOcorrencia.CANCELADO) {
            throw new IllegalArgumentException("Esta reserva já está cancelada.");
        }

        if (ocorrencia.getStatus() == StatusOcorrencia.CONCLUIDO || ocorrencia.getStatus() == StatusOcorrencia.FALTA) {
            throw new IllegalArgumentException("Não é possível cancelar uma reserva que já ocorreu.");
        }

        // Altera o status para CANCELADO
        ocorrencia.setStatus(StatusOcorrencia.CANCELADO);
        ocorrenciaRepository.save(ocorrencia);
    }


    @Transactional
    public void realizarCheckIn(Long ocorrenciaId, Long usuarioId) {
        Ocorrencia ocorrencia = ocorrenciaRepository.findById(ocorrenciaId)
                .orElseThrow(() -> new RuntimeException("Ocorrência não encontrada."));

        // Valida se o usuário é o dono da reserva
        boolean ehDono = ocorrencia.getReserva().getUsuario().getId().equals(usuarioId);
        if (!ehDono) {
            throw new IllegalArgumentException("Apenas o usuário que realizou a reserva pode fazer o check-in.");
        }

        if (ocorrencia.getStatus() != StatusOcorrencia.AGENDADO) {
            throw new IllegalArgumentException("Não é possível realizar check-in em uma ocorrência que não está agendada.");
        }

        if (ocorrencia.isCheckInRealizado()) {
            throw new IllegalArgumentException("O check-in já foi realizado para esta ocorrência.");
        }

        // nao pode chek in depois do horario
        LocalDateTime agora = LocalDateTime.now();
        if (agora.isAfter(ocorrencia.getDataFim())) {
            throw new IllegalArgumentException("O horário desta ocorrência já encerrou.");
        }

        ocorrencia.setCheckInRealizado(true);
        ocorrenciaRepository.save(ocorrencia);
    }
}