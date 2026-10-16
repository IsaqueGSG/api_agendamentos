CREATE TABLE tb_usuarios (
     id BIGINT AUTO_INCREMENT PRIMARY KEY,
     nome VARCHAR(120) NOT NULL,
     email VARCHAR(120) NOT NULL UNIQUE,
     senha VARCHAR(255) NOT NULL,
     perfil VARCHAR(20) NOT NULL,
     CONSTRAINT ok_perfil CHECK (perfil IN ('ALUNO', 'PROFESSOR', 'GESTOR'))
);

CREATE TABLE tb_espacos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(120) NOT NULL UNIQUE,
    tipo VARCHAR(30) NOT NULL,
    capacidade INT NOT NULL,
    recursos VARCHAR(255),
    bloqueado BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT ok_tipo CHECK (tipo IN ('SALA_AULA', 'LABORATORIO', 'AUDITORIO', 'SALA_ESTUDO'))
);

CREATE TABLE tb_reservas (
     id BIGINT AUTO_INCREMENT PRIMARY KEY,
     usuario_id BIGINT NOT NULL,
     espaco_id BIGINT NOT NULL,
     recorrente BOOLEAN NOT NULL DEFAULT FALSE,
     status_aprovacao VARCHAR(20) NOT NULL DEFAULT 'APROVADO',
     criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
     FOREIGN KEY (usuario_id) REFERENCES tb_usuarios(id),
     FOREIGN KEY (espaco_id) REFERENCES tb_espacos(id)
);

CREATE TABLE tb_ocorrencias (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        reserva_id BIGINT NOT NULL,
        espaco_id BIGINT NOT NULL,
        data_inicio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        data_fim TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        status VARCHAR(20) NOT NULL DEFAULT 'AGENDADO',
        check_in_realizado BOOLEAN NOT NULL DEFAULT FALSE,
        falta BOOLEAN NOT NULL DEFAULT FALSE,
        FOREIGN KEY (reserva_id) REFERENCES tb_reservas(id) ON DELETE CASCADE,
        FOREIGN KEY (espaco_id) REFERENCES tb_espacos(id),
        CONSTRAINT ok_status_ocorr CHECK (status IN ('AGENDADO', 'CANCELADO', 'CONCLUIDO', 'FALTA'))
);