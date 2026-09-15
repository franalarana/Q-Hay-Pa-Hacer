import { useState, useEffect } from 'react';
import { X, Plus, Trash2, Loader2, Sparkles, AlertCircle } from 'lucide-react';
import axiosClient from '../api/axiosClient';
// cambios aqui
import { getIngredienteEmoji } from './Sidebar';
// hasta aqui
import '../styles/createRecipeModal.css';

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

export default function CreateRecipeModal({ onClose, onRecipeCreated, recetaEditar }) {
  const esEdicion = Boolean(recetaEditar);
  const [catalogo, setCatalogo] = useState([]);
  const [loadingCatalogo, setLoadingCatalogo] = useState(true);

  const [titulo, setTitulo] = useState(recetaEditar?.titulo || '');
  const [descripcion, setDescripcion] = useState(recetaEditar?.descripcion || '');
  const [instrucciones, setInstrucciones] = useState(recetaEditar?.instrucciones || '');
  const [tiempoMinutos, setTiempoMinutos] = useState(recetaEditar?.tiempoMinutos || 20);
  const [porciones, setPorciones] = useState(recetaEditar?.porciones || 2);
  const [dificultad, setDificultad] = useState(recetaEditar?.dificultad || 'Fácil');
  const [imagenUrl, setImagenUrl] = useState(recetaEditar?.imagenUrl || '');

  const [ingredientes, setIngredientes] = useState(
    recetaEditar?.ingredientes?.length
      ? recetaEditar.ingredientes.map(i => ({
          ingredienteId: String(i.ingredienteId),
          cantidadRequerida: i.cantidadRequerida,
          unidad: i.unidadRequerida,
          opcional: i.opcional || false
        }))
      : [{ ingredienteId: '', cantidadRequerida: 1, unidad: 'UNIDAD', opcional: false }]
  );

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
      const payload = {
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
      };

      if (esEdicion) {
        await axiosClient.put(`/recetas/${recetaEditar.id}`, payload);
      } else {
        await axiosClient.post('/recetas', payload);
      }

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
    <div className="create-overlay">
      <div className="create-panel">
        <div className="create-header">
          <div className="create-header-title-group">
            <Sparkles size={24} color="var(--primary-dark)" />
            <h2 className="create-header-title">{esEdicion ? 'Editar Receta' : 'Crear Nueva Receta'}</h2>
          </div>
          <button onClick={onClose} className="create-close-button">
            <X size={22} />
          </button>
        </div>

        {errorMsg && (
          <div className="create-error-box">
            <AlertCircle size={16} /> {errorMsg}
          </div>
        )}

        <form onSubmit={handleSubmit} className="create-form">
          <div>
            <label className="create-field-label">
              Título de la Receta *
            </label>
            <input
              type="text"
              placeholder="Ej. Tortilla de Acelga y Queso"
              value={titulo}
              onChange={(e) => setTitulo(e.target.value)}
              required
              className="create-text-input"
            />
          </div>

          <div>
            <label className="create-field-label">
              Descripción breve
            </label>
            <input
              type="text"
              placeholder="Ej. Una preparación ligera y deliciosa para cualquier hora."
              value={descripcion}
              onChange={(e) => setDescripcion(e.target.value)}
              className="create-text-input"
            />
          </div>

          <div className="create-three-col-grid">
            <div>
              <label className="create-field-label">
                Tiempo (min)
              </label>
              <input
                type="number"
                min="1"
                value={tiempoMinutos}
                onChange={(e) => setTiempoMinutos(e.target.value)}
                className="create-small-input"
              />
            </div>
            <div>
              <label className="create-field-label">
                Porciones
              </label>
              <input
                type="number"
                min="1"
                value={porciones}
                onChange={(e) => setPorciones(e.target.value)}
                className="create-small-input"
              />
            </div>
            <div>
              <label className="create-field-label">
                Dificultad
              </label>
              <select
                value={dificultad}
                onChange={(e) => setDificultad(e.target.value)}
                className="create-small-input"
              >
                <option value="Fácil">Fácil</option>
                <option value="Media">Media</option>
                <option value="Difícil">Difícil</option>
              </select>
            </div>
          </div>

          <div>
            <label className="create-field-label">
              URL de imagen (opcional)
            </label>
            <input
              type="url"
              placeholder="https://..."
              value={imagenUrl}
              onChange={(e) => setImagenUrl(e.target.value)}
              className="create-text-input"
            />
          </div>

          {/* Lista de ingredientes */}
          <div>
            <div className="create-ingredientes-header-row">
              <label className="create-ingredientes-header-label">
                Ingredientes requeridos *
              </label>
              <button
                type="button"
                onClick={handleAddIngredienteRow}
                className="btn btn-outline create-add-ingrediente-button"
              >
                <Plus size={14} /> Añadir ingrediente
              </button>
            </div>

            <div className="create-ingredientes-list">
              {ingredientes.map((row, index) => (
                <div key={index} className="create-ingrediente-row">
                  <select
                    value={row.ingredienteId}
                    onChange={(e) => handleIngredienteChange(index, 'ingredienteId', e.target.value)}
                    required
                    className="create-ingrediente-select"
                  >
                    <option value="">Selecciona ingrediente...</option>
                    {/* cambios aqui */}
                    {catalogo.map(c => (
                      <option key={c.id} value={c.id}>{getIngredienteEmoji(c.nombre)} {c.nombre}</option>
                    ))}
                    {/* hasta aqui */}
                  </select>

                  <input
                    type="number"
                    step="0.1"
                    min="0.1"
                    placeholder="Cant."
                    value={row.cantidadRequerida}
                    onChange={(e) => handleIngredienteChange(index, 'cantidadRequerida', e.target.value)}
                    required
                    className="create-ingrediente-cantidad-input"
                  />

                  <select
                    value={row.unidad}
                    onChange={(e) => handleIngredienteChange(index, 'unidad', e.target.value)}
                    className="create-ingrediente-unidad-select"
                  >
                    {UNIDADES_DISPONIBLES.map(u => (
                      <option key={u.value} value={u.value}>{u.label}</option>
                    ))}
                  </select>

                  {ingredientes.length > 1 && (
                    <button
                      type="button"
                      onClick={() => handleRemoveIngredienteRow(index)}
                      className="create-remove-ingrediente-button"
                    >
                      <Trash2 size={16} />
                    </button>
                  )}
                </div>
              ))}
            </div>
          </div>

          <div>
            <label className="create-field-label">
              Instrucciones paso a paso *
            </label>
            <textarea
              rows={4}
              placeholder="1. Pica las verduras...&#10;2. Calienta el sartén...&#10;3. Sirve caliente."
              value={instrucciones}
              onChange={(e) => setInstrucciones(e.target.value)}
              required
              className="create-textarea"
            />
          </div>

          <div className="create-form-actions-row">
            <button
              type="submit"
              disabled={submitting}
              className="btn btn-primary create-submit-button"
            >
              {submitting ? <Loader2 size={18} className="spin" /> : (esEdicion ? 'Guardar Cambios' : 'Guardar y Publicar Receta')}
            </button>
            <button
              type="button"
              onClick={onClose}
              className="btn btn-outline create-cancel-button"
            >
              Cancelar
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
