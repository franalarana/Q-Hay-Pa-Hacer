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
                {catalogo.map(ing => (
                  <option key={ing.id} value={ing.id}>
                    {ing.nombre}
                  </option>
                ))}
                <option value="__nuevo__">+ Agregar ingrediente propio...</option>
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
                  <input
                    type="text"
                    placeholder="Ej. Verduras"
                    value={nuevoCategoria}
                    onChange={(e) => setNuevoCategoria(e.target.value)}
                    required
                    style={{ width: '100%', padding: '8px', borderRadius: 'var(--border-radius-sm)', border: '1px solid #D1D5DB', fontSize: '0.85rem' }}
                  />
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
              <div>
                <div style={{ fontWeight: '600', fontSize: '0.9rem', color: 'var(--text-main)' }}>
                  {item.nombreIngrediente}
                </div>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                  {item.cantidad} {item.unidad.toLowerCase()}
                </div>
              </div>

              <div style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                <button 
                  onClick={() => handleCambiarCantidad(item, -1)}
                  title="Restar 1"
                  style={{
                    width: '24px', height: '24px', borderRadius: '50%', border: 'none',
                    backgroundColor: 'rgba(0,0,0,0.06)', cursor: 'pointer', display: 'flex',
                    alignItems: 'center', justifyContent: 'center', fontWeight: 'bold'
                  }}
                >
                  -
                </button>
                <button 
                  onClick={() => handleCambiarCantidad(item, 1)}
                  title="Sumar 1"
                  style={{
                    width: '24px', height: '24px', borderRadius: '50%', border: 'none',
                    backgroundColor: 'rgba(0,0,0,0.06)', cursor: 'pointer', display: 'flex',
                    alignItems: 'center', justifyContent: 'center', fontWeight: 'bold'
                  }}
                >
                  +
                </button>
                <button 
                  onClick={() => handleEliminar(item.id)}
                  title="Eliminar de la despensa"
                  style={{
                    background: 'none', border: 'none', cursor: 'pointer',
                    color: '#DC2626', padding: '4px', marginLeft: '4px', opacity: 0.7
                  }}
                >
                  <Trash2 size={16} />
                </button>
              </div>
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
