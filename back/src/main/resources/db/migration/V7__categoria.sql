CREATE TABLE categorias (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    termo_busca VARCHAR(100) NOT NULL,
    ativa BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uk_categorias_nome UNIQUE (nome)
);

CREATE TABLE preferencia_usuario (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    categoria_id BIGINT NOT NULL,
    ativa BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uk_preferencia_usuario UNIQUE (usuario_id, categoria_id),
    CONSTRAINT fk_preferencia_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_preferencia_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);

ALTER TABLE vagas ADD COLUMN categoria_id BIGINT REFERENCES categorias(id);
CREATE INDEX idx_vaga_categoria ON vagas(categoria_id);
CREATE INDEX idx_preferencia_usuario_id ON preferencia_usuario(usuario_id);
CREATE INDEX idx_preferencia_categoria_id ON preferencia_usuario(categoria_id);

INSERT INTO categorias (nome,termo_busca) VALUES
('Desenvolvimento Web','desenvolvedor'),
('Suporte Técnico','suporte-tecnico'),
('Infraestrutura e Redes','infraestrutura'),
('Segurança da Informação','seguranca-da-informacao'),
('Dados e BI','analista-de-dados');