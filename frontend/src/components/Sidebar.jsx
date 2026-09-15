import { useState, useEffect } from 'react';
import { Search, Plus, Trash2, Edit2, Check, X, AlertCircle, Loader2 } from 'lucide-react';
import axiosClient from '../api/axiosClient';
import CustomSelect from './CustomSelect';
import * as styles from '../styles/sidebar.styles';

const UNIDADES_DISPONIBLES = [
  { value: 'UNIDAD', label: 'Unidades (ud)' },
  { value: 'GRAMOS', label: 'Gramos (g)' },
  { value: 'KILOGRAMOS', label: 'Kilogramos (kg)' },
  { value: 'MILILITROS', label: 'Mililitros (ml)' },
  { value: 'LITROS', label: 'Litros (L)' },
  { value: 'CUCHARADA', label: 'Cucharadas (cda)' },
  { value: 'CUCHARADITA', label: 'Cucharaditas (cdta)' },
  { value: 'TAZA', label: 'Tazas' },
  { value: 'PIZCA', label: 'Pizca' }
];

// cambios aqui
export const getIngredienteEmoji = (nombre = '') => {
  const n = (nombre || '').toLowerCase().trim();
  if (n.includes('huevo')) return '🥚';
  if (n.includes('leche')) return '🥛';
  if (n.includes('queso')) return '🧀';
  if (n.includes('mantequilla')) return '🧈';
  if (n.includes('arroz')) return '🍚';
  if (n.includes('harina')) return '🌾';
  if (n.includes('fideo') || n.includes('pasta') || n.includes('tallar')) return '🍝';
  if (n.includes('tomate') && n.includes('salsa')) return '🥫';
  if (n.includes('tomate')) return '🍅';
  if (n.includes('azúcar') || n.includes('azucar')) return '🍬';
  if (n.includes('aceite')) return '🫒';
  if (n.includes('sal')) return '🧂';
  if (n.includes('pimienta')) return '🌶️';
  if (n.includes('cebolla')) return '🧅';
  if (n.includes('papa')) return '🥔';
  if (n.includes('ajo')) return '🧄';
  if (n.includes('carne') || n.includes('vacuno')) return '🥩';
  if (n.includes('pollo')) return '🍗';
  if (n.includes('palta') || n.includes('aguacate')) return '🥑';
  if (n.includes('pescado') || n.includes('atun') || n.includes('atún') || n.includes('salmon') || n.includes('salmón')) return '🐟';
  if (n.includes('limon') || n.includes('limón')) return '🍋';
  if (n.includes('zanahoria')) return '🥕';
  if (n.includes('choclo') || n.includes('maiz') || n.includes('maíz')) return '🌽';
  if (n.includes('pan')) return '🍞';
  if (n.includes('oregano') || n.includes('orégano') || n.includes('albahaca') || n.includes('cilantro') || n.includes('perejil')) return '🌿';
  if (n.includes('champinon') || n.includes('champiñón') || n.includes('seta')) return '🍄';
  if (n.includes('cerdo') || n.includes('tocino')) return '🥓';
  if (n.includes('fruta') || n.includes('manzana')) return '🍎';
  if (n.includes('platano') || n.includes('plátano')) return '🍌';
  if (n.includes('agua')) return '💧';
  if (n.includes('vino')) return '🍷';
  return '🥗';
};
// hasta aqui

