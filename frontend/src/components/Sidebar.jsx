import { useState, useEffect } from 'react';
import { Search, Plus, Trash2, Edit2, Check, X, AlertCircle, Loader2 } from 'lucide-react';
import axiosClient from '../api/axiosClient';

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
    <aside className="sidebar">
      <h2>Tu Despensa</h2>
      <p className="subtitle">(Ingredientes que tienes en casa)</p>

      {/* Buscador dentro de tu despensa */}
      <div style={{ position: 'relative', marginBottom: '16px' }}>
        <input 
          type="text" 
          placeholder="Buscar en tu despensa..." 
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          style={{
            width: '100%',
            padding: '10px 38px 10px 14px',
            borderRadius: '50px',
            border: 'none',
            outline: 'none',
            fontSize: '0.9rem',
            boxShadow: 'inset 0 1px 3px rgba(0,0,0,0.08)'
          }}
        />
        <Search size={18} color="#666" style={{ position: 'absolute', right: '14px', top: '10px' }} />
      </div>

      {/* Botón para abrir formulario de agregar */}
      {!showForm && (
        <button 
          className="btn btn-primary" 
          onClick={() => setShowForm(true)}
          style={{ width: '100%', marginBottom: '20px', backgroundColor: 'white', color: 'var(--primary-dark)', gap: '6px' }}
        >
          <Plus size={18} /> Agregar Ingrediente
        </button>
      )}

      {/* Formulario / Panel para agregar ingrediente */}
      {showForm && (
        <div style={{
          backgroundColor: 'white',
          padding: '16px',
          borderRadius: 'var(--border-radius-md)',
          marginBottom: '20px',
          boxShadow: 'var(--shadow-sm)'
        }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
            <strong style={{ fontSize: '0.95rem', color: 'var(--text-main)' }}>Añadir a Despensa</strong>
            <button 
              onClick={() => setShowForm(false)} 
              style={{ background: 'none', border: 'none', cursor: 'pointer', color: '#888' }}
            >
              <X size={18} />
            </button>
          </div>

          {errorMsg && (
            <div style={{ display: 'flex', alignItems: 'center', gap: '6px', color: '#EF4444', fontSize: '0.8rem', marginBottom: '10px' }}>
              <AlertCircle size={14} /> {errorMsg}
            </div>
          )}

          <form onSubmit={handleGuardarIngrediente} style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
            <div>
              <label style={{ fontSize: '0.75rem', fontWeight: '600', color: 'var(--text-muted)' }}>Ingrediente</label>
              <select
                value={creandoNuevo ? '__nuevo__' : selectedIngredienteId}
                onChange={(e) => {
                  if (e.target.value === '__nuevo__') {
                    setCreandoNuevo(true);
                    setSelectedIngredienteId('');
                    return;
                  }
                  setCreandoNuevo(false);
                  setSelectedIngredienteId(e.target.value);
                  const sel = catalogo.find(c => c.id === Number(e.target.value));
                  if (sel) setUnidad(sel.unidadBase);
                }}
                required
                style={{
                  width: '100%',
                  padding: '8px',
                  borderRadius: 'var(--border-radius-sm)',
                  border: '1px solid #D1D5DB',
                  fontSize: '0.85rem'
                }}
              >
                <option value="">Selecciona del catálogo...</option>
                <option value="__nuevo__">+ Agregar ingrediente propio...</option>
                {/* cambios aqui */}
                {catalogo.map(ing => (
                  <option key={ing.id} value={ing.id}>
                    {getIngredienteEmoji(ing.nombre)} {ing.nombre}
                  </option>
                ))}
                {/* hasta aqui */}
              </select>
            </div>

            {creandoNuevo && (
              <div style={{ display: 'flex', gap: '8px' }}>
                <div style={{ flex: 1.4 }}>
                  <label style={{ fontSize: '0.75rem', fontWeight: '600', color: 'var(--text-muted)' }}>Nombre nuevo ingrediente</label>
                  <input
                    type="text"
                    placeholder="Ej. Palta"
                    value={nuevoNombre}
                    onChange={(e) => setNuevoNombre(e.target.value)}
                    required
                    style={{ width: '100%', padding: '8px', borderRadius: 'var(--border-radius-sm)', border: '1px solid #D1D5DB', fontSize: '0.85rem' }}
                  />
                </div>
                <div style={{ flex: 1 }}>
                  <label style={{ fontSize: '0.75rem', fontWeight: '600', color: 'var(--text-muted)' }}>Categoría</label>
                  <select
                    value={nuevoCategoria}
                    onChange={(e) => setNuevoCategoria(e.target.value)}
                    required
                    style={{ width: '100%', padding: '8px', borderRadius: 'var(--border-radius-sm)', border: '1px solid #D1D5DB', fontSize: '0.85rem' }}
                  >
                    <option value="">Selecciona...</option>
                    {categoriasExistentes.map(cat => (
                      <option key={cat} value={cat}>{cat}</option>
                    ))}
                  </select>
                </div>
              </div>
            )}

            <div style={{ display: 'flex', gap: '8px' }}>
              <div style={{ flex: 1 }}>
                <label style={{ fontSize: '0.75rem', fontWeight: '600', color: 'var(--text-muted)' }}>Cantidad</label>
                <input 
                  type="number" 
                  step="0.1" 
                  min="0.1"
                  value={cantidad} 
                  onChange={(e) => setCantidad(e.target.value)}
                  required
                  style={{
                    width: '100%',
                    padding: '8px',
                    borderRadius: 'var(--border-radius-sm)',
                    border: '1px solid #D1D5DB',
                    fontSize: '0.85rem'
                  }}
                />
              </div>

              <div style={{ flex: 1.5 }}>
                <label style={{ fontSize: '0.75rem', fontWeight: '600', color: 'var(--text-muted)' }}>Unidad</label>
                <select 
                  value={unidad}
                  onChange={(e) => setUnidad(e.target.value)}
                  style={{
                    width: '100%',
                    padding: '8px',
                    borderRadius: 'var(--border-radius-sm)',
                    border: '1px solid #D1D5DB',
                    fontSize: '0.85rem'
                  }}
                >
                  {UNIDADES_DISPONIBLES.map(u => (
                    <option key={u.value} value={u.value}>{u.label}</option>
                  ))}
                </select>
              </div>
            </div>

            <div style={{ display: 'flex', gap: '8px', marginTop: '6px' }}>
              <button 
                type="submit" 
                disabled={submitting}
                className="btn btn-primary" 
                style={{ flex: 1, padding: '8px', fontSize: '0.85rem' }}
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
                style={{ padding: '8px 12px', fontSize: '0.85rem' }}
              >
                Cancelar
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Lista de ingredientes registrados en la despensa */}
      <div style={{ flex: 1, overflowY: 'auto', display: 'flex', flexDirection: 'column', gap: '8px', paddingRight: '4px' }}>
        {loading ? (
          <div style={{ textAlign: 'center', padding: '24px 0', color: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px' }}>
            <Loader2 size={18} className="spin" /> Cargando despensa...
          </div>
        ) : ingredientesFiltrados.length === 0 ? (
          <div style={{
            backgroundColor: 'rgba(255,255,255,0.4)',
            borderRadius: 'var(--border-radius-md)',
            padding: '20px',
            textAlign: 'center',
            fontSize: '0.85rem',
            color: 'rgba(0,0,0,0.7)'
          }}>
            {searchTerm ? 'No se encontraron ingredientes con ese nombre.' : 'Tu despensa está vacía. ¡Agrega tus primeros ingredientes arriba!'}
          </div>
        ) : (
          ingredientesFiltrados.map((item) => (
            <div 
              key={item.id}
              style={{
                backgroundColor: 'rgba(255, 255, 255, 0.75)',
                backdropFilter: 'blur(4px)',
                borderRadius: 'var(--border-radius-sm)',
                padding: '10px 14px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                boxShadow: '0 2px 4px rgba(0,0,0,0.03)'
              }}
            >
              {/* cambios aqui */}
              {editingId === item.id ? (
                <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', width: '100%' }}>
                  <div style={{ fontWeight: '600', fontSize: '0.9rem', color: 'var(--text-main)', display: 'flex', alignItems: 'center', gap: '6px' }}>
                    <span>{getIngredienteEmoji(item.nombreIngrediente)}</span>
                    <span>{item.nombreIngrediente}</span>
                  </div>
                  <div style={{ display: 'flex', gap: '6px', alignItems: 'center' }}>
                    <input
                      type="number"
                      step="0.1"
                      min="0.1"
                      value={editCantidad}
                      onChange={(e) => setEditCantidad(e.target.value)}
                      style={{
                        width: '75px',
                        padding: '6px 8px',
                        borderRadius: 'var(--border-radius-sm)',
                        border: '1px solid #D1D5DB',
                        fontSize: '0.85rem'
                      }}
                    />
                    <select
                      value={editUnidad}
                      onChange={(e) => setEditUnidad(e.target.value)}
                      style={{
                        flex: 1,
                        padding: '6px 8px',
                        borderRadius: 'var(--border-radius-sm)',
                        border: '1px solid #D1D5DB',
                        fontSize: '0.8rem'
                      }}
                    >
                      {UNIDADES_DISPONIBLES.map(u => (
                        <option key={u.value} value={u.value}>{u.label}</option>
                      ))}
                    </select>
                    <button
                      onClick={() => handleGuardarEdicion(item.id)}
                      disabled={savingEdit}
                      title="Guardar cambios"
                      style={{
                        width: '28px', height: '28px', borderRadius: '50%', border: 'none',
                        backgroundColor: 'var(--primary-dark)', cursor: 'pointer', display: 'flex',
                        alignItems: 'center', justifyContent: 'center', color: 'white', flexShrink: 0
                      }}
                    >
                      {savingEdit ? <Loader2 size={14} className="spin" /> : <Check size={14} />}
                    </button>
                    <button
                      onClick={() => setEditingId(null)}
                      title="Cancelar"
                      style={{
                        width: '28px', height: '28px', borderRadius: '50%', border: 'none',
                        backgroundColor: '#E5E7EB', cursor: 'pointer', display: 'flex',
                        alignItems: 'center', justifyContent: 'center', color: '#4B5563', flexShrink: 0
                      }}
                    >
                      <X size={14} />
                    </button>
                  </div>
                </div>
              ) : (
                <>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <span style={{ fontSize: '1.25rem' }}>{getIngredienteEmoji(item.nombreIngrediente)}</span>
                    <div>
                      <div style={{ fontWeight: '600', fontSize: '0.9rem', color: 'var(--text-main)' }}>
                        {item.nombreIngrediente}
                      </div>
                      <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                        {item.cantidad} {item.unidad.toLowerCase()}
                      </div>
                    </div>
                  </div>

                  <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                    <button 
                      onClick={() => handleIniciarEdicion(item)}
                      title="Editar ingrediente"
                      style={{
                        width: '28px', height: '28px', borderRadius: '50%', border: 'none',
                        backgroundColor: 'rgba(0,0,0,0.06)', cursor: 'pointer', display: 'flex',
                        alignItems: 'center', justifyContent: 'center'
                      }}
                    >
                      <Edit2 size={14} color="var(--text-main)" />
                    </button>
                    <button 
                      onClick={() => handleEliminar(item.id)}
                      title="Eliminar de la despensa"
                      style={{
                        width: '28px', height: '28px', borderRadius: '50%', border: 'none',
                        backgroundColor: 'rgba(239, 68, 68, 0.08)', cursor: 'pointer', display: 'flex',
                        alignItems: 'center', justifyContent: 'center', color: '#DC2626'
                      }}
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

      <div style={{ marginTop: '16px', textAlign: 'center', fontSize: '0.75rem', color: 'rgba(0,0,0,0.6)' }}>
        {despensa.length} ingrediente{despensa.length === 1 ? '' : 's'} disponible{despensa.length === 1 ? '' : 's'}
      </div>
    </aside>
  );
}
