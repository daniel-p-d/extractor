import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../api';
import { useMinhasCategorias } from '../hooks';

const diasAtras = (n) => new Date(Date.now() - n * 86400000).toISOString().slice(0, 10);

export default function Vagas() {
  const { carregando, categorias } = useMinhasCategorias();
  const [categoriaId, setCategoriaId] = useState('');
  const [filtroData, setFiltroData] = useState('todas');
  const [ultima, setUltima] = useState(null);
  const [pagina, setPagina] = useState(0);
  const [dados, setDados] = useState(null);
  const [erro, setErro] = useState('');

  useEffect(() => {
    api('/vagas/ultima-extracao').then((d) => setUltima(d.data)).catch(() => {});
  }, []);

  useEffect(() => {
    let desde = null;
    if (filtroData === 'ultima') desde = ultima;
    if (filtroData === '7') desde = diasAtras(7);
    if (filtroData === '30') desde = diasAtras(30);
    if (filtroData === 'ultima' && !ultima) { setDados(null); return; }

    const params = new URLSearchParams({ pagina, tamanho: 10 });
    if (categoriaId) params.set('categoriaId', categoriaId);
    if (desde) params.set('desde', desde);

    api(`/vagas?${params}`)
      .then((d) => { setDados(d); setErro(''); })
      .catch((e) => setErro(e.message));
  }, [categoriaId, filtroData, ultima, pagina]);

  if (!carregando && categorias.length === 0) {
    return (
      <div className="card">
        <h3>Escolha o que você quer acompanhar</h3>
        <p className="muted">Você ainda não acompanha nenhuma categoria.</p>
        <Link to="/preferencias">Escolher categorias →</Link>
      </div>
    );
  }

  return (
    <>
      <div className="barra">
        <h2>Vagas</h2>
        <div className="filtros">
          <select value={categoriaId} onChange={(e) => { setCategoriaId(e.target.value); setPagina(0); }}>
            <option value="">Minhas categorias</option>
            {categorias.map((c) => <option key={c.id} value={c.id}>{c.nome}</option>)}
          </select>
          <select value={filtroData} onChange={(e) => { setFiltroData(e.target.value); setPagina(0); }}>
            <option value="todas">Qualquer extração</option>
            <option value="ultima">Última extração{ultima ? ` (${ultima})` : ''}</option>
            <option value="7">Últimos 7 dias</option>
            <option value="30">Últimos 30 dias</option>
          </select>
        </div>
      </div>

      {erro && <p className="erro">{erro}</p>}
      {dados && <p className="muted">{dados.totalElementos} vagas encontradas</p>}
      {filtroData === 'ultima' && !ultima && <p className="muted">Ainda não há extrações para suas categorias.</p>}

      {dados?.conteudo.map((v) => (
        <article key={v.id} className="card">
          <h3><a href={v.url} target="_blank" rel="noreferrer">{v.titulo}</a></h3>
          <p className="muted">{[v.empresa, v.localizacao, v.portal].filter(Boolean).join(' · ')}</p>
          <p className="muted">
            {[v.categoria, v.senioridade, v.modalidade, v.dataPublicacao].filter(Boolean).join(' · ')}
          </p>
          <div className="tags">
            {v.habilidades.map((h) => <span key={h} className="tag">{h}</span>)}
          </div>
        </article>
      ))}

      {dados && dados.totalPaginas > 1 && (
        <div className="paginacao">
          <button disabled={pagina === 0} onClick={() => setPagina(pagina - 1)}>Anterior</button>
          <span>{pagina + 1} / {dados.totalPaginas}</span>
          <button disabled={pagina + 1 >= dados.totalPaginas} onClick={() => setPagina(pagina + 1)}>Próxima</button>
        </div>
      )}
    </>
  );
}