ALTER TABLE categorias ADD COLUMN padrao BOOLEAN NOT NULL DEFAULT FALSE;
UPDATE categorias SET padrao = TRUE;
CREATE UNIQUE INDEX uk_categorias_termo_busca ON categorias(termo_busca);