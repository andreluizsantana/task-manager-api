CREATE SEQUENCE IF NOT EXISTS task_sequence START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS task_group_sequence START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS user_sequence START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    inativo BOOLEAN NOT NULL DEFAULT FALSE,
    criado_em TIMESTAMP,
    atualizado_em TIMESTAMP
);

CREATE TABLE IF NOT EXISTS task_group (
    id BIGINT PRIMARY KEY,
    frequency VARCHAR(50) NOT NULL,
    total_recorrencia INTEGER,
    criado_em TIMESTAMP,
    atualizado_em TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tasks (
    id BIGINT PRIMARY KEY,
    title VARCHAR(120) NOT NULL,
    descricao TEXT NOT NULL,
    status VARCHAR(255) NOT NULL,
    tipo_recorrencia VARCHAR(50),
    ocorrencia INTEGER,
    data_execucao DATE,
    task_group_id BIGINT,
    criado_em TIMESTAMP,
    atualizado_em TIMESTAMP,
    CONSTRAINT fk_tasks_task_group FOREIGN KEY (task_group_id) REFERENCES task_group (id)
);
