import { useMsal, useAccount } from '@azure/msal-react';
import { LogOut, User, CheckCircle2, AlertCircle, ShieldCheck, Search, Sparkles, Loader2, UtensilsCrossed, Heart, Plus, History, ChefHat } from 'lucide-react';
import Sidebar from '../components/Sidebar';
import RecipeCard from '../components/RecipeCard';
import RecipeDetailModal from '../components/RecipeDetailModal';
import CreateRecipeModal from '../components/CreateRecipeModal';
import HistoryModal from '../components/HistoryModal';
import SemaforoIcon from '../components/SemaforoIcon';
import { useState, useEffect } from 'react';
import axiosClient from '../api/axiosClient';
import '../styles/dashboard.css';

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
  const [editingReceta, setEditingReceta] = useState(null);

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

  // Eliminar una receta propia
  const handleEliminarReceta = async (receta) => {
    if (!window.confirm(`¿Seguro que quieres eliminar "${receta.titulo}"? Esta acción no se puede deshacer.`)) {
      return;
    }
    try {
      await axiosClient.delete(`/recetas/${receta.id}`);
      setSelectedReceta(null);
      cargarRecetas();
    } catch (err) {
      console.error('Error al eliminar receta:', err);
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
      // cambios aqui
      .then(res => setBackendStatus(res.data?.status === 'ok' ? 'Conectado (OK)' : (typeof res.data === 'string' ? res.data : JSON.stringify(res.data))))
      // hasta aqui
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

      <main className="main-content dash-main-content">

        {/* Barra superior de navegación */}
        <header className="top-nav">
          {/* cambios aqui */}
          <div className="dash-nav-tabs-group">
            <button
              onClick={() => setActiveTab('EXPLORAR')}
              className={`dash-tab-button ${activeTab === 'EXPLORAR' ? 'active' : ''}`}
            >
              Explorar
            </button>

            <button
              onClick={() => setActiveTab('FAVORITOS')}
              className={`dash-tab-button dash-tab-button--icon ${activeTab === 'FAVORITOS' ? 'active' : ''}`}
            >
              <Heart size={16} color="#EF4444" fill={activeTab === 'FAVORITOS' ? '#EF4444' : 'none'} /> Mis Favoritas
            </button>

            <button
              onClick={() => setActiveTab('MIS_RECETAS')}
              className={`dash-tab-button ${activeTab === 'MIS_RECETAS' ? 'active' : ''}`}
            >
              Mis Recetas
            </button>

            <button
              onClick={() => setShowHistoryModal(true)}
              className="dash-history-button"
            >
              <History size={16} /> Historial
            </button>

            <button
              onClick={() => {
                setEditingReceta(null);
                setShowCreateModal(true);
              }}
              className="btn btn-gradient dash-create-button"
            >
              <Plus size={16} /> Crear Receta
            </button>
          </div>

          <div className="dash-user-group">
            <span
              className="dash-user-name"
              title={userData?.nombre || account?.name || 'Usuario'}
            >
              Hola, {((userData?.nombre || account?.name || 'Usuario').split(' ')[0])}
            </span>
            <div className="dash-user-avatar">
              <User size={18} color="#666" />
            </div>
            <button
              className="btn btn-outline dash-logout-button"
              onClick={handleLogout}
              title="Cerrar sesión"
            >
              <LogOut size={16} /> Salir
            </button>
          </div>
          {/* hasta aqui */}
        </header>

        <div>
          {/* Header principal con resumen inteligente */}
          <div className="dash-summary-header-row">
            <div>
              <h1 className="dash-summary-title">
                {activeTab === 'EXPLORAR' && "¿Qué hay pa' cocinar hoy?"}
                {activeTab === 'FAVORITOS' && (<>Tus Recetas Favoritas <Heart size={22} color="#EF4444" fill="#EF4444" /></>)}
                {activeTab === 'MIS_RECETAS' && (<>Recetas Creadas por Ti <ChefHat size={22} color="var(--primary-dark)" /></>)}
              </h1>
              <p className="dash-summary-subtitle">
                {activeTab === 'EXPLORAR' && (
                  countVerdes > 0
                    ? (<><Sparkles size={16} color="var(--primary-dark)" /> ¡Tienes {countVerdes} receta{countVerdes > 1 ? 's' : ''} lista{countVerdes > 1 ? 's' : ''} para preparar ahora mismo con tu despensa!</>)
                    : `Tienes ${despensaCount} ingredientes registrados. Comparamos tu despensa contra todas las recetas disponibles.`
                )}
                {activeTab === 'FAVORITOS' && "Guarda tus preparaciones preferidas y comprueba si tienes los ingredientes a mano."}
                {activeTab === 'MIS_RECETAS' && "Administra tus recetas personalizadas y compártelas con tu despensa."}
              </p>
            </div>

            {/* Buscador de recetas */}
            <div className="dash-search-wrapper">
              <input
                type="text"
                placeholder="Buscar recetas..."
                value={searchReceta}
                onChange={(e) => setSearchReceta(e.target.value)}
                className="dash-search-input"
              />
              <Search size={18} color="#9CA3AF" className="dash-search-icon" />
            </div>
          </div>

          {/* Filtros por estado semáforo */}
          <div className="dash-filters-row">
            <button
              onClick={() => setFiltroEstado('TODAS')}
              className={`btn ${filtroEstado === 'TODAS' ? 'btn-primary' : 'btn-outline'} dash-filter-button`}
            >
              Todas ({recetas.length})
            </button>
            <button
              onClick={() => setFiltroEstado('VERDE')}
              className={`btn ${filtroEstado === 'VERDE' ? 'btn-primary' : 'btn-outline'} dash-filter-button dash-filter-button--icon ${filtroEstado === 'VERDE' ? 'active-verde' : ''}`}
            >
              <SemaforoIcon estado="VERDE" size={9} /> Listas para cocinar ({countVerdes})
            </button>
            <button
              onClick={() => setFiltroEstado('AMARILLO')}
              className={`btn ${filtroEstado === 'AMARILLO' ? 'btn-primary' : 'btn-outline'} dash-filter-button dash-filter-button--icon ${filtroEstado === 'AMARILLO' ? 'active-amarillo' : ''}`}
            >
              <SemaforoIcon estado="AMARILLO" size={9} /> Casi listas ({countAmarillos})
            </button>
            <button
              onClick={() => setFiltroEstado('ROJO')}
              className={`btn ${filtroEstado === 'ROJO' ? 'btn-primary' : 'btn-outline'} dash-filter-button dash-filter-button--icon ${filtroEstado === 'ROJO' ? 'active-rojo' : ''}`}
            >
              <SemaforoIcon estado="ROJO" size={9} /> Faltan ingredientes ({recetas.filter(r => r.estadoGeneral === 'ROJO').length})
            </button>
          </div>

          {/* Grid de Recetas */}
          {loadingRecetas ? (
            <div className="dash-loading-block">
              <Loader2 size={24} className="spin" color="var(--primary-dark)" />
              <span>Calculando coincidencias con tu despensa...</span>
            </div>
          ) : recetasFiltradas.length === 0 ? (
            <div className="dash-empty-block">
              <UtensilsCrossed size={48} color="#9CA3AF" className="dash-empty-icon" />
              <h3 className="dash-empty-title">No se encontraron recetas</h3>
              <p className="dash-empty-subtitle">
                {activeTab === 'FAVORITOS'
                  ? (<>Aún no has marcado recetas como favoritas. Haz clic en el corazón <Heart size={14} color="#EF4444" fill="#EF4444" className="dash-inline-heart-icon" /> de cualquier receta.</>)
                  : (activeTab === 'MIS_RECETAS'
                      ? 'No has creado recetas aún. ¡Haz clic en "+ Crear Receta" para agregar la tuya!'
                      : 'Prueba cambiando el filtro o agregando ingredientes a tu despensa.')}
              </p>
            </div>
          ) : (
            <div className="dash-recipes-grid">
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
          <div className="dash-backend-status-bar">
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
          esPropia={activeTab === 'MIS_RECETAS'}
          onEditar={(receta) => {
            setEditingReceta(receta);
            setSelectedReceta(null);
            setShowCreateModal(true);
          }}
          onEliminar={handleEliminarReceta}
        />
      )}

      {/* Modal de Crear/Editar Receta */}
      {showCreateModal && (
        <CreateRecipeModal
          recetaEditar={editingReceta}
          onClose={() => {
            setShowCreateModal(false);
            setEditingReceta(null);
          }}
          onRecipeCreated={() => {
            setActiveTab('MIS_RECETAS');
            setEditingReceta(null);
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
