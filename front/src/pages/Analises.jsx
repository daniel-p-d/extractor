import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../api';
import { useMinhasCategorias } from '../hooks';

function Barras({ itens }) {
  if (!itens?.length) return <p className="muted">Sem dados neste período.</p>;
  return itens.map((i) => (
    <div key={i.rotulo} className="linha-analise">
      <span className="nome" title={i.rotulo}>{i.rotulo}</span>
      <div className="barra-fundo"><div className="barra-preench" style={{ width: `${i.percentual}%` }} /></div>
      <span className="valor">{i.percentual}% ({i.quantidade})</span>
    </div>
  ));
}

function Variacao({ valor, sufixo }) {
  if (valor == null) return <span className="muted">—</span>;
  return <span className={valor >= 0 ? 'sobe' : 'desce'}>{valor > 0 ? '+' : ''}{valor}{sufixo}</span>;
}

export default function Analises() {
  const { carregando, categorias } = useMinhasCategorias();
  const [categoriaId, setCategoriaId] = useState('');
  const [dias, setDias] = useState(30);
  const [r, setR] = useState(null);
  const [erro, setErro] = useState('');

  useEffect(() => {
    if (!categoriaId && categorias.length) setCategoriaId(String(categorias[0].id));
  }, [categorias, categoriaId]);

  useEffect(() => {
    if (!categoriaId) return;
    api(`/analises/resumo?categoriaId=${categoriaId}&dias=${dias}`)
      .then((d) => { setR(d); setErro(''); })
      .catch((e) => setErro(e.message));
  }, [categoriaId, dias]);

  if (!carregando && categorias.length === 0) {
    return (
      <div className="card">
        <h3>Escolha uma categoria para ver as análises</h3>
        <Link to="/preferencias">Escolher categorias →</Link>
      </div>
    );
  }

  const maxSerie = r ? Math.max(1, ...r.volume.serieDiaria.map((p) => p.quantidade)) : 1;

  return (
    <>
      <div className="barra">
        <h2>Análises</h2>
        <div className="filtros">
          <select value={categoriaId} onChange={(e) => setCategoriaId(e.target.value)}>
            {categorias.map((c) => <option key={c.id} value={c.id}>{c.nome}</option>)}
          </select>
          <select value={dias} onChange={(e) => setDias(Number(e.target.value))}>
            <option value={7}>Últimos 7 dias</option>
            <option value={30}>Últimos 30 dias</option>
            <option value={90}>Últimos 90 dias</option>
          </select>
        </div>
      </div>

      {erro && <p className="erro">{erro}</p>}

      {r && (
        <>
          <section className="card">
            <h3>Vagas coletadas</h3>
            <p>
              <strong>{r.volume.totalAtual}</strong> neste período
              {' '}· {r.volume.totalAnterior} no anterior
              {' '}· variação <Variacao valor={r.volume.variacaoPercentual} sufixo="%" />
            </p>
            <div className="serie">
              {r.volume.serieDiaria.map((p) => (
                <div key={p.data} className="serie-barra" title={`${p.data}: ${p.quantidade}`}
                     style={{ height: `${(p.quantidade / maxSerie) * 100}%` }} />
              ))}
            </div>
          </section>

          <section className="card">
            <h3>Tecnologias em destaque</h3>
            <p className="muted">% das vagas que citam a tecnologia · variação em pontos percentuais vs. período anterior</p>
            {r.tecnologias.length === 0 && <p className="muted">Sem dados neste período.</p>}
            {r.tecnologias.map((t) => (
              <div key={t.habilidade} className="linha-analise">
                <span className="nome">{t.habilidade}</span>
                <div className="barra-fundo"><div className="barra-preench" style={{ width: `${t.percentual}%` }} /></div>
                <span className="valor">{t.percentual}% ({t.quantidade})</span>
                <Variacao valor={t.variacaoPontos} sufixo=" pp" />
              </div>
            ))}
          </section>

          <div className="grade">
            <section className="card"><h3>Vagas por portal</h3><Barras itens={r.porPortal} /></section>
            <section className="card"><h3>Vagas por senioridade</h3><Barras itens={r.porSenioridade} /></section>
            <section className="card"><h3>Vagas por localização</h3><Barras itens={r.porLocalizacao} /></section>
            <section className="card"><h3>Empresas com mais vagas</h3><Barras itens={r.empresas} /></section>
          </div>

          <h3>Habilidades por senioridade</h3>
          <div className="grade">
            {r.habilidadesPorSenioridade.map((g) => (
              <section key={g.senioridade} className="card">
                <h3>{g.senioridade} <span className="muted">({g.totalVagas} vagas)</span></h3>
                <Barras itens={g.habilidades} />
              </section>
            ))}
          </div>
        </>
      )}
    </>
  );
}