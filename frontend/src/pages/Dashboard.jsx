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
import * as styles from '../styles/dashboard.styles';

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

      <main className="main-content" style={styles.mainContent}>

        {/* Barra superior de navegación */}
        <header className="top-nav">
          {/* cambios aqui */}
          <div style={styles.navTabsGroup}>
            <button
              onClick={() => setActiveTab('EXPLORAR')}
              style={styles.getTabButton(activeTab === 'EXPLORAR')}
            >
              Explorar
            </button>

            <button
              onClick={() => setActiveTab('FAVORITOS')}
              style={styles.getTabButtonWithIcon(activeTab === 'FAVORITOS')}
            >
              <Heart size={16} color="#EF4444" fill={activeTab === 'FAVORITOS' ? '#EF4444' : 'none'} /> Mis Favoritas
            </button>

            <button
              onClick={() => setActiveTab('MIS_RECETAS')}
              style={styles.getTabButton(activeTab === 'MIS_RECETAS')}
            >
              Mis Recetas
            </button>

            <button
              onClick={() => setShowHistoryModal(true)}
              style={styles.historyButton}
            >
              <History size={16} /> Historial
            </button>

            <button
              onClick={() => {
                setEditingReceta(null);
                setShowCreateModal(true);
              }}
              className="btn btn-gradient"
              style={styles.createButton}
            >
              <Plus size={16} /> Crear Receta
            </button>
          </div>

          <div style={styles.userGroup}>
            <span
              style={styles.userName}
              title={userData?.nombre || account?.name || 'Usuario'}
            >
              Hola, {((userData?.nombre || account?.name || 'Usuario').split(' ')[0])}
            </span>
            <div style={styles.userAvatar}>
              <User size={18} color="#666" />
            </div>
            <button
              className="btn btn-outline"
              onClick={handleLogout}
              title="Cerrar sesión"
              style={styles.logoutButton}
            >
              <LogOut size={16} /> Salir
            </button>
          </div>
          {/* hasta aqui */}
        </header>

        <div>
          {/* Header principal con resumen inteligente */}
          <div style={styles.summaryHeaderRow}>
            <div>
              <h1 style={styles.summaryTitle}>
                {activeTab === 'EXPLORAR' && "¿Qué hay pa' cocinar hoy?"}
                {activeTab === 'FAVORITOS' && (<>Tus Recetas Favoritas <Heart size={22} color="#EF4444" fill="#EF4444" /></>)}
                {activeTab === 'MIS_RECETAS' && (<>Recetas Creadas por Ti <ChefHat size={22} color="var(--primary-dark)" /></>)}
              </h1>
              <p style={styles.summarySubtitle}>
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
            <div style={styles.searchWrapper}>
              <input
                type="text"
                placeholder="Buscar recetas..."
                value={searchReceta}
                onChange={(e) => setSearchReceta(e.target.value)}
                style={styles.searchInput}
              />
              <Search size={18} color="#9CA3AF" style={styles.searchIcon} />
            </div>
          </div>

          {/* Filtros por estado semáforo */}
          <div style={styles.filtersRow}>
            <button
              onClick={() => setFiltroEstado('TODAS')}
              className={`btn ${filtroEstado === 'TODAS' ? 'btn-primary' : 'btn-outline'}`}
              style={styles.filterButtonBase}
            >
              Todas ({recetas.length})
            </button>
            <button
              onClick={() => setFiltroEstado('VERDE')}
              className={`btn ${filtroEstado === 'VERDE' ? 'btn-primary' : 'btn-outline'}`}
              style={styles.getFilterButtonWithIcon(filtroEstado === 'VERDE', '#10B981')}
            >
              <SemaforoIcon estado="VERDE" size={9} /> Listas para cocinar ({countVerdes})
            </button>
            <button
              onClick={() => setFiltroEstado('AMARILLO')}
              className={`btn ${filtroEstado === 'AMARILLO' ? 'btn-primary' : 'btn-outline'}`}
              style={styles.getFilterButtonWithIcon(filtroEstado === 'AMARILLO', '#F59E0B')}
            >
              <SemaforoIcon estado="AMARILLO" size={9} /> Casi listas ({countAmarillos})
            </button>
            <button
              onClick={() => setFiltroEstado('ROJO')}
              className={`btn ${filtroEstado === 'ROJO' ? 'btn-primary' : 'btn-outline'}`}
              style={styles.getFilterButtonWithIcon(filtroEstado === 'ROJO', '#EF4444')}
            >
              <SemaforoIcon estado="ROJO" size={9} /> Faltan ingredientes ({recetas.filter(r => r.estadoGeneral === 'ROJO').length})
            </button>
          </div>

          {/* Grid de Recetas */}
          {loadingRecetas ? (
            <div style={styles.loadingBlock}>
              <Loader2 size={24} className="spin" color="var(--primary-dark)" />
              <span>Calculando coincidencias con tu despensa...</span>
            </div>
          ) : recetasFiltradas.length === 0 ? (
            <div style={styles.emptyBlock}>
              <UtensilsCrossed size={48} color="#9CA3AF" style={styles.emptyIcon} />
              <h3 style={styles.emptyTitle}>No se encontraron recetas</h3>
              <p style={styles.emptySubtitle}>
                {activeTab === 'FAVORITOS'
                  ? (<>Aún no has marcado recetas como favoritas. Haz clic en el corazón <Heart size={14} color="#EF4444" fill="#EF4444" style={styles.inlineHeartIcon} /> de cualquier receta.</>)
                  : (activeTab === 'MIS_RECETAS'
                      ? 'No has creado recetas aún. ¡Haz clic en "+ Crear Receta" para agregar la tuya!'
                      : 'Prueba cambiando el filtro o agregando ingredientes a tu despensa.')}
              </p>
            </div>
          ) : (
            <div style={styles.recipesGrid}>
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
          <div style={styles.backendStatusBar}>
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
