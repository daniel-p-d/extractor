-- criação das tabelas

CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    login VARCHAR(100) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    perfil VARCHAR(255) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP NOT NULL DEFAULT now(),
    ultimo_login TIMESTAMP,
    CONSTRAINT uk_usuarios_login UNIQUE (login)
);

CREATE TABLE portais (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    url_base VARCHAR(500) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    seletor_config TEXT,
    criado_em TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT uk_portais_nome UNIQUE (nome)
);

CREATE TABLE empresas (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    setor VARCHAR(50),
    CONSTRAINT uk_empresas_nome UNIQUE (nome)
);

CREATE TABLE habilidades (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    categoria VARCHAR(30) NOT NULL,
    CONSTRAINT uk_habilidades_nome UNIQUE (nome)
);

CREATE TABLE vagas (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    url VARCHAR(1000) NOT NULL,
    descricao_bruta TEXT,
    portal_id BIGINT NOT NULL,
    empresa_id BIGINT,
    localizacao VARCHAR(255),
    modalidade VARCHAR(255),
    regime VARCHAR(255),
    senioridade VARCHAR(30),
    salario_min NUMERIC(12,2),
    salario_max NUMERIC(12,2),
    data_publicacao DATE,
    extraido_em TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVA',
    CONSTRAINT uk_vagas_url UNIQUE (url),
    CONSTRAINT fk_vagas_portal FOREIGN KEY (portal_id) REFERENCES portais(id),
    CONSTRAINT fk_vagas_empresa FOREIGN KEY (empresa_id) REFERENCES empresas(id)
);

CREATE TABLE vaga_habilidade (
    id BIGSERIAL PRIMARY KEY,
    vaga_id BIGINT NOT NULL,
    habilidade_id BIGINT NOT NULL,
    obrigatoria BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uk_vaga_habilidade UNIQUE (vaga_id, habilidade_id),
    CONSTRAINT fk_vaga_habilidade_vaga FOREIGN KEY (vaga_id) REFERENCES vagas(id) ON DELETE CASCADE,
    CONSTRAINT fk_vaga_habilidade_habilidade FOREIGN KEY (habilidade_id) REFERENCES habilidades(id)
);

CREATE TABLE historico_coleta (
    id BIGSERIAL PRIMARY KEY,
    portal_id BIGINT NOT NULL,
    iniciado_em TIMESTAMP NOT NULL,
    finalizado_em TIMESTAMP,
    vagas_encontradas INTEGER,
    vagas_novas INTEGER,
    vagas_com_erro INTEGER,
    sucesso BOOLEAN NOT NULL,
    mensagem_erro TEXT,
    CONSTRAINT fk_historico_coleta_portal FOREIGN KEY (portal_id) REFERENCES portais(id)
);

CREATE TABLE analise_tendencia (
    id BIGSERIAL PRIMARY KEY,
    habilidade_id BIGINT NOT NULL,
    periodo_referencia DATE NOT NULL,
    quantidade_mencoes INTEGER NOT NULL,
    percentual_vaga NUMERIC(5,2),
    variacao_percentual NUMERIC(6,2),
    localizacao VARCHAR(255),
    CONSTRAINT fk_analise_tendencia_habilidade FOREIGN KEY (habilidade_id) REFERENCES habilidades(id)
);

CREATE INDEX idx_vaga_status ON vagas(status);
CREATE INDEX idx_vaga_data_publicacao ON vagas(data_publicacao);
CREATE INDEX idx_vaga_portal_id ON vagas(portal_id);
CREATE INDEX idx_vaga_empresa_id ON vagas(empresa_id);
CREATE INDEX idx_vaga_senioridade ON vagas(senioridade);
CREATE INDEX idx_vaga_habilidade_vaga_id ON vaga_habilidade(vaga_id);
CREATE INDEX idx_vaga_habilidade_habilidade_id ON vaga_habilidade(habilidade_id);
CREATE INDEX idx_historico_coleta_portal_id ON historico_coleta(portal_id);
CREATE INDEX idx_historico_coleta_iniciado_em ON historico_coleta(iniciado_em);
CREATE INDEX idx_analise_periodo ON analise_tendencia(periodo_referencia);
CREATE INDEX idx_analise_habilidade_id ON analise_tendencia(habilidade_id);
CREATE INDEX idx_habilidade_categoria ON habilidades(categoria);