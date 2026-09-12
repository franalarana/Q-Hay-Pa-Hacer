import { useMsal, useAccount } from '@azure/msal-react';
import { LogOut, User, CheckCircle2, AlertCircle, ShieldCheck, Search, Sparkles, Loader2, UtensilsCrossed, Heart, Plus, History, ChefHat } from 'lucide-react';
import Sidebar from '../components/Sidebar';
import RecipeCard from '../components/RecipeCard';
import RecipeDetailModal from '../components/RecipeDetailModal';
import CreateRecipeModal from '../components/CreateRecipeModal';
import HistoryModal from '../components/HistoryModal';
import { useState, useEffect } from 'react';
import axiosClient from '../api/axiosClient';

export default function Dashboard() {
  const { instance, accounts } = useMsal();
  const account = useAccount(accounts[0] || {});
  const [backendStatus, setBackendStatus] = useState('Verificando conexión con backend...');
  const [userData, setUserData] = useState(null);
  const [loadingUser, setLoadingUser] = useState(true);

  // Estados de vistas y modales
  const [activeTab, setActiveTab] = useState('EXPLORAR'); // EXPLORAR, FAVORITOS, MIS_RECETAS
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showHistoryModal, setShowHistoryModal] = useState(false);
  const [selectedReceta, setSelectedReceta] = useState(null);

  // Estados de datos
  const [recetas, setRecetas] = useState([]);
  const [favoritosIds, setFavoritosIds] = useState([]);
  const [loadingRecetas, setLoadingRecetas] = useState(true);
  const [filtroEstado, setFiltroEstado] = useState('TODAS'); // TODAS, VERDE, AMARILLO, ROJO
  const [searchReceta, setSearchReceta] = useState('');
  const [despensaCount, setDespensaCount] = useState(0);

  const handleLogout = () => {
    instance.logoutRedirect({
      postLogoutRedirectUri: window.location.origin,
    });
  };

  // Cargar IDs de favoritos del usuario
  const cargarFavoritosIds = async () => {
    try {
      const res = await axiosClient.get('/favoritos/ids');
      setFavoritosIds(res.data);
    } catch (err) {
      console.error('Error al cargar favoritos:', err);
    }
  };

  // Alternar favorito (toggle)
  const handleToggleFavorito = async (recetaId) => {
    try {
      await axiosClient.post(`/favoritos/${recetaId}`);
      if (favoritosIds.includes(recetaId)) {
        setFavoritosIds(favoritosIds.filter(id => id !== recetaId));
      } else {
        setFavoritosIds([...favoritosIds, recetaId]);
      }
    } catch (err) {
      console.error('Error al alternar favorito:', err);
    }
  };

  // Cargar recetas según pestaña activa
  const cargarRecetas = async () => {
    try {
      setLoadingRecetas(true);
      let endpoint = '/recetas/sugeridas';
      if (activeTab === 'FAVORITOS') {
        endpoint = '/favoritos';
      } else if (activeTab === 'MIS_RECETAS') {
        endpoint = '/recetas/mis-recetas';
      }
      const res = await axiosClient.get(endpoint);
      setRecetas(res.data);
    } catch (err) {
      console.error('Error al cargar recetas:', err);
    } finally {
      setLoadingRecetas(false);
    }
  };

  useEffect(() => {
    // 1. Probar salud pública del backend
    axiosClient.get('/public/health')
      .then(res => setBackendStatus(res.data))
      .catch(err => setBackendStatus('Error de conexión: ' + (err.response?.data || err.message)));

    // 2. Probar endpoint protegido /me
    axiosClient.get('/me')
      .then(res => setUserData(res.data))
      .catch(err => console.error('Error al consultar /me:', err))
      .finally(() => setLoadingUser(false));

    // 3. Cargar IDs de favoritos
    cargarFavoritosIds();
  }, []);

  useEffect(() => {
    cargarRecetas();
  }, [activeTab]);

  // Filtrado de recetas por estado y por texto de búsqueda
  const recetasFiltradas = recetas.filter(r => {
    const coincideEstado = (filtroEstado === 'TODAS') || (r.estadoGeneral === filtroEstado);
    const coincideTexto = r.titulo.toLowerCase().includes(searchReceta.toLowerCase()) ||
                          r.descripcion?.toLowerCase().includes(searchReceta.toLowerCase());
    return coincideEstado && coincideTexto;
  });

  const countVerdes = recetas.filter(r => r.estadoGeneral === 'VERDE').length;
  const countAmarillos = recetas.filter(r => r.estadoGeneral === 'AMARILLO').length;

  return (
    <div className="app-container">
      {/* Sidebar de despensa */}
      <Sidebar 
        onDespensaChange={(items) => {
          setDespensaCount(items.length);
          cargarRecetas();
        }} 
      />
      
      <main className="main-content">
        {/* Barra superior de navegación */}
        <header className="top-nav">
          <button 
            onClick={() => setActiveTab('EXPLORAR')}
            style={{
              background: 'none', border: 'none', cursor: 'pointer',
              color: activeTab === 'EXPLORAR' ? 'var(--primary-dark)' : 'var(--text-main)',
              fontWeight: activeTab === 'EXPLORAR' ? '700' : '500',
              borderBottom: activeTab === 'EXPLORAR' ? '2px solid var(--primary-dark)' : 'none',
              paddingBottom: '4px', fontSize: '0.95rem'
            }}
          >
            Explorar
          </button>

          <button 
            onClick={() => setActiveTab('FAVORITOS')}
            style={{
              background: 'none', border: 'none', cursor: 'pointer',
              color: activeTab === 'FAVORITOS' ? 'var(--primary-dark)' : 'var(--text-main)',
              fontWeight: activeTab === 'FAVORITOS' ? '700' : '500',
              borderBottom: activeTab === 'FAVORITOS' ? '2px solid var(--primary-dark)' : 'none',
              paddingBottom: '4px', fontSize: '0.95rem',
              display: 'flex', alignItems: 'center', gap: '4px'
            }}
          >
            <Heart size={16} color="#EF4444" fill={activeTab === 'FAVORITOS' ? '#EF4444' : 'none'} /> Mis Favoritas
          </button>

          <button 
            onClick={() => setActiveTab('MIS_RECETAS')}
            style={{
              background: 'none', border: 'none', cursor: 'pointer',
              color: activeTab === 'MIS_RECETAS' ? 'var(--primary-dark)' : 'var(--text-main)',
              fontWeight: activeTab === 'MIS_RECETAS' ? '700' : '500',
              borderBottom: activeTab === 'MIS_RECETAS' ? '2px solid var(--primary-dark)' : 'none',
              paddingBottom: '4px', fontSize: '0.95rem'
            }}
          >
            Mis Recetas
          </button>

          <button 
            onClick={() => setShowHistoryModal(true)}
            style={{
              background: 'none', border: 'none', cursor: 'pointer',
              color: 'var(--text-main)', fontWeight: '500',
              paddingBottom: '4px', fontSize: '0.95rem',
              display: 'flex', alignItems: 'center', gap: '4px'
            }}
          >
            <History size={16} /> Historial
          </button>

          <button 
            onClick={() => setShowCreateModal(true)}
            className="btn btn-primary"
            style={{ padding: '8px 16px', fontSize: '0.85rem', gap: '6px' }}
          >
            <Plus size={16} /> Crear Receta
          </button>
          
          <div style={{ display: 'flex', alignItems: 'center', gap: '14px', marginLeft: 'auto' }}>
            <span style={{ fontSize: '0.9rem', color: 'var(--text-muted)' }}>
              Hola, {userData?.nombre || account?.name || 'Usuario'}
            </span>
            <div style={{
              width: '38px', height: '38px', borderRadius: '50%', 
              backgroundColor: '#E0E0E0', display: 'flex', 
              alignItems: 'center', justifyContent: 'center'
            }}>
              <User size={18} color="#666" />
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
              <h1 style={{ marginBottom: '6px', fontSize: '1.9rem' }}>
                {activeTab === 'EXPLORAR' && "¿Qué hay pa' cocinar hoy?"}
                {activeTab === 'FAVORITOS' && "Tus Recetas Favoritas ❤️"}
                {activeTab === 'MIS_RECETAS' && "Recetas Creadas por Ti 👨‍🍳"}
              </h1>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.95rem' }}>
                {activeTab === 'EXPLORAR' && (
                  countVerdes > 0 
                    ? `🎉 ¡Tienes ${countVerdes} receta${countVerdes > 1 ? 's' : ''} lista${countVerdes > 1 ? 's' : ''} para preparar ahora mismo con tu despensa!` 
                    : `Tienes ${despensaCount} ingredientes registrados. Comparamos tu despensa contra todas las recetas disponibles.`
                )}
                {activeTab === 'FAVORITOS' && "Guarda tus preparaciones preferidas y comprueba si tienes los ingredientes a mano."}
                {activeTab === 'MIS_RECETAS' && "Administra tus recetas personalizadas y compártelas con tu despensa."}
              </p>
            </div>

            {/* Buscador de recetas */}
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

          {/* Grid de Recetas */}
          {loadingRecetas ? (
            <div style={{ textAlign: 'center', padding: '60px 0', color: 'var(--text-muted)', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px' }}>
              <Loader2 size={24} className="spin" color="var(--primary-dark)" />
              <span>Calculando coincidencias con tu despensa...</span>
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
                {activeTab === 'FAVORITOS' 
                  ? 'Aún no has marcado recetas como favoritas. Haz clic en el corazón ❤️ de cualquier receta.' 
                  : (activeTab === 'MIS_RECETAS' 
                      ? 'No has creado recetas aún. ¡Haz clic en "+ Crear Receta" para agregar la tuya!' 
                      : 'Prueba cambiando el filtro o agregando ingredientes a tu despensa.')}
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
                  isFavorito={favoritosIds.includes(receta.id)}
                  onToggleFavorito={handleToggleFavorito}
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
            <span>Usuario: {account?.username || 'Invitado'}</span>
          </div>
        </div>
      </main>

      {/* Modal de Detalle de Receta */}
      {selectedReceta && (
        <RecipeDetailModal 
          receta={selectedReceta} 
          isFavorito={favoritosIds.includes(selectedReceta.id)}
          onToggleFavorito={handleToggleFavorito}
          onCookingDone={cargarRecetas}
          onClose={() => setSelectedReceta(null)} 
        />
      )}

      {/* Modal de Crear Receta */}
      {showCreateModal && (
        <CreateRecipeModal 
          onClose={() => setShowCreateModal(false)}
          onRecipeCreated={() => {
            setActiveTab('MIS_RECETAS');
            cargarRecetas();
          }}
        />
      )}

      {/* Modal de Historial de Cocina */}
      {showHistoryModal && (
        <HistoryModal 
          onClose={() => setShowHistoryModal(false)}
        />
      )}
    </div>
  );
}
