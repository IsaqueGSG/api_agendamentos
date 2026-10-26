package com.api_agendamentos.api_agendamentos.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_espacos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Espaco {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoEspaco tipo;

    @Column(nullable = false)
    private Integer capacidade;

    private String recursos; // ex: "projetor, computadores"

    @Builder.Default
    @Column(nullable = false)
    private boolean bloqueado = false;
}