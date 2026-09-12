import { useMsal, useAccount } from '@azure/msal-react';
import { LogOut, User, CheckCircle2, AlertCircle, ShieldCheck } from 'lucide-react';
import Sidebar from '../components/Sidebar';
import { useState, useEffect } from 'react';
import axiosClient from '../api/axiosClient';

export default function Dashboard() {
  const { instance, accounts } = useMsal();
  const account = useAccount(accounts[0] || {});
  const [backendStatus, setBackendStatus] = useState('Verificando conexión con backend...');
  const [userData, setUserData] = useState(null);
  const [loadingUser, setLoadingUser] = useState(true);

  const handleLogout = () => {
    instance.logoutRedirect({
      postLogoutRedirectUri: window.location.origin,
    });
  };

  useEffect(() => {
    // 1. Probar salud pública del backend
    axiosClient.get('/public/health')
      .then(res => {
        setBackendStatus(res.data);
      })
      .catch(err => {
        setBackendStatus('Error de conexión: ' + (err.response?.data || err.message));
      });

    // 2. Probar endpoint protegido /me (valida JWT y devuelve/crea usuario en BD)
    axiosClient.get('/me')
      .then(res => {
        setUserData(res.data);
      })
      .catch(err => {
        console.error('Error al consultar /me:', err);
      })
      .finally(() => {
        setLoadingUser(false);
      });
  }, []);

  const [despensaItems, setDespensaItems] = useState([]);

  return (
    <div className="app-container">
      <Sidebar onDespensaChange={(items) => setDespensaItems(items)} />
      
      <main className="main-content">
        <header className="top-nav">
          <a href="#" className="active" style={{ color: 'var(--primary-dark)', fontWeight: '600' }}>Inicio</a>
          <a href="#">Mis Recetas</a>
          <a href="#">Explorar</a>
          
          <div style={{ display: 'flex', alignItems: 'center', gap: '16px', marginLeft: 'auto' }}>
            <span style={{ fontSize: '0.9rem', color: 'var(--text-muted)' }}>
              Hola, {userData?.nombre || account?.name || 'Usuario'}
            </span>
            <div style={{
              width: '40px', height: '40px', borderRadius: '50%', 
              backgroundColor: '#E0E0E0', display: 'flex', 
              alignItems: 'center', justifyContent: 'center'
            }}>
              <User size={20} color="#666" />
            </div>
            <button 
              className="btn btn-outline" 
              onClick={handleLogout} 
              title="Cerrar sesión" 
              style={{ padding: '8px 12px', borderRadius: 'var(--border-radius-sm)', display: 'flex', alignItems: 'center', gap: '6px' }}
            >
              <LogOut size={16} /> Salir
            </button>
          </div>
        </header>

        <div>
          <h1 style={{ marginBottom: '8px' }}>Bienvenido a Qué hay pa' hacer</h1>
          <p style={{ color: 'var(--text-muted)', marginBottom: '24px' }}>
            Tienes <strong>{despensaItems.length}</strong> ingrediente{despensaItems.length === 1 ? '' : 's'} disponible{despensaItems.length === 1 ? '' : 's'} en tu despensa.
          </p>

          {/* Tarjeta de estado de autenticación y BFF */}
          <div style={{
            backgroundColor: 'white',
            borderRadius: 'var(--border-radius-md)',
            padding: '20px 24px',
            marginBottom: '32px',
            boxShadow: 'var(--shadow-sm)',
            border: '1px solid #E5E7EB',
            display: 'flex',
            flexDirection: 'column',
            gap: '12px'
          }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px', fontWeight: '600', color: 'var(--primary-dark)' }}>
              <ShieldCheck size={22} />
              <span>Estado de Autenticación & Conexión BFF</span>
            </div>
            
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '16px', fontSize: '0.9rem' }}>
              <div>
                <strong style={{ color: 'var(--text-main)' }}>Usuario Entra ID:</strong>
                <p style={{ margin: '4px 0 0', color: 'var(--text-muted)' }}>{account?.username || account?.name || 'Cargando...'}</p>
              </div>

              <div>
                <strong style={{ color: 'var(--text-main)' }}>Backend (/public/health):</strong>
                <p style={{ margin: '4px 0 0', color: backendStatus.includes('Error') ? '#EF4444' : '#10B981', display: 'flex', alignItems: 'center', gap: '4px' }}>
                  {backendStatus.includes('Error') ? <AlertCircle size={16} /> : <CheckCircle2 size={16} />}
                  {backendStatus}
                </p>
              </div>

              <div>
                <strong style={{ color: 'var(--text-main)' }}>Base de datos (/api/me):</strong>
                <p style={{ margin: '4px 0 0', color: userData ? '#10B981' : (loadingUser ? 'var(--text-muted)' : '#F59E0B'), display: 'flex', alignItems: 'center', gap: '4px' }}>
                  {userData ? <CheckCircle2 size={16} /> : (loadingUser ? null : <AlertCircle size={16} />)}
                  {userData ? `Sincronizado (ID local: ${userData.id})` : (loadingUser ? 'Validando JWT...' : 'Pendiente de conexión')}
                </p>
              </div>
            </div>
          </div>

          <div style={{ display: 'flex', gap: '24px', flexWrap: 'wrap' }}>
            <div style={{ 
              width: '100%', maxWidth: '320px', height: '200px', 
              backgroundColor: '#F9FAFB', borderRadius: 'var(--border-radius-md)',
              display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center',
              border: '2px dashed #D1D5DB', padding: '16px', textAlign: 'center'
            }}>
              <p style={{ color: '#9CA3AF', fontWeight: '500', marginBottom: '8px' }}>Recetas sugeridas</p>
              <span style={{ fontSize: '0.8rem', color: '#9CA3AF' }}>Se activarán con la comparación de despensa</span>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}

