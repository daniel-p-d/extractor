import { Routes, Route, Navigate, Link } from 'react-router-dom';
import { useAuth } from './auth';
import Login from './pages/Login';
import Cadastro from './pages/Cadastro';
import Vagas from './pages/Vagas';
import Preferencias from './pages/Preferencias';
import Analises from './pages/Analises';

function Privada({ children }) {
  const { usuario } = useAuth();
  return usuario ? children : <Navigate to="/login" replace />;
}

function Navbar() {
  const { usuario, logout } = useAuth();
  return (
    <nav>
      <strong>Vagas Extractor</strong>
      {usuario ? (
        <>
          <Link to="/vagas">Vagas</Link>
          <Link to="/analises">Análises</Link>
          <Link to="/preferencias">Minhas categorias</Link>
          <span className="spacer" />
          <span>{usuario.nome}</span>
          <button className="link" onClick={logout}>Sair</button>
        </>
      ) : (
        <>
          <span className="spacer" />
          <Link to="/login">Entrar</Link>
          <Link to="/cadastro">Cadastrar</Link>
        </>
      )}
    </nav>
  );
}

export default function App() {
  return (
    <>
      <Navbar />
      <main>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/cadastro" element={<Cadastro />} />
          <Route path="/vagas" element={<Privada><Vagas /></Privada>} />
          <Route path="/analises" element={<Privada><Analises /></Privada>} />
          <Route path="/preferencias" element={<Privada><Preferencias /></Privada>} />
          <Route path="*" element={<Navigate to="/vagas" replace />} />
        </Routes>
      </main>
    </>
  );
}