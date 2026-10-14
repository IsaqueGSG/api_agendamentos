package com.api_agendamentos.api_agendamentos.repository;

import com.api_agendamentos.api_agendamentos.entity.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    @Query("""
        SELECT CASE when count (a) > 0 then TRUE else FALSE end
            from Agendamento a
            where a.usuario = :usuario
                AND ( a.status = com.api_agendamentos.api_agendamentos.entity.StatusAgendamentos.AGENDADO )
                AND ( a.dataInicio < :fim )
                AND ( a.dataFim > :inicio )
                AND ( :id_novo is NULL or a.id <> :id_novo )
    """)
    boolean ExisteConflito(
            @Param("usuario") String usuario,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim,
            @Param("id_novo") Long id_novo
    );
}
