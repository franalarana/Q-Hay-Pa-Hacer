import { useState } from 'react';
import { X, Clock, Users, ChefHat, CheckCircle2, AlertTriangle, XCircle, Heart, Utensils, Loader2, Pencil, Trash2 } from 'lucide-react';
import axiosClient from '../api/axiosClient';
import SemaforoIcon from './SemaforoIcon';
// cambios aqui
import { getIngredienteEmoji } from './Sidebar';
// hasta aqui
import * as styles from '../styles/recipeDetailModal.styles';

export default function RecipeDetailModal({ receta, onClose, isFavorito, onToggleFavorito, onCookingDone, esPropia, onEditar, onEliminar }) {
  const [cooking, setCooking] = useState(false);
  const [cookedSuccess, setCookedSuccess] = useState(false);
  const [notas, setNotas] = useState('');
  const [showNotasInput, setShowNotasInput] = useState(false);

  if (!receta) return null;

  const handleCocinar = async () => {
    try {
      setCooking(true);
      await axiosClient.post(`/historial/${receta.id}`, { notas: notas.trim() || undefined });
      setCookedSuccess(true);
      if (onCookingDone) onCookingDone();
      setTimeout(() => {
        setCookedSuccess(false);
        setShowNotasInput(false);
      }, 3000);
    } catch (err) {
      console.error('Error al registrar en historial:', err);
    } finally {
      setCooking(false);
    }
  };

  return (
    <div style={styles.overlay}>
      <div style={styles.panel}>
        {/* Imagen y botones de acción superior */}
        <div style={styles.imageWrapper}>
          <img
            src={receta.imagenUrl || 'https://images.unsplash.com/photo-1495521821757-a1efb6729352?w=800&auto=format&fit=crop&q=60'}
            alt={receta.titulo}
            onError={(e) => {
              e.currentTarget.onerror = null;
              e.currentTarget.src = 'https://images.unsplash.com/photo-1495521821757-a1efb6729352?w=800&auto=format&fit=crop&q=60';
            }}
            style={styles.image}
          />

          {/* Botón Favorito */}
          <button
            onClick={() => onToggleFavorito && onToggleFavorito(receta.id)}
            title={isFavorito ? 'Quitar de favoritos' : 'Guardar en favoritos'}
            style={styles.roundTopButton('left')}
          >
            <Heart size={22} color={isFavorito ? '#EF4444' : '#666'} fill={isFavorito ? '#EF4444' : 'none'} />
          </button>

          {/* Botón Cerrar */}
          <button
            onClick={onClose}
            style={styles.roundTopButton('right')}
          >
            <X size={22} color="#333" />
          </button>
        </div>

        {/* Contenido del modal */}
        <div style={styles.content}>
          <div style={styles.titleRow}>
            <h2 style={styles.title}>
              {receta.titulo}
            </h2>
            <span style={styles.getEstadoBadge(receta.estadoGeneral)}>
              <SemaforoIcon estado={receta.estadoGeneral} size={10} />
              {receta.estadoGeneral === 'VERDE' ? 'Listo para cocinar' : receta.estadoGeneral === 'AMARILLO' ? 'Faltan cantidades' : 'Faltan ingredientes'}
            </span>
          </div>

          {esPropia && (
            <div style={styles.ownerActionsRow}>
              <button
                className="btn btn-outline"
                onClick={() => onEditar && onEditar(receta)}
                style={styles.editButton}
              >
                <Pencil size={15} /> Editar receta
              </button>
              <button
                className="btn btn-outline"
                onClick={() => onEliminar && onEliminar(receta)}
                style={styles.deleteButton}
              >
                <Trash2 size={15} /> Eliminar receta
              </button>
            </div>
          )}

          <p style={styles.description}>
            {receta.descripcion}
          </p>

          {/* Metadata chips */}
          <div style={styles.metadataRow}>
            <div style={styles.metadataItem}>
              <Clock size={18} color="var(--primary-dark)" /> {receta.tiempoMinutos} min
            </div>
            <div style={styles.metadataItem}>
              <Users size={18} color="var(--primary-dark)" /> {receta.porciones} porciones
            </div>
            <div style={styles.metadataItem}>
              <ChefHat size={18} color="var(--primary-dark)" /> Dificultad: {receta.dificultad}
            </div>
          </div>

          {/* Ingredientes con estado semáforo */}
          <div style={styles.sectionBlock}>
            <h3 style={styles.sectionTitle}>
              Ingredientes ({receta.ingredientesVerdes}/{receta.totalIngredientes} listos)
            </h3>
            <div style={styles.ingredientesList}>
              {receta.ingredientes.map((ing) => {
                const isVerde = ing.estado === 'VERDE';
                const isAmarillo = ing.estado === 'AMARILLO';
                return (
                  <div
                    key={ing.ingredienteId}
                    style={styles.getIngredienteRow(isVerde, isAmarillo)}
                  >
                    <div style={styles.ingredienteNameGroup}>
                      {isVerde && <CheckCircle2 size={18} color="#16A34A" />}
                      {isAmarillo && <AlertTriangle size={18} color="#CA8A04" />}
                      {!isVerde && !isAmarillo && <XCircle size={18} color="#DC2626" />}
                      {/* cambios aqui */}
                      <span style={styles.ingredienteName}>
                        {getIngredienteEmoji(ing.nombreIngrediente)} {ing.nombreIngrediente}
                      </span>
                      {/* hasta aqui */}
                    </div>

                    <div style={styles.ingredienteRightBlock}>
                      <div style={styles.ingredienteRequerido}>
                        Requiere: {ing.cantidadRequerida} {ing.unidadRequerida.toLowerCase()}
                      </div>
                      <div style={styles.getIngredienteMensaje(isVerde, isAmarillo)}>
                        {ing.mensajeEstado}
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>

          {/* Instrucciones de preparación */}
          <div style={styles.sectionBlock}>
            <h3 style={styles.sectionTitle}>
              Instrucciones de preparación
            </h3>
            <div style={styles.instruccionesBox}>
              {receta.instrucciones}
            </div>
          </div>

          {/* Sección ¡Cociné esta receta! */}
          <div style={styles.cookSection}>
            <div style={styles.cookHeaderRow}>
              <div>
                <strong style={styles.cookHeaderTitle}>
                  <Utensils size={18} /> ¿Preparaste este plato?
                </strong>
                <p style={styles.cookHeaderSubtitle}>
                  Guárdalo en tu historial de cocina para recordar tus preparaciones.
                </p>
              </div>

              {!cookedSuccess ? (
                <button
                  className="btn btn-primary"
                  onClick={() => {
                    if (!showNotasInput) {
                      setShowNotasInput(true);
                    } else {
                      handleCocinar();
                    }
                  }}
                  disabled={cooking}
                  style={styles.cookButton}
                >
                  {cooking ? <Loader2 size={16} className="spin" /> : '¡Cociné esta receta!'}
                </button>
              ) : (
                <div style={styles.cookedSuccess}>
                  <CheckCircle2 size={20} /> ¡Registrado en tu historial!
                </div>
              )}
            </div>

            {showNotasInput && !cookedSuccess && (
              <div style={styles.notasRow}>
                <input
                  type="text"
                  placeholder="Nota opcional (ej: Le agregué más queso y quedó espectacular)"
                  value={notas}
                  onChange={(e) => setNotas(e.target.value)}
                  style={styles.notasInput}
                />
                <button
                  className="btn btn-primary"
                  onClick={handleCocinar}
                  disabled={cooking}
                  style={styles.confirmButton}
                >
                  Confirmar
                </button>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
