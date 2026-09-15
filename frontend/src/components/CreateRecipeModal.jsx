import { useState, useEffect } from 'react';
import { X, Plus, Trash2, Loader2, Sparkles, AlertCircle } from 'lucide-react';
import axiosClient from '../api/axiosClient';
// cambios aqui
import { getIngredienteEmoji } from './Sidebar';
// hasta aqui
import * as styles from '../styles/createRecipeModal.styles';

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
    <div style={styles.overlay}>
      <div style={styles.panel}>
        <div style={styles.header}>
          <div style={styles.headerTitleGroup}>
            <Sparkles size={24} color="var(--primary-dark)" />
            <h2 style={styles.headerTitle}>{esEdicion ? 'Editar Receta' : 'Crear Nueva Receta'}</h2>
          </div>
          <button onClick={onClose} style={styles.closeButton}>
            <X size={22} />
          </button>
        </div>

        {errorMsg && (
          <div style={styles.errorBox}>
            <AlertCircle size={16} /> {errorMsg}
          </div>
        )}

        <form onSubmit={handleSubmit} style={styles.form}>
          <div>
            <label style={styles.fieldLabel}>
              Título de la Receta *
            </label>
            <input
              type="text"
              placeholder="Ej. Tortilla de Acelga y Queso"
              value={titulo}
              onChange={(e) => setTitulo(e.target.value)}
              required
              style={styles.textInput}
            />
          </div>

          <div>
            <label style={styles.fieldLabel}>
              Descripción breve
            </label>
            <input
              type="text"
              placeholder="Ej. Una preparación ligera y deliciosa para cualquier hora."
              value={descripcion}
              onChange={(e) => setDescripcion(e.target.value)}
              style={styles.textInput}
            />
          </div>

          <div style={styles.threeColGrid}>
            <div>
              <label style={styles.fieldLabel}>
                Tiempo (min)
              </label>
              <input
                type="number"
                min="1"
                value={tiempoMinutos}
                onChange={(e) => setTiempoMinutos(e.target.value)}
                style={styles.smallInput}
              />
            </div>
            <div>
              <label style={styles.fieldLabel}>
                Porciones
              </label>
              <input
                type="number"
                min="1"
                value={porciones}
                onChange={(e) => setPorciones(e.target.value)}
                style={styles.smallInput}
              />
            </div>
            <div>
              <label style={styles.fieldLabel}>
                Dificultad
              </label>
              <select
                value={dificultad}
                onChange={(e) => setDificultad(e.target.value)}
                style={styles.smallInput}
              >
                <option value="Fácil">Fácil</option>
                <option value="Media">Media</option>
                <option value="Difícil">Difícil</option>
              </select>
            </div>
          </div>

          <div>
            <label style={styles.fieldLabel}>
              URL de imagen (opcional)
            </label>
            <input
              type="url"
              placeholder="https://..."
              value={imagenUrl}
              onChange={(e) => setImagenUrl(e.target.value)}
              style={styles.textInput}
            />
          </div>

          {/* Lista de ingredientes */}
          <div>
            <div style={styles.ingredientesHeaderRow}>
              <label style={styles.ingredientesHeaderLabel}>
                Ingredientes requeridos *
              </label>
              <button
                type="button"
                onClick={handleAddIngredienteRow}
                className="btn btn-outline"
                style={styles.addIngredienteButton}
              >
                <Plus size={14} /> Añadir ingrediente
              </button>
            </div>

            <div style={styles.ingredientesList}>
              {ingredientes.map((row, index) => (
                <div key={index} style={styles.ingredienteRow}>
                  <select
                    value={row.ingredienteId}
                    onChange={(e) => handleIngredienteChange(index, 'ingredienteId', e.target.value)}
                    required
                    style={styles.ingredienteSelect}
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
                    style={styles.ingredienteCantidadInput}
                  />

                  <select
                    value={row.unidad}
                    onChange={(e) => handleIngredienteChange(index, 'unidad', e.target.value)}
                    style={styles.ingredienteUnidadSelect}
                  >
                    {UNIDADES_DISPONIBLES.map(u => (
                      <option key={u.value} value={u.value}>{u.label}</option>
                    ))}
                  </select>

                  {ingredientes.length > 1 && (
                    <button
                      type="button"
                      onClick={() => handleRemoveIngredienteRow(index)}
                      style={styles.removeIngredienteButton}
                    >
                      <Trash2 size={16} />
                    </button>
                  )}
                </div>
              ))}
            </div>
          </div>

          <div>
            <label style={styles.fieldLabel}>
              Instrucciones paso a paso *
            </label>
            <textarea
              rows={4}
              placeholder="1. Pica las verduras...&#10;2. Calienta el sartén...&#10;3. Sirve caliente."
              value={instrucciones}
              onChange={(e) => setInstrucciones(e.target.value)}
              required
              style={styles.textarea}
            />
          </div>

          <div style={styles.formActionsRow}>
            <button
              type="submit"
              disabled={submitting}
              className="btn btn-primary"
              style={styles.submitButton}
            >
              {submitting ? <Loader2 size={18} className="spin" /> : (esEdicion ? 'Guardar Cambios' : 'Guardar y Publicar Receta')}
            </button>
            <button
              type="button"
              onClick={onClose}
              className="btn btn-outline"
              style={styles.cancelButton}
            >
              Cancelar
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
