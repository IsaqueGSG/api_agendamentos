package com.api_agendamentos.api_agendamentos.repository;

import com.api_agendamentos.api_agendamentos.entity.Ocorrencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface OcorrenciaRepository extends JpaRepository<Ocorrencia, Long> {

    @Query("""
        SELECT CASE WHEN COUNT(o) > 0 THEN TRUE ELSE FALSE END
        FROM Ocorrencia o
        WHERE o.espaco.id = :espacoId
          AND o.status = 'AGENDADO'
          AND o.dataInicio < :fim
          And o.dataFim > :inicio
    """)
    boolean existeConflitoEspaco(
            @Param("espacoId") Long espacoId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );

    @Query("""
        SELECT COUNT(o) 
        FROM Ocorrencia o
        JOIN o.reserva r
        WHERE r.usuario.id = :usuarioId
          AND o.status = 'AGENDADO'
          AND o.dataInicio > CURRENT_TIMESTAMP
    """)
    long contarReservasFuturasUsuario(@Param("usuarioId") Long usuarioId);
}