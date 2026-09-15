import { useState } from 'react';
import { Clock, Users, ArrowRight, Heart } from 'lucide-react';
import SemaforoIcon from './SemaforoIcon';
import '../styles/recipeCard.css';

export default function RecipeCard({ receta, onSelect, isFavorito, onToggleFavorito }) {
  const [isHovered, setIsHovered] = useState(false);
  const isVerde = receta.estadoGeneral === 'VERDE';
  const isAmarillo = receta.estadoGeneral === 'AMARILLO';
  const badgeLabel = isVerde ? '¡Listo para cocinar!' : isAmarillo ? 'Faltan cantidades' : 'Faltan ingredientes';

  return (
    <div className={`frame-pastel recipe-card ${isHovered ? 'hovered' : ''}`}
    onClick={() => onSelect(receta)}
    onMouseEnter={(e) => {
      setIsHovered(true);
      e.currentTarget.style.transform = 'translateY(-4px)';
      e.currentTarget.style.boxShadow = 'var(--shadow-md)';
    }}
    onMouseLeave={(e) => {
      setIsHovered(false);
      e.currentTarget.style.transform = 'translateY(0)';
      e.currentTarget.style.boxShadow = 'var(--shadow-sm)';
    }}
    >
      {/* Imagen con Badge de estado y Botón de Favorito */}
      <div className="recipe-card-image-wrapper">
        <img
          src={receta.imagenUrl || 'https://images.unsplash.com/photo-1495521821757-a1efb6729352?w=600&auto=format&fit=crop&q=60'}
          alt={receta.titulo}
          onError={(e) => {
            e.currentTarget.onerror = null;
            e.currentTarget.src = 'https://images.unsplash.com/photo-1495521821757-a1efb6729352?w=600&auto=format&fit=crop&q=60';
          }}
          className="recipe-card-image"
        />

        {/* Botón Favorito */}
        <button
          onClick={(e) => {
            e.stopPropagation();
            if (onToggleFavorito) onToggleFavorito(receta.id);
          }}
          title={isFavorito ? 'Quitar de favoritos' : 'Guardar en favoritos'}
          className="recipe-card-favorito-button"
          onMouseEnter={(e) => e.currentTarget.style.transform = 'scale(1.1)'}
          onMouseLeave={(e) => e.currentTarget.style.transform = 'scale(1)'}
        >
          <Heart size={18} color={isFavorito ? '#EF4444' : '#666'} fill={isFavorito ? '#EF4444' : 'none'} />
        </button>

        <div className="recipe-card-badge" data-estado={receta.estadoGeneral}>
          <SemaforoIcon estado={receta.estadoGeneral} size={9} /> {badgeLabel}
        </div>
      </div>

      {/* Contenido de la tarjeta */}
      <div className="recipe-card-body">
        <h3 className="recipe-card-title">
          {receta.titulo}
        </h3>

        {/* Barra de progreso de ingredientes disponibles */}
        <div className="recipe-card-progress-section">
          <div className="recipe-card-progress-label-row">
            <span className="recipe-card-progress-label-text">Compatibilidad</span>
            <span className="recipe-card-progress-percent" data-estado={receta.estadoGeneral}>
              {receta.porcentajeCoincidencia}% ({receta.ingredientesVerdes}/{receta.totalIngredientes} ingredientes)
            </span>
          </div>
          <div className="recipe-card-progress-track">
            <div
              className="recipe-card-progress-fill"
              data-estado={receta.estadoGeneral}
              style={{ width: `${receta.porcentajeCoincidencia}%` }}
            />
          </div>
        </div>

        {/* Contenido que se despliega on hover */}
        <div className="recipe-card-hover-content">
          {/* Badges de ingredientes semáforo rápidos */}
          <div className="recipe-card-ingredient-badges-row">
            {receta.ingredientes.map(ing => {
              return (
                <span
                  key={ing.ingredienteId}
                  className="recipe-card-ingredient-badge"
                >
                  <SemaforoIcon estado={ing.estado} size={8} /> {ing.nombreIngrediente}
                </span>
              );
            })}
          </div>

          {/* Footer con tiempo, porciones y botón ver */}
          <div className="recipe-card-footer">
            <div className="recipe-card-footer-meta-group">
              <span className="recipe-card-footer-meta-item">
                <Clock size={14} /> {receta.tiempoMinutos}m
              </span>
              <span className="recipe-card-footer-meta-item">
                <Users size={14} /> {receta.porciones}
              </span>
            </div>

            <span className="recipe-card-footer-link">
              Ver receta <ArrowRight size={14} />
            </span>
          </div>
        </div>
      </div>
    </div>
  );
}
