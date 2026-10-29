-- Bancos origem e destino replicação

CREATE TABLE tb_funcionarios (
     id BIGSERIAL PRIMARY KEY,
     nome VARCHAR(150) NOT NULL,
     cpf VARCHAR(14) NOT NULL UNIQUE,
     ativo BOOLEAN NOT NULL DEFAULT TRUE,
     created_at TIMESTAMP NOT NULL DEFAULT NOW(),
     updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE tb_clientes (
     id BIGSERIAL PRIMARY KEY,
     nome VARCHAR(150) NOT NULL,
     documento VARCHAR(20) NOT NULL,
     ativo BOOLEAN NOT NULL DEFAULT TRUE,
     created_at TIMESTAMP NOT NULL DEFAULT NOW(),
     updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE tb_bombas (
    id BIGSERIAL PRIMARY KEY,
    identificador VARCHAR(50) NOT NULL,
    tipo_combustivel VARCHAR(50) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE tb_abastecimentos (
   id BIGSERIAL PRIMARY KEY,
   bomba_id BIGINT NOT NULL,
   cliente_id BIGINT NULL,
   funcionario_id BIGINT NOT NULL,
   litros NUMERIC(10, 3) NOT NULL,
   valor_total NUMERIC(10, 2) NOT NULL,
   data_hora TIMESTAMP NOT NULL,
   created_at TIMESTAMP NOT NULL DEFAULT NOW(),
   updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

   CONSTRAINT fk_abastecimento_bomba
       FOREIGN KEY (bomba_id)
           REFERENCES tb_bombas(id),

   CONSTRAINT fk_abastecimento_cliente
       FOREIGN KEY (cliente_id)
           REFERENCES tb_clientes(id),

   CONSTRAINT fk_abastecimento_funcionario
       FOREIGN KEY (funcionario_id)
           REFERENCES tb_funcionarios(id)
);

-- Banco de controle da replicação

CREATE TABLE tb_replicacao_processo (
    id BIGSERIAL PRIMARY KEY,
    processo VARCHAR(100) NOT NULL,
    descricao VARCHAR(300) NULL,
    habilitado BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE tb_replicacao_processo_tabela (
   id BIGSERIAL PRIMARY KEY,
   processo_id BIGINT NOT NULL,
   tabela_origem VARCHAR(150) NOT NULL,
   tabela_destino VARCHAR(150) NOT NULL,
   ordem INTEGER NOT NULL,
   habilitado BOOLEAN NOT NULL DEFAULT TRUE,
   ds_where VARCHAR(500) NULL
);

CREATE TABLE tb_replicacao_direcao (
   id BIGSERIAL PRIMARY KEY,
   conexao_origem VARCHAR(150) NOT NULL,
   conexao_destino VARCHAR(150) NOT NULL,
   usuario_origem VARCHAR(45) NOT NULL,
   usuario_destino VARCHAR(45) NOT NULL,
   senha_origem VARCHAR(45) NOT NULL,
   senha_destino VARCHAR(45) NOT NULL,
   habilitado BOOLEAN NOT NULL DEFAULT TRUE,
   processo_id BIGINT NOT NULL
);

ALTER TABLE tb_replicacao_processo_tabela
    ADD CONSTRAINT fk_replicacao_processo_tabela
        FOREIGN KEY (processo_id) REFERENCES tb_replicacao_processo(id);

ALTER TABLE tb_replicacao_direcao
    ADD CONSTRAINT fk_replicacao_processo_direcao
        FOREIGN KEY (processo_id) REFERENCES tb_replicacao_processo(id);