package com.api_agendamentos.api_agendamentos.services;

import com.api_agendamentos.api_agendamentos.entity.*;
import com.api_agendamentos.api_agendamentos.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final EspacoRepository espacoRepository;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public ReservaService(ReservaRepository reservaRepository,
                          EspacoRepository espacoRepository,
                          UsuarioRepository usuarioRepository,
                          JwtService jwtService) {
        this.reservaRepository = reservaRepository;
        this.espacoRepository = espacoRepository;
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
    }

    // identifica o usuário a partir do token JWT (lança 401 se o token for inválido)
    private Long usuarioIdDoToken(String token) {
        return jwtService.validar(token).usuarioId();
    }

    @Transactional
    public Reserva criarReservaAvulsa(String token, Long espacoId, LocalDateTime inicio, LocalDateTime fim) {
        Long usuarioId = usuarioIdDoToken(token);
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        Espaco espaco = espacoRepository.findById(espacoId)
                .orElseThrow(() -> new RuntimeException("Espaço não encontrado"));

        validarRegrasComuns(inicio, fim, espaco, usuario);

        // Se for AUDITORIO, exige aprovação do gestor
        String statusAprovacaoInicial = (espaco.getTipo() == TipoEspaco.AUDITORIO) ? "PENDENTE" : "APROVADO";

        Reserva reserva = Reserva.builder()
                .usuario(usuario)
                .espaco(espaco)
                .dataInicio(inicio)
                .dataFim(fim)
                .status(StatusReserva.AGENDADO)
                .recorrente(false)
                .statusAprovacao(statusAprovacaoInicial)
                .criadoEm(LocalDateTime.now())
                .build();

        return reservaRepository.save(reserva);
    }

    @Transactional
    public List<Reserva> criarReservaRecorrente(String token, Long espacoId, LocalDateTime inicio, LocalDateTime fim, LocalDateTime dataLimite) {
        Long usuarioId = usuarioIdDoToken(token);
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        Espaco espaco = espacoRepository.findById(espacoId)
                .orElseThrow(() -> new RuntimeException("Espaço não encontrado"));

        if (usuario.getPerfil() != PerfilUsuario.PROFESSOR) {
            throw new IllegalArgumentException("Apenas professores podem realizar reservas recorrentes.");
        }

        validarRegrasComuns(inicio, fim, espaco, usuario);

        if (dataLimite.isBefore(inicio) || dataLimite.isAfter(inicio.plusMonths(6))) {
            throw new IllegalArgumentException("A data limite da recorrência não pode exceder 6 meses a partir da primeira data.");
        }

        List<LocalDateTime[]> datasGeradas = new ArrayList<>();
        LocalDateTime proximaInicio = inicio;
        LocalDateTime proximaFim = fim;

        // Pré-validação de conflitos em todas as semanas antes de gravar qualquer uma
        while (!proximaInicio.isAfter(dataLimite)) {
            if (reservaRepository.existeConflitoEspaco(espacoId, proximaInicio, proximaFim)) {
                throw new IllegalArgumentException("Conflito de horário detectado na data: " + proximaInicio + ". Nenhuma reserva recorrente foi criada.");
            }
            datasGeradas.add(new LocalDateTime[]{proximaInicio, proximaFim});
            proximaInicio = proximaInicio.plusWeeks(1);
            proximaFim = proximaFim.plusWeeks(1);
        }

        // Gravação individual de cada reserva da recorrência
        List<Reserva> reservasSalvas = new ArrayList<>();
        for (LocalDateTime[] par : datasGeradas) {
            Reserva reserva = Reserva.builder()
                    .usuario(usuario)
                    .espaco(espaco)
                    .dataInicio(par[0])
                    .dataFim(par[1])
                    .status(StatusReserva.AGENDADO)
                    .recorrente(true)
                    .statusAprovacao("APROVADO")
                    .criadoEm(LocalDateTime.now())
                    .build();
            reservasSalvas.add(reservaRepository.save(reserva));
        }

        return reservasSalvas;
    }

    @Transactional
    public void cancelarReserva(Long reservaId, String token) {
        Long usuarioId = usuarioIdDoToken(token);
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));
        Usuario usuarioReq = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        boolean ehDono = reserva.getUsuario().getId().equals(usuarioId);
        boolean ehGestor = usuarioReq.getPerfil() == PerfilUsuario.GESTOR;

        if (!ehDono && !ehGestor) {
            throw new IllegalArgumentException("Você não tem permissão para cancelar esta reserva.");
        }

        if (reserva.getStatus() == StatusReserva.CANCELADO) {
            throw new IllegalArgumentException("Esta reserva já está cancelada.");
        }

        if (reserva.getStatus() == StatusReserva.CONCLUIDO || reserva.getStatus() == StatusReserva.FALTA) {
            throw new IllegalArgumentException("Não é possível cancelar uma reserva que já ocorreu.");
        }

        reserva.setStatus(StatusReserva.CANCELADO);
        reservaRepository.save(reserva);
    }

    @Transactional
    public void realizarCheckIn(Long reservaId, String token) {
        Long usuarioId = usuarioIdDoToken(token);
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada."));

        boolean ehDono = reserva.getUsuario().getId().equals(usuarioId);
        if (!ehDono) {
            throw new IllegalArgumentException("Apenas o usuário que realizou a reserva pode fazer o check-in.");
        }

        if (reserva.getStatus() != StatusReserva.AGENDADO) {
            throw new IllegalArgumentException("Não é possível realizar check-in em uma reserva que não está agendada.");
        }

        if (reserva.isCheckInRealizado()) {
            throw new IllegalArgumentException("O check-in já foi realizado para esta reserva.");
        }

        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime inicioPermitido = reserva.getDataInicio().minusMinutes(15);
        LocalDateTime fimPermitido = reserva.getDataInicio().plusMinutes(15);

        if (agora.isBefore(inicioPermitido) || agora.isAfter(fimPermitido)) {
            throw new IllegalArgumentException("O check-in só pode ser realizado entre 15 minutos antes e 15 minutos depois do horário de início.");
        }

        if (agora.isAfter(reserva.getDataFim())) {
            throw new IllegalArgumentException("O horário desta reserva já encerrou.");
        }

        reserva.setCheckInRealizado(true);
        reservaRepository.save(reserva);
    }

    private void validarRegrasComuns(LocalDateTime inicio, LocalDateTime fim, Espaco espaco, Usuario usuario) {
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
            long reservasFuturas = reservaRepository.contarReservasFuturasUsuario(usuario.getId());
            if (reservasFuturas >= 2) {
                throw new IllegalArgumentException("Aluno atingiu o limite máximo de 2 reservas futuras.");
            }
        }

        if (espaco.isBloqueado()) {
            throw new IllegalArgumentException("Este espaço está bloqueado para manutenção.");
        }

        if (reservaRepository.existeConflitoEspaco(espaco.getId(), inicio, fim)) {
            throw new IllegalArgumentException("Já existe uma reserva agendada para este espaço neste horário.");
        }
    }


    // Listar reservas pendentes de aprovação
    public List<Reserva> listarPendentes(String token) {
        Long usuarioId = usuarioIdDoToken(token);
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (usuario.getPerfil() != PerfilUsuario.GESTOR) {
            throw new IllegalArgumentException("Apenas gestores podem visualizar reservas pendentes.");
        }

        return reservaRepository.findByStatusAprovacao("PENDENTE");
    }

    @Transactional
    public void gerenciarAprovacao(Long reservaId, String token, boolean aprovar) {
        Long usuarioId = usuarioIdDoToken(token);
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (usuario.getPerfil() != PerfilUsuario.GESTOR) {
            throw new IllegalArgumentException("Apenas gestores podem aprovar ou rejeitar reservas.");
        }

        if (!"PENDENTE".equals(reserva.getStatusAprovacao())) {
            throw new IllegalArgumentException("Esta reserva não está com status pendente de aprovação.");
        }

        if (aprovar) {
            // Verifica se surgiu algum conflito no meio tempo antes de aprovar
            if (reservaRepository.existeConflitoEspaco(reserva.getEspaco().getId(), reserva.getDataInicio(), reserva.getDataFim())) {
                throw new IllegalArgumentException("Não é possível aprovar: há conflito de horário com outra reserva já aprovada.");
            }
            reserva.setStatusAprovacao("APROVADO");
        } else {
            reserva.setStatusAprovacao("REJEITADO");
            reserva.setStatus(StatusReserva.CANCELADO); // Cancela a ocorrência se rejeitado
        }

        reservaRepository.save(reserva);
    }

    // RF11
    public List<Reserva> relatorioOcupacao(String token, LocalDateTime inicio, LocalDateTime fim) {
        Long usuarioId = usuarioIdDoToken(token);
        validarGestor(usuarioId);
        return reservaRepository.findByDataInicioBetween(inicio, fim);
    }

    // RF12
    public List<Reserva> relatorioFaltas(String token) {
        Long usuarioId = usuarioIdDoToken(token);
        validarGestor(usuarioId);
        return reservaRepository.findByFaltaTrue();
    }

    // RF13
    public List<Reserva> relatorioPorUsuario(String token, Long usuarioAlvoId) {
        Long usuarioIdSolicitante = usuarioIdDoToken(token);
        validarGestor(usuarioIdSolicitante);
        return reservaRepository.findByUsuarioId(usuarioAlvoId);
    }

    // validação de gestor
    private void validarGestor(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (usuario.getPerfil() != PerfilUsuario.GESTOR) {
            throw new IllegalArgumentException("Acesso negado: apenas gestores podem gerar relatórios.");
        }
    }

}