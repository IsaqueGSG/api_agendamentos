package com.api_agendamentos.api_agendamentos.repository;

import com.api_agendamentos.api_agendamentos.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    @Query("""
        SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END
        FROM Reserva r
        WHERE r.espaco.id = :espacoId
          AND r.status = 'AGENDADO'
          AND r.statusAprovacao = 'APROVADO'
          AND r.dataInicio < :fim
          AND r.dataFim > :inicio
    """)
    boolean existeConflitoEspaco(
            @Param("espacoId") Long espacoId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );

    @Query("""
        SELECT COUNT(r) 
        FROM Reserva r
        WHERE r.usuario.id = :usuarioId
          AND r.status = 'AGENDADO'
          AND r.dataInicio > CURRENT_TIMESTAMP
    """)
    long contarReservasFuturasUsuario(@Param("usuarioId") Long usuarioId);

    List<Reserva> findByStatusAprovacao(String statusAprovacao);

    // RF11
    List<Reserva> findByDataInicioBetween(LocalDateTime inicio, LocalDateTime fim);

    // Buscar reservas por espaço e período
    List<Reserva> findByEspacoIdAndDataInicioBetween(Long espacoId, LocalDateTime inicio, LocalDateTime fim);

    // RF12
    List<Reserva> findByFaltaTrue();

    // RF13
    List<Reserva> findByUsuarioId(Long usuarioId);
}