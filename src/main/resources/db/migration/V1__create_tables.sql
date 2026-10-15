CREATE TABLE tb_agendamentos(
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(120) NOT NULL,
    descricao TEXT,
    data_inicio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_fim TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'AGENDADO',
    usuario VARCHAR(80) NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT ok_status CHECK (status IN ('AGENDADO', 'CANCELADO', 'CONCLUIDO')),
    CONSTRAINT ok_intervalo CHECK (data_inicio < data_fim)
);

-- Criando índice
CREATE INDEX idx_ag_usuario_inicio_fim
    ON tb_agendamentos (usuario, data_inicio, data_fim);


-- para postgrees

-- CREATE TABLE tb_agendamentos(
-- 	id serial PRIMARY KEY,
-- 	titulo VARCHAR(120) NOT NULL,
-- 	descricao TEXT,
-- 	data_inicio TIMESTAMP NOT NULL,
-- 	data_fim TIMESTAMP NOT NULL,
-- 	status VARCHAR(20) NOT NULL DEFAULT 'AGENDADO',
-- 	usuario VARCHAR(80) NOT NULL,
-- 	criado_em TIMESTAMP NOT NULL DEFAULT NOW(),
-- 	atualizado_em TIMESTAMP NOT NULL DEFAULT NOW(),
-- 	CONSTRAINT ok_status CHECK (status in ('AGENDADO', 'CANCELADO', 'CONCLUIDO')),
-- 	CONSTRAINT ok_intervalo CHECK (data_inicio < data_fim)
-- );
--
-- -- criando index
-- CREATE INDEX idx_ag_usuario_inicio_fim
-- 	ON tb_agendamentos (usuario, data_inicio, data_fim);
--
--
-- -- atualiza o campo "atualizado_em" automaticamente
-- CREATE OR REPLACE FUNCTION set_atualizado_em()
-- RETURNS TRIGGER AS $$
-- BEGIN
-- 	NEW.atualizado_em := NOW();
-- 	RETURN NEW;
-- END;
-- $$ LANGUAGE plpgsql;
--
-- CREATE TRIGGER trg_set_atualizado_em
-- BEFORE UPDATE ON tb_agendamentos
-- FOR EACH ROW
-- EXECUTE FUNCTION set_atualizado_em();