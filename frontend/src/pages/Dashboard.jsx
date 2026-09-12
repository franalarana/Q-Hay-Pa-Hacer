import { useMsal, useAccount } from '@azure/msal-react';
import { LogOut, User, CheckCircle2, AlertCircle, ShieldCheck, Search, Sparkles, Filter, Loader2, UtensilsCrossed } from 'lucide-react';
import Sidebar from '../components/Sidebar';
import RecipeCard from '../components/RecipeCard';
import RecipeDetailModal from '../components/RecipeDetailModal';
import { useState, useEffect } from 'react';
import axiosClient from '../api/axiosClient';

export default function Dashboard() {
  const { instance, accounts } = useMsal();
  const account = useAccount(accounts[0] || {});
  const [backendStatus, setBackendStatus] = useState('Verificando conexión con backend...');
  const [userData, setUserData] = useState(null);
  const [loadingUser, setLoadingUser] = useState(true);

  // Estados de recetas y despensa
  const [recetas, setRecetas] = useState([]);
  const [loadingRecetas, setLoadingRecetas] = useState(true);
  const [filtroEstado, setFiltroEstado] = useState('TODAS'); // TODAS, VERDE, AMARILLO, ROJO
  const [searchReceta, setSearchReceta] = useState('');
  const [selectedReceta, setSelectedReceta] = useState(null);
  const [despensaCount, setDespensaCount] = useState(0);

  const handleLogout = () => {
    instance.logoutRedirect({
      postLogoutRedirectUri: window.location.origin,
    });
  };

  // Función para cargar/recargar las recetas sugeridas comparadas con la despensa
  const cargarRecetasSugeridas = async () => {
    try {
      setLoadingRecetas(true);
      const res = await axiosClient.get('/recetas/sugeridas');
      setRecetas(res.data);
    } catch (err) {
      console.error('Error al cargar recetas sugeridas:', err);
    } finally {
      setLoadingRecetas(false);
    }
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

    // 3. Carga inicial de recetas
    cargarRecetasSugeridas();
  }, []);

  // Filtrado de recetas por estado y por texto de búsqueda
  const recetasFiltradas = recetas.filter(r => {
    const coincideEstado = (filtroEstado === 'TODAS') || (r.estadoGeneral === filtroEstado);
    const coincideTexto = r.titulo.toLowerCase().includes(searchReceta.toLowerCase()) ||
                          r.descripcion.toLowerCase().includes(searchReceta.toLowerCase());
    return coincideEstado && coincideTexto;
  });

  const countVerdes = recetas.filter(r => r.estadoGeneral === 'VERDE').length;
  const countAmarillos = recetas.filter(r => r.estadoGeneral === 'AMARILLO').length;

  return (
    <div className="app-container">
      {/* Sidebar de despensa que dispara recarga de recetas automáticamente al cambiar */}
      <Sidebar 
        onDespensaChange={(items) => {
          setDespensaCount(items.length);
          cargarRecetasSugeridas();
        }} 
      />
      
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
          {/* Header principal con resumen inteligente */}
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '16px', marginBottom: '24px' }}>
            <div>
              <h1 style={{ marginBottom: '6px', fontSize: '2rem' }}>¿Qué hay pa' cocinar hoy?</h1>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.95rem' }}>
                {countVerdes > 0 
                  ? `🎉 ¡Tienes ${countVerdes} receta${countVerdes > 1 ? 's' : ''} lista${countVerdes > 1 ? 's' : ''} para preparar de inmediato con tus ingredientes!` 
                  : (despensaCount === 0 
                      ? 'Agrega ingredientes en el panel izquierdo para que el algoritmo busque qué puedes cocinar.' 
                      : `Tienes ${despensaCount} ingredientes registrados. Te mostramos qué te falta para completar cada receta.`)}
              </p>
            </div>

            {/* Barra de búsqueda de recetas */}
            <div style={{ position: 'relative', minWidth: '260px' }}>
              <input 
                type="text" 
                placeholder="Buscar recetas..."
                value={searchReceta}
                onChange={(e) => setSearchReceta(e.target.value)}
                style={{
                  width: '100%',
                  padding: '10px 38px 10px 14px',
                  borderRadius: '50px',
                  border: '1px solid #E5E7EB',
                  outline: 'none',
                  fontSize: '0.9rem',
                  backgroundColor: '#F9FAFB'
                }}
              />
              <Search size={18} color="#9CA3AF" style={{ position: 'absolute', right: '14px', top: '11px' }} />
            </div>
          </div>

          {/* Filtros por estado semáforo */}
          <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap', marginBottom: '28px' }}>
            <button 
              onClick={() => setFiltroEstado('TODAS')}
              className={`btn ${filtroEstado === 'TODAS' ? 'btn-primary' : 'btn-outline'}`}
              style={{ fontSize: '0.85rem', padding: '6px 14px' }}
            >
              Todas ({recetas.length})
            </button>
            <button 
              onClick={() => setFiltroEstado('VERDE')}
              className={`btn ${filtroEstado === 'VERDE' ? 'btn-primary' : 'btn-outline'}`}
              style={{ fontSize: '0.85rem', padding: '6px 14px', backgroundColor: filtroEstado === 'VERDE' ? '#10B981' : undefined, color: filtroEstado === 'VERDE' ? 'white' : undefined }}
            >
              🟢 Listas para cocinar ({countVerdes})
            </button>
            <button 
              onClick={() => setFiltroEstado('AMARILLO')}
              className={`btn ${filtroEstado === 'AMARILLO' ? 'btn-primary' : 'btn-outline'}`}
              style={{ fontSize: '0.85rem', padding: '6px 14px', backgroundColor: filtroEstado === 'AMARILLO' ? '#F59E0B' : undefined, color: filtroEstado === 'AMARILLO' ? 'white' : undefined }}
            >
              🟡 Casi listas ({countAmarillos})
            </button>
            <button 
              onClick={() => setFiltroEstado('ROJO')}
              className={`btn ${filtroEstado === 'ROJO' ? 'btn-primary' : 'btn-outline'}`}
              style={{ fontSize: '0.85rem', padding: '6px 14px', backgroundColor: filtroEstado === 'ROJO' ? '#EF4444' : undefined, color: filtroEstado === 'ROJO' ? 'white' : undefined }}
            >
              🔴 Faltan ingredientes ({recetas.filter(r => r.estadoGeneral === 'ROJO').length})
            </button>
          </div>

          {/* Grid de Recetas Sugeridas */}
          {loadingRecetas ? (
            <div style={{ textAlign: 'center', padding: '60px 0', color: 'var(--text-muted)', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px' }}>
              <Loader2 size={24} className="spin" color="var(--primary-dark)" />
              <span>Comparando despensa y calculando mejores recetas...</span>
            </div>
          ) : recetasFiltradas.length === 0 ? (
            <div style={{
              textAlign: 'center',
              padding: '60px 20px',
              backgroundColor: '#F9FAFB',
              borderRadius: 'var(--border-radius-lg)',
              border: '2px dashed #E5E7EB'
            }}>
              <UtensilsCrossed size={48} color="#9CA3AF" style={{ marginBottom: '12px' }} />
              <h3 style={{ color: 'var(--text-main)', marginBottom: '8px' }}>No se encontraron recetas</h3>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
                Prueba cambiando el filtro de estado o ajustando los ingredientes de tu despensa.
              </p>
            </div>
          ) : (
            <div style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))',
              gap: '24px',
              marginBottom: '32px'
            }}>
              {recetasFiltradas.map(receta => (
                <RecipeCard 
                  key={receta.id} 
                  receta={receta} 
                  onSelect={(r) => setSelectedReceta(r)} 
                />
              ))}
            </div>
          )}

          {/* Panel de estado de conexión con backend */}
          <div style={{
            backgroundColor: '#F9FAFB',
            borderRadius: 'var(--border-radius-sm)',
            padding: '12px 16px',
            fontSize: '0.8rem',
            color: 'var(--text-muted)',
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            borderTop: '1px solid #E5E7EB'
          }}>
            <span>Backend: {backendStatus}</span>
            <span>Usuario autenticado: {account?.username || 'Invitado'}</span>
          </div>
        </div>
      </main>

      {/* Modal de detalle y preparación */}
      {selectedReceta && (
        <RecipeDetailModal 
          receta={selectedReceta} 
          onClose={() => setSelectedReceta(null)} 
        />
      )}
    </div>
  );
}
