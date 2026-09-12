import { useState, useEffect } from 'react';
import { X, Plus, Trash2, Loader2, Sparkles, AlertCircle } from 'lucide-react';
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

export default function CreateRecipeModal({ onClose, onRecipeCreated }) {
  const [catalogo, setCatalogo] = useState([]);
  const [loadingCatalogo, setLoadingCatalogo] = useState(true);

  const [titulo, setTitulo] = useState('');
  const [descripcion, setDescripcion] = useState('');
  const [instrucciones, setInstrucciones] = useState('');
  const [tiempoMinutos, setTiempoMinutos] = useState(20);
  const [porciones, setPorciones] = useState(2);
  const [dificultad, setDificultad] = useState('Fácil');
  const [imagenUrl, setImagenUrl] = useState('');
  
  const [ingredientes, setIngredientes] = useState([
    { ingredienteId: '', cantidadRequerida: 1, unidad: 'UNIDAD', opcional: false }
  ]);

  const [submitting, setSubmitting] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');

  useEffect(() => {
    axiosClient.get('/ingredientes')
      .then(res => setCatalogo(res.data))
      .catch(err => console.error(err))
      .finally(() => setLoadingCatalogo(false));
  }, []);

  const handleAddIngredienteRow = () => {
    setIngredientes([...ingredientes, { ingredienteId: '', cantidadRequerida: 1, unidad: 'UNIDAD', opcional: false }]);
  };

  const handleRemoveIngredienteRow = (index) => {
    if (ingredientes.length > 1) {
      setIngredientes(ingredientes.filter((_, i) => i !== index));
    }
  };

  const handleIngredienteChange = (index, field, value) => {
    const updated = [...ingredientes];
    updated[index][field] = value;
    if (field === 'ingredienteId') {
      const sel = catalogo.find(c => c.id === Number(value));
      if (sel) updated[index].unidad = sel.unidadBase;
    }
    setIngredientes(updated);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!titulo.trim()) {
      setErrorMsg('El título es obligatorio.');
      return;
    }
    if (!instrucciones.trim()) {
      setErrorMsg('Las instrucciones son obligatorias.');
      return;
    }
    const invalidIng = ingredientes.some(i => !i.ingredienteId || i.cantidadRequerida <= 0);
    if (invalidIng) {
      setErrorMsg('Verifica que todos los ingredientes seleccionados tengan cantidad válida.');
      return;
    }

    try {
      setSubmitting(true);
      setErrorMsg('');
      await axiosClient.post('/recetas', {
        titulo,
        descripcion,
        instrucciones,
        tiempoMinutos: Number(tiempoMinutos),
        porciones: Number(porciones),
        dificultad,
        imagenUrl: imagenUrl.trim() || undefined,
        ingredientes: ingredientes.map(i => ({
          ingredienteId: Number(i.ingredienteId),
          cantidadRequerida: Number(i.cantidadRequerida),
          unidad: i.unidad,
          opcional: i.opcional
        }))
      });

      if (onRecipeCreated) {
        onRecipeCreated();
      }
      onClose();
    } catch (err) {
      setErrorMsg(err.response?.data?.message || 'Error al guardar tu receta.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div style={{
      position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
      backgroundColor: 'rgba(0, 0, 0, 0.5)',
      backdropFilter: 'blur(4px)',
      display: 'flex', alignItems: 'center', justifyContent: 'center',
      zIndex: 1000, padding: '20px'
    }}>
      <div style={{
        backgroundColor: 'white',
        borderRadius: 'var(--border-radius-lg)',
        maxWidth: '680px',
        width: '100%',
        maxHeight: '90vh',
        overflowY: 'auto',
        padding: '32px',
        boxShadow: 'var(--shadow-md)',
        position: 'relative'
      }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <Sparkles size={24} color="var(--primary-dark)" />
            <h2 style={{ fontSize: '1.5rem', margin: 0, color: 'var(--text-main)' }}>Crear Nueva Receta</h2>
          </div>
          <button onClick={onClose} style={{ background: 'none', border: 'none', cursor: 'pointer', color: '#666' }}>
            <X size={22} />
          </button>
        </div>

        {errorMsg && (
          <div style={{ display: 'flex', alignItems: 'center', gap: '6px', color: '#EF4444', backgroundColor: '#FEE2E2', padding: '10px 14px', borderRadius: 'var(--border-radius-sm)', marginBottom: '16px', fontSize: '0.85rem' }}>
            <AlertCircle size={16} /> {errorMsg}
          </div>
        )}

        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <div>
            <label style={{ fontSize: '0.85rem', fontWeight: '600', color: 'var(--text-main)', display: 'block', marginBottom: '6px' }}>
              Título de la Receta *
            </label>
            <input 
              type="text" 
              placeholder="Ej. Tortilla de Acelga y Queso"
              value={titulo}
              onChange={(e) => setTitulo(e.target.value)}
              required
              style={{ width: '100%', padding: '10px 12px', borderRadius: 'var(--border-radius-sm)', border: '1px solid #D1D5DB', fontSize: '0.9rem' }}
            />
          </div>

          <div>
            <label style={{ fontSize: '0.85rem', fontWeight: '600', color: 'var(--text-main)', display: 'block', marginBottom: '6px' }}>
              Descripción breve
            </label>
            <input 
              type="text" 
              placeholder="Ej. Una preparación ligera y deliciosa para cualquier hora."
              value={descripcion}
              onChange={(e) => setDescripcion(e.target.value)}
              style={{ width: '100%', padding: '10px 12px', borderRadius: 'var(--border-radius-sm)', border: '1px solid #D1D5DB', fontSize: '0.9rem' }}
            />
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '12px' }}>
            <div>
              <label style={{ fontSize: '0.85rem', fontWeight: '600', color: 'var(--text-main)', display: 'block', marginBottom: '6px' }}>
                Tiempo (min)
              </label>
              <input 
                type="number" 
                min="1"
                value={tiempoMinutos}
                onChange={(e) => setTiempoMinutos(e.target.value)}
                style={{ width: '100%', padding: '8px 12px', borderRadius: 'var(--border-radius-sm)', border: '1px solid #D1D5DB', fontSize: '0.9rem' }}
              />
            </div>
            <div>
              <label style={{ fontSize: '0.85rem', fontWeight: '600', color: 'var(--text-main)', display: 'block', marginBottom: '6px' }}>
                Porciones
              </label>
              <input 
                type="number" 
                min="1"
                value={porciones}
                onChange={(e) => setPorciones(e.target.value)}
                style={{ width: '100%', padding: '8px 12px', borderRadius: 'var(--border-radius-sm)', border: '1px solid #D1D5DB', fontSize: '0.9rem' }}
              />
            </div>
            <div>
              <label style={{ fontSize: '0.85rem', fontWeight: '600', color: 'var(--text-main)', display: 'block', marginBottom: '6px' }}>
                Dificultad
              </label>
              <select 
                value={dificultad}
                onChange={(e) => setDificultad(e.target.value)}
                style={{ width: '100%', padding: '8px 12px', borderRadius: 'var(--border-radius-sm)', border: '1px solid #D1D5DB', fontSize: '0.9rem' }}
              >
                <option value="Fácil">Fácil</option>
                <option value="Media">Media</option>
                <option value="Difícil">Difícil</option>
              </select>
            </div>
          </div>

          <div>
            <label style={{ fontSize: '0.85rem', fontWeight: '600', color: 'var(--text-main)', display: 'block', marginBottom: '6px' }}>
              URL de imagen (opcional)
            </label>
            <input 
              type="url" 
              placeholder="https://..."
              value={imagenUrl}
              onChange={(e) => setImagenUrl(e.target.value)}
              style={{ width: '100%', padding: '10px 12px', borderRadius: 'var(--border-radius-sm)', border: '1px solid #D1D5DB', fontSize: '0.9rem' }}
            />
          </div>

          {/* Lista de ingredientes */}
          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
              <label style={{ fontSize: '0.85rem', fontWeight: '600', color: 'var(--text-main)' }}>
                Ingredientes requeridos *
              </label>
              <button 
                type="button" 
                onClick={handleAddIngredienteRow}
                className="btn btn-outline"
                style={{ padding: '4px 10px', fontSize: '0.75rem', gap: '4px' }}
              >
                <Plus size={14} /> Añadir ingrediente
              </button>
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
              {ingredientes.map((row, index) => (
                <div key={index} style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
                  <select 
                    value={row.ingredienteId}
                    onChange={(e) => handleIngredienteChange(index, 'ingredienteId', e.target.value)}
                    required
                    style={{ flex: 2, padding: '8px', borderRadius: 'var(--border-radius-sm)', border: '1px solid #D1D5DB', fontSize: '0.85rem' }}
                  >
                    <option value="">Selecciona ingrediente...</option>
                    {catalogo.map(c => (
                      <option key={c.id} value={c.id}>{c.nombre} ({c.categoria})</option>
                    ))}
                  </select>

                  <input 
                    type="number" 
                    step="0.1" 
                    min="0.1"
                    placeholder="Cant."
                    value={row.cantidadRequerida}
                    onChange={(e) => handleIngredienteChange(index, 'cantidadRequerida', e.target.value)}
                    required
                    style={{ flex: 1, padding: '8px', borderRadius: 'var(--border-radius-sm)', border: '1px solid #D1D5DB', fontSize: '0.85rem' }}
                  />

                  <select 
                    value={row.unidad}
                    onChange={(e) => handleIngredienteChange(index, 'unidad', e.target.value)}
                    style={{ flex: 1.5, padding: '8px', borderRadius: 'var(--border-radius-sm)', border: '1px solid #D1D5DB', fontSize: '0.85rem' }}
                  >
                    {UNIDADES_DISPONIBLES.map(u => (
                      <option key={u.value} value={u.value}>{u.label}</option>
                    ))}
                  </select>

                  {ingredientes.length > 1 && (
                    <button 
                      type="button" 
                      onClick={() => handleRemoveIngredienteRow(index)}
                      style={{ background: 'none', border: 'none', color: '#EF4444', cursor: 'pointer', padding: '4px' }}
                    >
                      <Trash2 size={16} />
                    </button>
                  )}
                </div>
              ))}
            </div>
          </div>

          <div>
            <label style={{ fontSize: '0.85rem', fontWeight: '600', color: 'var(--text-main)', display: 'block', marginBottom: '6px' }}>
              Instrucciones paso a paso *
            </label>
            <textarea 
              rows={4}
              placeholder="1. Pica las verduras...&#10;2. Calienta el sartén...&#10;3. Sirve caliente."
              value={instrucciones}
              onChange={(e) => setInstrucciones(e.target.value)}
              required
              style={{ width: '100%', padding: '10px 12px', borderRadius: 'var(--border-radius-sm)', border: '1px solid #D1D5DB', fontSize: '0.9rem', fontFamily: 'inherit', resize: 'vertical' }}
            />
          </div>

          <div style={{ display: 'flex', gap: '12px', marginTop: '12px' }}>
            <button 
              type="submit" 
              disabled={submitting} 
              className="btn btn-primary"
              style={{ flex: 1, padding: '12px' }}
            >
              {submitting ? <Loader2 size={18} className="spin" /> : 'Guardar y Publicar Receta'}
            </button>
            <button 
              type="button" 
              onClick={onClose} 
              className="btn btn-outline"
              style={{ padding: '12px 20px' }}
            >
              Cancelar
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
