import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../auth';
import { api } from '../api';

export default function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [erro, setErro] = useState('');

  async function enviar(e) {
    e.preventDefault();
    setErro('');

    try {
      await login(email, senha);
    } catch (err) {
      console.error('Falha no login:', err);
      setErro('E-mail ou senha inválidos.');
      return;
    }

    let destino = '/vagas';
    try {
      const prefs = await api('/preferencias');
      if (!prefs.some((p) => p.ativa)) destino = '/preferencias';
    } catch (err) {
      console.error('Falha ao carregar preferências após o login:', err);
    }
    navigate(destino);
  }

  return (
    <form className="card narrow" onSubmit={enviar}>
      <h2>Entrar</h2>
      <input type="email" placeholder="E-mail" value={email}
             onChange={(e) => setEmail(e.target.value)} required />
      <input type="password" placeholder="Senha" value={senha}
             onChange={(e) => setSenha(e.target.value)} required />
      {erro && <p className="erro">{erro}</p>}
      <button type="submit">Entrar</button>
      <p>Não tem conta? <Link to="/cadastro">Cadastre-se</Link></p>
    </form>
  );
}