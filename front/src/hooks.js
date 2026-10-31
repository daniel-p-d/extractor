import { useEffect, useState } from 'react';
import { api } from './api';

export function useMinhasCategorias() {
  const [estado, setEstado] = useState({ carregando: true, categorias: [] });

  useEffect(() => {
    api('/preferencias')
      .then((prefs) => setEstado({
        carregando: false,
        categorias: prefs.filter((p) => p.ativa).map((p) => ({ id: p.categoriaId, nome: p.nomeCategoria })),
      }))
      .catch(() => setEstado({ carregando: false, categorias: [] }));
  }, []);

  return estado;
}