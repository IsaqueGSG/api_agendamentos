package mapper;

import java.time.LocalDateTime;

import com.api_agendamentos.api_agendamentos.dto.AgendamentoCreateRequest;
import com.api_agendamentos.api_agendamentos.entity.Agendamento;
import com.api_agendamentos.api_agendamentos.entity.StatusAgendamentos;

public class AgendamentoMapper {
	
	public static Agendamento toEntity(AgendamentoCreateRequest req) {
		return Agendamento.builder()
				.titulo(req.titulo())
				.descricao(req.descricao())
				.dataInicio(req.dataInicio())
				.dataFim(req.dataFim())
				.usuario(req.usuario())
				.status(StatusAgendamentos.AGENDADO)
				.criadoEm(LocalDateTime.now())
				.atualizadoEm(LocalDateTime.now())
				.build();
	}

}
