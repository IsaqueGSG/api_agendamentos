package com.api_agendamentos.api_agendamentos.repository;

import com.api_agendamentos.api_agendamentos.entity.Espaco;
import com.api_agendamentos.api_agendamentos.entity.TipoEspaco;
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
              SELECT o.espaco.id FROM Ocorrencia o
              WHERE o.status = 'AGENDADO'
                AND o.dataInicio < :fim
                AND o.dataFim > :inicio
          )
    """)
    List<Espaco> buscarEspacosLivres(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );
}