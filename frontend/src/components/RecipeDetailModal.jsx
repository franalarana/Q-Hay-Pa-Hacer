import { useState } from 'react';
import { X, Clock, Users, ChefHat, CheckCircle2, AlertTriangle, XCircle, Heart, Utensils, Loader2, Pencil, Trash2 } from 'lucide-react';
import axiosClient from '../api/axiosClient';
import SemaforoIcon from './SemaforoIcon';
// cambios aqui
import { getIngredienteEmoji } from './Sidebar';
// hasta aqui
import '../styles/recipeDetailModal.css';

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
    <div className="detail-overlay">
      <div className="detail-panel">
        {/* Imagen y botones de acción superior */}
        <div className="detail-image-wrapper">
          <img
            src={receta.imagenUrl || 'https://images.unsplash.com/photo-1495521821757-a1efb6729352?w=800&auto=format&fit=crop&q=60'}
            alt={receta.titulo}
            onError={(e) => {
              e.currentTarget.onerror = null;
              e.currentTarget.src = 'https://images.unsplash.com/photo-1495521821757-a1efb6729352?w=800&auto=format&fit=crop&q=60';
            }}
            className="detail-image"
          />

          {/* Botón Favorito */}
          <button
            onClick={() => onToggleFavorito && onToggleFavorito(receta.id)}
            title={isFavorito ? 'Quitar de favoritos' : 'Guardar en favoritos'}
            className="detail-round-top-button detail-round-top-button--left"
          >
            <Heart size={22} color={isFavorito ? '#EF4444' : '#666'} fill={isFavorito ? '#EF4444' : 'none'} />
          </button>

          {/* Botón Cerrar */}
          <button
            onClick={onClose}
            className="detail-round-top-button detail-round-top-button--right"
          >
            <X size={22} color="#333" />
          </button>
        </div>

        {/* Contenido del modal */}
        <div className="detail-content">
          <div className="detail-title-row">
            <h2 className="detail-title">
              {receta.titulo}
            </h2>
            <span className="detail-estado-badge" data-estado={receta.estadoGeneral}>
              <SemaforoIcon estado={receta.estadoGeneral} size={10} />
              {receta.estadoGeneral === 'VERDE' ? 'Listo para cocinar' : receta.estadoGeneral === 'AMARILLO' ? 'Faltan cantidades' : 'Faltan ingredientes'}
            </span>
          </div>

          {esPropia && (
            <div className="detail-owner-actions-row">
              <button
                className="btn btn-outline detail-edit-button"
                onClick={() => onEditar && onEditar(receta)}
              >
                <Pencil size={15} /> Editar receta
              </button>
              <button
                className="btn btn-outline detail-delete-button"
                onClick={() => onEliminar && onEliminar(receta)}
              >
                <Trash2 size={15} /> Eliminar receta
              </button>
            </div>
          )}

          <p className="detail-description">
            {receta.descripcion}
          </p>

          {/* Metadata chips */}
          <div className="detail-metadata-row">
            <div className="detail-metadata-item">
              <Clock size={18} color="var(--primary-dark)" /> {receta.tiempoMinutos} min
            </div>
            <div className="detail-metadata-item">
              <Users size={18} color="var(--primary-dark)" /> {receta.porciones} porciones
            </div>
            <div className="detail-metadata-item">
              <ChefHat size={18} color="var(--primary-dark)" /> Dificultad: {receta.dificultad}
            </div>
          </div>

          {/* Ingredientes con estado semáforo */}
          <div className="detail-section-block">
            <h3 className="detail-section-title">
              Ingredientes ({receta.ingredientesVerdes}/{receta.totalIngredientes} listos)
            </h3>
            <div className="detail-ingredientes-list">
              {receta.ingredientes.map((ing) => {
                const isVerde = ing.estado === 'VERDE';
                const isAmarillo = ing.estado === 'AMARILLO';
                return (
                  <div
                    key={ing.ingredienteId}
                    className="detail-ingrediente-row"
                    data-estado={ing.estado}
                  >
                    <div className="detail-ingrediente-name-group">
                      {isVerde && <CheckCircle2 size={18} color="#16A34A" />}
                      {isAmarillo && <AlertTriangle size={18} color="#CA8A04" />}
                      {!isVerde && !isAmarillo && <XCircle size={18} color="#DC2626" />}
                      {/* cambios aqui */}
                      <span className="detail-ingrediente-name">
                        {getIngredienteEmoji(ing.nombreIngrediente)} {ing.nombreIngrediente}
                      </span>
                      {/* hasta aqui */}
                    </div>

                    <div className="detail-ingrediente-right-block">
                      <div className="detail-ingrediente-requerido">
                        Requiere: {ing.cantidadRequerida} {ing.unidadRequerida.toLowerCase()}
                      </div>
                      <div className="detail-ingrediente-mensaje" data-estado={ing.estado}>
                        {ing.mensajeEstado}
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>

          {/* Instrucciones de preparación */}
          <div className="detail-section-block">
            <h3 className="detail-section-title">
              Instrucciones de preparación
            </h3>
            <div className="detail-instrucciones-box">
              {receta.instrucciones}
            </div>
          </div>

          {/* Sección ¡Cociné esta receta! */}
          <div className="detail-cook-section">
            <div className="detail-cook-header-row">
              <div>
                <strong className="detail-cook-header-title">
                  <Utensils size={18} /> ¿Preparaste este plato?
                </strong>
                <p className="detail-cook-header-subtitle">
                  Guárdalo en tu historial de cocina para recordar tus preparaciones.
                </p>
              </div>

              {!cookedSuccess ? (
                <button
                  className="btn btn-primary detail-cook-button"
                  onClick={() => {
                    if (!showNotasInput) {
                      setShowNotasInput(true);
                    } else {
                      handleCocinar();
                    }
                  }}
                  disabled={cooking}
                >
                  {cooking ? <Loader2 size={16} className="spin" /> : '¡Cociné esta receta!'}
                </button>
              ) : (
                <div className="detail-cooked-success">
                  <CheckCircle2 size={20} /> ¡Registrado en tu historial!
                </div>
              )}
            </div>

            {showNotasInput && !cookedSuccess && (
              <div className="detail-notas-row">
                <input
                  type="text"
                  placeholder="Nota opcional (ej: Le agregué más queso y quedó espectacular)"
                  value={notas}
                  onChange={(e) => setNotas(e.target.value)}
                  className="detail-notas-input"
                />
                <button
                  className="btn btn-primary detail-confirm-button"
                  onClick={handleCocinar}
                  disabled={cooking}
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
