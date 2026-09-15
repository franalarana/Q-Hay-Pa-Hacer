import { useState } from 'react';
import { Clock, Users, ArrowRight, Heart } from 'lucide-react';
import SemaforoIcon from './SemaforoIcon';
import * as styles from '../styles/recipeCard.styles';

export default function RecipeCard({ receta, onSelect, isFavorito, onToggleFavorito }) {
  const [isHovered, setIsHovered] = useState(false);
  const isVerde = receta.estadoGeneral === 'VERDE';
  const isAmarillo = receta.estadoGeneral === 'AMARILLO';

  // Badge config según el semáforo
  const badgeBg = isVerde ? '#D1F4E0' : isAmarillo ? '#FEF08A' : '#FBD5D5';
  const badgeColor = isVerde ? '#22543D' : isAmarillo ? '#744210' : '#742A2A';
  const badgeLabel = isVerde ? '¡Listo para cocinar!' : isAmarillo ? 'Faltan cantidades' : 'Faltan ingredientes';
  const progressColor = isVerde ? '#16A34A' : isAmarillo ? '#CA8A04' : '#DC2626';
  const progressFillColor = isVerde ? '#38A169' : isAmarillo ? '#D69E2E' : '#E53E3E';

  return (
    <div className="frame-pastel" style={styles.card}
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
      <div style={styles.imageWrapper}>
        <img
          src={receta.imagenUrl || 'https://images.unsplash.com/photo-1495521821757-a1efb6729352?w=600&auto=format&fit=crop&q=60'}
          alt={receta.titulo}
          onError={(e) => {
            e.currentTarget.onerror = null;
            e.currentTarget.src = 'https://images.unsplash.com/photo-1495521821757-a1efb6729352?w=600&auto=format&fit=crop&q=60';
          }}
          style={styles.image}
        />

        {/* Botón Favorito */}
        <button
          onClick={(e) => {
            e.stopPropagation();
            if (onToggleFavorito) onToggleFavorito(receta.id);
          }}
          title={isFavorito ? 'Quitar de favoritos' : 'Guardar en favoritos'}
          style={styles.favoritoButton}
          onMouseEnter={(e) => e.currentTarget.style.transform = 'scale(1.1)'}
          onMouseLeave={(e) => e.currentTarget.style.transform = 'scale(1)'}
        >
          <Heart size={18} color={isFavorito ? '#EF4444' : '#666'} fill={isFavorito ? '#EF4444' : 'none'} />
        </button>

        <div style={styles.getBadge(badgeBg, badgeColor)}>
          <SemaforoIcon estado={receta.estadoGeneral} size={9} /> {badgeLabel}
        </div>
      </div>

      {/* Contenido de la tarjeta */}
      <div style={styles.body}>
        <h3 style={styles.title}>
          {receta.titulo}
        </h3>

        {/* Barra de progreso de ingredientes disponibles */}
        <div style={styles.getProgressSection(isHovered)}>
          <div style={styles.progressLabelRow}>
            <span style={styles.progressLabelText}>Compatibilidad</span>
            <span style={styles.getProgressPercentText(progressColor)}>
              {receta.porcentajeCoincidencia}% ({receta.ingredientesVerdes}/{receta.totalIngredientes} ingredientes)
            </span>
          </div>
          <div style={styles.progressTrack}>
            <div style={styles.getProgressFill(receta.porcentajeCoincidencia, progressFillColor)} />
          </div>
        </div>

        {/* Contenido que se despliega on hover */}
        <div style={styles.getHoverContent(isHovered)}>
          {/* Badges de ingredientes semáforo rápidos */}
          <div style={styles.ingredientBadgesRow}>
            {receta.ingredientes.map(ing => {
              return (
                <span
                  key={ing.ingredienteId}
                  style={styles.ingredientBadge}
                >
                  <SemaforoIcon estado={ing.estado} size={8} /> {ing.nombreIngrediente}
                </span>
              );
            })}
          </div>

          {/* Footer con tiempo, porciones y botón ver */}
          <div style={styles.footer}>
            <div style={styles.footerMetaGroup}>
              <span style={styles.footerMetaItem}>
                <Clock size={14} /> {receta.tiempoMinutos}m
              </span>
              <span style={styles.footerMetaItem}>
                <Users size={14} /> {receta.porciones}
              </span>
            </div>

            <span style={styles.footerLink}>
              Ver receta <ArrowRight size={14} />
            </span>
          </div>
        </div>
      </div>
    </div>
  );
}
