import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../api';

export default function Preferencias() {
  const [catalogo, setCatalogo] = useState([]);
  const [minhas, setMinhas] = useState([]);
  const [termo, setTermo] = useState('');
  const [erro, setErro] = useState('');
  const [aviso, setAviso] = useState('');

  async function carregar() {
    setCatalogo(await api('/categorias', { auth: false }));
    setMinhas(await api('/preferencias'));
  }

  useEffect(() => { carregar().catch((e) => setErro(e.message)); }, []);

  const ativas = minhas.filter((p) => p.ativa);
  const idsAtivos = new Set(ativas.map((p) => p.categoriaId));
  const sugestoes = catalogo.filter((c) => !idsAtivos.has(c.id));

  const LIMITE = 2;
  const cheio = ativas.length >= LIMITE;

  async function executar(acao, mensagem = '') {
    setErro('');
    setAviso('');
    try {
      await acao();
      await carregar();
      setAviso(mensagem);
    } catch (e) {
      setErro(e.message);
    }
  }

  const adicionarCatalogo = (categoriaId) =>
    executar(() => api('/preferencias', { method: 'POST', body: { categoriaId } }));

  const remover = (id) =>
    executar(() => api(`/preferencias/${id}`, { method: 'DELETE' }));

  async function adicionarPersonalizada(e) {
    e.preventDefault();
    await executar(
      () => api('/preferencias/personalizada', { method: 'POST', body: { termo } }),
      'Pronto! As vagas desse termo aparecem após a próxima coleta automática (de madrugada).'
    );
    setTermo('');
  }

  return (
    <>
      <h2>Minhas categorias</h2>
      <p className="muted">
        Você recebe e-mail quando surgem vagas novas nas categorias que acompanha.
      </p>
      {erro && <p className="erro">{erro}</p>}
      {aviso && <p className="muted">{aviso}</p>}

      <section className="card">
        <h3>Acompanhando</h3>
        {ativas.length === 0 && (
          <p className="erro">Escolha ao menos uma categoria para ver vagas e receber e-mails.</p>
        )}
        {ativas.map((p) => (
          <div key={p.id} className="linha">
            <span>{p.nomeCategoria}</span>
            <button className="secundario" onClick={() => remover(p.id)}>Remover</button>
          </div>
        ))}
        {ativas.length > 0 && <p><Link to="/vagas">Ver vagas das minhas categorias →</Link></p>}
      </section>

      <form className="card" onSubmit={adicionarPersonalizada}>
        <h3>Acompanhar outro cargo</h3>
        <p className="muted">
          Digite o cargo ou área que você procura (ex.: analista de dados). Você pode acompanhar até {LIMITE} categorias ao mesmo tempo.
        </p>
        {cheio && <p className="erro">Você já está no limite. Remova uma categoria para adicionar outra.</p>}
        <div className="linha">
          <input
            value={termo}
            onChange={(e) => setTermo(e.target.value)}
            placeholder="Ex.: analista de dados"
            minLength={3}
            maxLength={60}
            required
            disabled={cheio}
          />
          <button onClick={() => adicionarCatalogo(c.id)} disabled={cheio}>Acompanhar</button>
        </div>
      </form>

      {sugestoes.length > 0 && (
        <section className="card">
          <h3>Sugestões</h3>
          {sugestoes.map((c) => (
            <div key={c.id} className="linha">
              <span>{c.nome}</span>
              <button onClick={() => adicionarCatalogo(c.id)}>Acompanhar</button>
            </div>
          ))}
        </section>
      )}
    </>
  );
}