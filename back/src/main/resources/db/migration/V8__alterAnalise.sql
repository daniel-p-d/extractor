ALTER TABLE analise_tendencia DROP COLUMN localizacao;

ALTER TABLE analise_tendencia ADD COLUMN categoria_id BIGINT NOT NULL REFERENCES categorias(id);
CREATE INDEX idx_analise_categoria ON analise_tendencia(categoria_id);

ALTER TABLE analise_tendencia ADD CONSTRAINT uk_analise_tendencia UNIQUE (habilidade_id, categoria_id, periodo_referencia);