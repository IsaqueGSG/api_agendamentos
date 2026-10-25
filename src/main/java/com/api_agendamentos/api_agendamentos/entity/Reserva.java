package com.api_agendamentos.api_agendamentos.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_reservas")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "espaco_id", nullable = false)
    private Espaco espaco;

    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDateTime dataFim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusReserva status;

    @Builder.Default
    @Column(name = "check_in_realizado", nullable = false)
    private boolean checkInRealizado = false;

    @Builder.Default
    @Column(nullable = false)
    private boolean falta = false;

    @Builder.Default
    @Column(nullable = false)
    private boolean recorrente = false;

    @Builder.Default
    @Column(name = "status_aprovacao", nullable = false, length = 20)
    private String statusAprovacao = "APROVADO";

    @Builder.Default
    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();
}