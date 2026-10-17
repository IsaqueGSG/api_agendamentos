package com.api_agendamentos.api_agendamentos.repository;

import com.api_agendamentos.api_agendamentos.entity.Espaco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EspacoRepository extends JpaRepository<Espaco, Long> {

    @Query("""
        SELECT e FROM Espaco e
        WHERE e.bloqueado = FALSE
          AND e.id NOT IN (
              SELECT r.espaco.id FROM Reserva r
              WHERE r.status = 'AGENDADO'
                AND r.dataInicio < :fim
                AND r.dataFim > :inicio
          )
    """)
    List<Espaco> buscarEspacosLivres(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );
}