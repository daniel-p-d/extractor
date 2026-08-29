CREATE TABLE alerta_usuario (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    habilidade_id BIGINT NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP NOT NULL DEFAULT now(),
    ultima_notificacao_em TIMESTAMP,
    CONSTRAINT uk_alerta_usuario_habilidade UNIQUE (usuario_id, habilidade_id),
    CONSTRAINT fk_alerta_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_alerta_habilidade FOREIGN KEY (habilidade_id) REFERENCES habilidades(id)
);

CREATE INDEX idx_alerta_usuario_id ON alerta_usuario(usuario_id);
CREATE INDEX idx_alerta_habilidade_id ON alerta_usuario(habilidade_id);
CREATE INDEX idx_alerta_ativo ON alerta_usuario(ativo);