import { useMsal, useAccount } from '@azure/msal-react';
import { LogOut, User } from 'lucide-react';
import Sidebar from '../components/Sidebar';
import { useState, useEffect } from 'react';
import axiosClient from '../api/axiosClient';

export default function Dashboard() {
  const { instance, accounts } = useMsal();
  const account = useAccount(accounts[0] || {});
  const [backendStatus, setBackendStatus] = useState('Verificando backend...');

  const handleLogout = () => {
    instance.logoutRedirect({
      postLogoutRedirectUri: "http://localhost:5173",
    });
  };

  useEffect(() => {
    // Ejemplo de llamada al backend para verificar que el interceptor funciona
    axiosClient.get('/public/health')
      .then(res => setBackendStatus('Backend conectado: ' + res.data))
      .catch(err => setBackendStatus('Error conectando al backend: ' + err.message));
  }, []);

  return (
    <div className="app-container">
      <Sidebar />
      
      <main className="main-content">
        <header className="top-nav">
          <a href="#">Inicio</a>
          <a href="#">Mis Recetas</a>
          <a href="#">Explorar</a>
          
          <div style={{ display: 'flex', alignItems: 'center', gap: '16px', marginLeft: 'auto' }}>
            <span style={{ fontSize: '0.9rem', color: 'var(--text-muted)' }}>
              Hola, {account?.name || 'Usuario'}
            </span>
            <div style={{
              width: '40px', height: '40px', borderRadius: '50%', 
              backgroundColor: '#E0E0E0', display: 'flex', 
              alignItems: 'center', justifyContent: 'center'
            }}>
              <User size={20} color="#666" />
            </div>
            <button className="btn btn-outline" onClick={handleLogout} title="Cerrar sesión" style={{ padding: '8px', borderRadius: '50%' }}>
              <LogOut size={18} />
            </button>
          </div>
        </header>

        <div>
          <h1 style={{ marginBottom: '8px' }}>Bienvenido a Qué hay pa' hacer</h1>
          <p style={{ color: 'var(--text-muted)', marginBottom: '32px' }}>
            Estado de conexión: {backendStatus}
          </p>

          <div style={{ display: 'flex', gap: '24px', flexWrap: 'wrap' }}>
            {/* Aquí irían las tarjetas de recetas en la Fase 4 */}
            <div style={{ 
              width: '100%', maxWidth: '300px', height: '200px', 
              backgroundColor: '#F3F4F6', borderRadius: 'var(--border-radius-md)',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              border: '2px dashed #D1D5DB'
            }}>
              <p style={{ color: '#9CA3AF' }}>Recetas sugeridas (Próximamente)</p>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}