export default function Sidebar({ onDespensaChange }) {
  const [despensa, setDespensa] = useState([]);
  const [catalogo, setCatalogo] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');

  // Estado para el modal/formulario de agregar/editar
  const [showForm, setShowForm] = useState(false);
  const [selectedIngredienteId, setSelectedIngredienteId] = useState('');
  const [cantidad, setCantidad] = useState(1);
  const [unidad, setUnidad] = useState('UNIDAD');
  const [submitting, setSubmitting] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');

  // cambios aqui
  const [editingId, setEditingId] = useState(null);
  const [editCantidad, setEditCantidad] = useState(1);
  const [editUnidad, setEditUnidad] = useState('UNIDAD');
  const [savingEdit, setSavingEdit] = useState(false);

  const handleIniciarEdicion = (item) => {
    setEditingId(item.id);
    setEditCantidad(item.cantidad);
    setEditUnidad(item.unidad);
  };

  const handleGuardarEdicion = async (id) => {
    if (Number(editCantidad) <= 0) return;
    try {
      setSavingEdit(true);
      await axiosClient.patch(`/despensa/${id}`, {
        cantidad: Number(editCantidad),
        unidad: editUnidad
      });
      setEditingId(null);
      await cargarDatos();
    } catch (err) {
      console.error('Error al editar ingrediente:', err);
    } finally {
      setSavingEdit(false);
    }
  };
  // hasta aqui

  // Estado para crear un ingrediente propio (no está en el catálogo)
  const [creandoNuevo, setCreandoNuevo] = useState(false);
  const [nuevoNombre, setNuevoNombre] = useState('');
  const [nuevoCategoria, setNuevoCategoria] = useState('');

  // Cargar catálogo de ingredientes y despensa del usuario
  const cargarDatos = async () => {
    try {
      setLoading(true);
      const [resDespensa, resCatalogo] = await Promise.all([
        axiosClient.get('/despensa'),
        axiosClient.get('/ingredientes')
      ]);
      setDespensa(resDespensa.data);
      setCatalogo(resCatalogo.data);
      if (onDespensaChange) {
        onDespensaChange(resDespensa.data);
      }
    } catch (err) {
      console.error('Error al cargar datos de la despensa:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargarDatos();
  }, []);

  // Manejar adición o actualización en la despensa
  const handleGuardarIngrediente = async (e) => {
    e.preventDefault();

    if (creandoNuevo) {
      if (!nuevoNombre.trim() || !nuevoCategoria.trim()) {
        setErrorMsg('Ingresa el nombre y la categoría de tu nuevo ingrediente.');
        return;
      }
    } else if (!selectedIngredienteId) {
      setErrorMsg('Por favor selecciona un ingrediente.');
      return;
    }
    if (cantidad <= 0) {
      setErrorMsg('La cantidad debe ser mayor a 0.');
      return;
    }

    try {
      setSubmitting(true);
      setErrorMsg('');

      let ingredienteId = selectedIngredienteId;
      if (creandoNuevo) {
        const resNuevo = await axiosClient.post('/ingredientes', {
          nombre: nuevoNombre.trim(),
          categoria: nuevoCategoria.trim(),
          unidadBase: unidad
        });
        ingredienteId = resNuevo.data.id;
      }

      await axiosClient.post('/despensa', {
        ingredienteId: Number(ingredienteId),
        cantidad: Number(cantidad),
        unidad: unidad
      });

      // Limpiar y recargar
      setShowForm(false);
      setSelectedIngredienteId('');
      setCantidad(1);
      setUnidad('UNIDAD');
      setCreandoNuevo(false);
      setNuevoNombre('');
      setNuevoCategoria('');
      await cargarDatos();
    } catch (err) {
      setErrorMsg(err.response?.data?.message || 'Error al guardar ingrediente.');
    } finally {
      setSubmitting(false);
    }
  };

  // Ajuste rápido de cantidad (+ / -)
  const handleCambiarCantidad = async (item, delta) => {
    const nuevaCantidad = Math.max(0.5, item.cantidad + delta);
    try {
      await axiosClient.patch(`/despensa/${item.id}`, {
        cantidad: nuevaCantidad,
        unidad: item.unidad
      });
      cargarDatos();
    } catch (err) {
      console.error('Error al modificar cantidad:', err);
    }
  };

  // Eliminar ingrediente
  const handleEliminar = async (id) => {
    try {
      await axiosClient.delete(`/despensa/${id}`);
      cargarDatos();
    } catch (err) {
      console.error('Error al eliminar ingrediente:', err);
    }
  };

  // Categorías únicas del catálogo, para el selector de "ingrediente propio"
  const categoriasExistentes = [...new Set(catalogo.map(ing => ing.categoria))].sort();

  // Filtrado de ingredientes en la despensa según el buscador
  const ingredientesFiltrados = despensa.filter(item =>
    item.nombreIngrediente.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <aside className="sidebar" style={styles.asideRoot}>

      <h2 style={styles.title}>
        Tu Despensa
      </h2>

      {/* Buscador dentro de tu despensa */}
      <div style={styles.searchWrapper}>
        <input
          type="text"
          placeholder="Buscar en tu despensa..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          style={styles.searchInput}
        />
        <Search size={18} color="#666" style={styles.searchIcon} />
      </div>

      {/* Botón para abrir formulario de agregar */}
      {!showForm && (
        <button
          className="btn btn-primary"
          onClick={() => setShowForm(true)}
          style={styles.addButton}
        >
          <Plus size={18} /> Agregar Ingrediente
        </button>
      )}

      {/* Formulario / Panel para agregar ingrediente */}
      {showForm && (
        <div style={styles.formPanel}>
          <div style={styles.formHeader}>
            <strong style={styles.formHeaderTitle}>Añadir a Despensa</strong>
            <button
              onClick={() => setShowForm(false)}
              style={styles.closeFormButton}
            >
              <X size={18} />
            </button>
          </div>

          {errorMsg && (
            <div style={styles.errorBox}>
              <AlertCircle size={14} /> {errorMsg}
            </div>
          )}

          <form onSubmit={handleGuardarIngrediente} style={styles.form}>
            <div>
              <label style={styles.fieldLabel}>Ingrediente</label>
              <CustomSelect
                value={creandoNuevo ? '__nuevo__' : String(selectedIngredienteId)}
                onChange={(val) => {
                  if (val === '__nuevo__') {
                    setCreandoNuevo(true);
                    setSelectedIngredienteId('');
                    return;
                  }
                  if (val === '') {
                    setCreandoNuevo(false);
                    setSelectedIngredienteId('');
                    return;
                  }
                  setCreandoNuevo(false);
                  setSelectedIngredienteId(val);
                  const sel = catalogo.find(c => String(c.id) === String(val));
                  if (sel) setUnidad(sel.unidadBase);
                }}
                options={[
                  { value: '', label: 'Selecciona del catálogo...' },
                  { value: '__nuevo__', label: '+ Agregar ingrediente propio...', isDivider: true },
                  ...catalogo.map(ing => ({
                    value: String(ing.id),
                    label: (
                      <>
                        <span style={styles.emojiOptionText}>{getIngredienteEmoji(ing.nombre)}</span>
                        <span>{ing.nombre}</span>
                      </>
                    )
                  }))
                ]}
              />
            </div>

            {creandoNuevo && (
              <div style={styles.inlineRow}>
                <div style={styles.flexFieldWide}>
                  <label style={styles.fieldLabel}>Nombre nuevo ingrediente</label>
                  <input
                    type="text"
                    placeholder="Ej. Palta"
                    value={nuevoNombre}
                    onChange={(e) => setNuevoNombre(e.target.value)}
                    required
                    className="pastel-input"
                  />
                </div>
                <div style={styles.flexField}>
                  <label style={styles.fieldLabel}>Categoría</label>
                  <CustomSelect
                    value={nuevoCategoria}
                    onChange={setNuevoCategoria}
                    options={[
                      { value: '', label: 'Selecciona...', isDivider: true },
                      ...categoriasExistentes.map(cat => ({ value: cat, label: cat }))
                    ]}
                  />
                </div>
              </div>
            )}

            <div style={styles.inlineRow}>
              <div style={styles.flexField}>
                <label style={styles.fieldLabel}>Cantidad</label>
                <input
                  type="number"
                  step="0.1"
                  min="0.1"
                  value={cantidad}
                  onChange={(e) => setCantidad(e.target.value)}
                  required
                  className="pastel-input"
                />
              </div>

              <div style={styles.flexFieldMedium}>
                <label style={styles.fieldLabel}>Unidad</label>
                <CustomSelect
                  value={unidad}
                  onChange={setUnidad}
                  options={UNIDADES_DISPONIBLES}
                />
              </div>
            </div>

            <div style={styles.formActionsRow}>
              <button
                type="submit"
                disabled={submitting}
                className="btn btn-primary"
                style={styles.submitButton}
              >
                {submitting ? <Loader2 size={16} className="spin" /> : 'Guardar'}
              </button>
              <button
                type="button"
                onClick={() => {
                  setShowForm(false);
                  setCreandoNuevo(false);
                  setNuevoNombre('');
                  setNuevoCategoria('');
                }}
                className="btn btn-outline"
                style={styles.cancelButton}
              >
                Cancelar
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Lista de ingredientes registrados en la despensa */}
      <div style={styles.listWrapper}>
        {loading ? (
          <div style={styles.loadingRow}>
            <Loader2 size={18} className="spin" /> Cargando despensa...
          </div>
        ) : ingredientesFiltrados.length === 0 ? (
          <div style={styles.emptyListBox}>
            {searchTerm ? 'No se encontraron ingredientes con ese nombre.' : 'Tu despensa está vacía. ¡Agrega tus primeros ingredientes arriba!'}
          </div>
        ) : (
          ingredientesFiltrados.map((item) => (
            <div
              key={item.id}
              style={{
                ...styles.itemRow,
                zIndex: editingId === item.id ? 20 : 1,
                position: editingId === item.id ? 'relative' : 'static'
              }}
            >
              {/* cambios aqui */}
              {editingId === item.id ? (
                <div style={styles.editColumn}>
                  <div style={styles.editNameRow}>
                    <span>{getIngredienteEmoji(item.nombreIngrediente)}</span>
                    <span>{item.nombreIngrediente}</span>
                  </div>
                  <div style={styles.editControlsRow}>
                    <input
                      type="number"
                      step="0.1"
                      min="0.1"
                      value={editCantidad}
                      onChange={(e) => setEditCantidad(e.target.value)}
                      className="pastel-input"
                      style={styles.editQuantityInput}
                    />
                    <div style={styles.editUnitWrapper}>
                      <CustomSelect
                        value={editUnidad}
                        onChange={setEditUnidad}
                        options={UNIDADES_DISPONIBLES}
                      />
                    </div>
                    <button
                      onClick={() => handleGuardarEdicion(item.id)}
                      disabled={savingEdit}
                      title="Guardar cambios"
                      style={styles.roundIconButton('var(--primary-dark)', 'white')}
                    >
                      {savingEdit ? <Loader2 size={14} className="spin" /> : <Check size={14} />}
                    </button>
                    <button
                      onClick={() => setEditingId(null)}
                      title="Cancelar"
                      style={styles.roundIconButton('#E5E7EB', '#4B5563')}
                    >
                      <X size={14} />
                    </button>
                  </div>
                </div>
              ) : (
                <>
                  <div style={styles.itemViewRow}>
                    <span style={styles.itemEmoji}>{getIngredienteEmoji(item.nombreIngrediente)}</span>
                    <div>
                      <div style={styles.itemName}>
                        {item.nombreIngrediente}
                      </div>
                      <div style={styles.itemQuantity}>
                        {item.cantidad} {item.unidad.toLowerCase()}
                      </div>
                    </div>
                  </div>

                  <div style={styles.itemActionsRow}>
                    <button
                      onClick={() => handleIniciarEdicion(item)}
                      title="Editar ingrediente"
                      style={styles.editIconButton}
                    >
                      <Edit2 size={14} color="var(--text-main)" />
                    </button>
                    <button
                      onClick={() => handleEliminar(item.id)}
                      title="Eliminar de la despensa"
                      style={styles.deleteIconButton}
                    >
                      <Trash2 size={15} />
                    </button>
                  </div>
                </>
              )}
              {/* hasta aqui */}
            </div>
          ))
        )}
      </div>

      <div style={styles.footerCount}>
        {despensa.length} ingrediente{despensa.length === 1 ? '' : 's'} disponible{despensa.length === 1 ? '' : 's'}
      </div>
    </aside>
  );
}
