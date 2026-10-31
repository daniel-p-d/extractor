import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { api } from '../api';

export default function Cadastro() {
  const navigate = useNavigate();
  const [form, setForm] = useState({ nome: '', login: '', senha: '' });
  const [erro, setErro] = useState('');

  const mudar = (campo) => (e) => setForm({ ...form, [campo]: e.target.value });

  async function enviar(e) {
    e.preventDefault();
    setErro('');
    try {
      await api('/auth/registro', { method: 'POST', body: form, auth: false });
      navigate('/login');
    } catch (err) {
      setErro(err.message || 'Não foi possível cadastrar.');
    }
  }

  return (
    <form className="card narrow" onSubmit={enviar}>
      <h2>Criar conta</h2>
      <input placeholder="Nome" value={form.nome} onChange={mudar('nome')} required />
      <input type="email" placeholder="E-mail" value={form.login} onChange={mudar('login')} required />
      <input type="password" placeholder="Senha (mín. 8 caracteres)" minLength={8}
             value={form.senha} onChange={mudar('senha')} required />
      {erro && <p className="erro">{erro}</p>}
      <button type="submit">Cadastrar</button>
      <p>Já tem conta? <Link to="/login">Entrar</Link></p>
    </form>
  );
}