package com.api_agendamentos.api_agendamentos.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name= "tb_agendamentos")
public class Agendamento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, length = 120)
	private String titulo;
	
	@Column(columnDefinition = "TEXT")
	private String descricao;
	
	@Column(name ="data_inicio", nullable = false)
	private LocalDateTime dataInicio;
	
	@Column(name ="data_fim", nullable = false)
	private LocalDateTime dataFim;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private StatusAgendamentos status;
	
	@Column(nullable = false, length = 80)
	private String usuario;
	
	@Column(name = "criado_em", nullable = false)
	private LocalDateTime criadoEm;
	
	@Column(name = "atualizado_em", nullable = false)
	private LocalDateTime atualizadoEm;
	
}
