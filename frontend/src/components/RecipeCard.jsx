import { Clock, Users, ArrowRight, Heart } from 'lucide-react';
import SemaforoIcon from './SemaforoIcon';
// cambios aqui
import { getIngredienteEmoji } from './Sidebar';
// hasta aqui

export default function RecipeCard({ receta, onSelect, isFavorito, onToggleFavorito }) {
  const isVerde = receta.estadoGeneral === 'VERDE';
  const isAmarillo = receta.estadoGeneral === 'AMARILLO';

  // Badge config según el semáforo
  const badgeBg = isVerde ? '#D1F4E0' : isAmarillo ? '#FEF08A' : '#FBD5D5';
  const badgeColor = isVerde ? '#22543D' : isAmarillo ? '#744210' : '#742A2A';
  const badgeLabel = isVerde ? '¡Listo para cocinar!' : isAmarillo ? 'Faltan cantidades' : 'Faltan ingredientes';

  return (
    <div style={{
      backgroundColor: 'white',
      borderRadius: 'var(--border-radius-md)',
      overflow: 'hidden',
      boxShadow: 'var(--shadow-sm)',
      border: '1px solid #E5E7EB',
      display: 'flex',
      flexDirection: 'column',
      transition: 'transform 0.2s ease, box-shadow 0.2s ease',
      cursor: 'pointer',
      position: 'relative'
    }}
    onClick={() => onSelect(receta)}
    onMouseEnter={(e) => {
      e.currentTarget.style.transform = 'translateY(-4px)';
      e.currentTarget.style.boxShadow = 'var(--shadow-md)';
    }}
    onMouseLeave={(e) => {
      e.currentTarget.style.transform = 'translateY(0)';
      e.currentTarget.style.boxShadow = 'var(--shadow-sm)';
    }}
    >
      {/* Imagen con Badge de estado y Botón de Favorito */}
      <div style={{ position: 'relative', height: '170px', width: '100%' }}>
        <img 
          src={receta.imagenUrl || 'https://images.unsplash.com/photo-1495521821757-a1efb6729352?w=600&auto=format&fit=crop&q=60'} 
          alt={receta.titulo}
          style={{ width: '100%', height: '100%', objectFit: 'cover' }}
        />
        
        {/* Botón Favorito */}
        <button
          onClick={(e) => {
            e.stopPropagation();
            if (onToggleFavorito) onToggleFavorito(receta.id);
          }}
          title={isFavorito ? 'Quitar de favoritos' : 'Guardar en favoritos'}
          style={{
            position: 'absolute',
            top: '12px',
            left: '12px',
            backgroundColor: 'rgba(255, 255, 255, 0.9)',
            border: 'none',
            borderRadius: '50%',
            width: '32px',
            height: '32px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            cursor: 'pointer',
            boxShadow: '0 2px 4px rgba(0,0,0,0.15)',
            transition: 'transform 0.2s'
          }}
          onMouseEnter={(e) => e.currentTarget.style.transform = 'scale(1.1)'}
          onMouseLeave={(e) => e.currentTarget.style.transform = 'scale(1)'}
        >
          <Heart size={18} color={isFavorito ? '#EF4444' : '#666'} fill={isFavorito ? '#EF4444' : 'none'} />
        </button>

        <div style={{
          position: 'absolute',
          top: '12px',
          right: '12px',
          backgroundColor: badgeBg,
          color: badgeColor,
          padding: '4px 10px',
          borderRadius: '20px',
          fontSize: '0.75rem',
          fontWeight: '700',
          boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
          display: 'inline-flex',
          alignItems: 'center',
          gap: '5px'
        }}>
          <SemaforoIcon estado={receta.estadoGeneral} size={9} /> {badgeLabel}
        </div>
      </div>

      {/* Contenido de la tarjeta */}
      <div style={{ padding: '16px 20px', display: 'flex', flexDirection: 'column', flex: 1 }}>
        <h3 style={{ fontSize: '1.15rem', color: 'var(--text-main)', marginBottom: '6px', fontWeight: '700' }}>
          {receta.titulo}
        </h3>
        
        <p style={{
          fontSize: '0.85rem',
          color: 'var(--text-muted)',
          marginBottom: '16px',
          lineHeight: '1.4',
          display: '-webkit-box',
          WebkitLineClamp: 2,
          WebkitBoxOrient: 'vertical',
          overflow: 'hidden'
        }}>
          {receta.descripcion}
        </p>

        {/* Barra de progreso de ingredientes disponibles */}
        <div style={{ marginBottom: '16px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.75rem', fontWeight: '600', marginBottom: '4px' }}>
            <span style={{ color: 'var(--text-muted)' }}>Compatibilidad</span>
            <span style={{ color: isVerde ? '#16A34A' : isAmarillo ? '#CA8A04' : '#DC2626' }}>
              {receta.porcentajeCoincidencia}% ({receta.ingredientesVerdes}/{receta.totalIngredientes} ingredientes)
            </span>
          </div>
          <div style={{ width: '100%', height: '8px', backgroundColor: '#E5E7EB', borderRadius: '4px', overflow: 'hidden' }}>
            <div style={{
              width: `${receta.porcentajeCoincidencia}%`,
              height: '100%',
              backgroundColor: isVerde ? '#38A169' : isAmarillo ? '#D69E2E' : '#E53E3E',
              borderRadius: '4px',
              transition: 'width 0.4s ease'
            }} />
          </div>
        </div>

        {/* Badges de ingredientes semáforo rápidos */}
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '6px', marginBottom: '16px' }}>
          {receta.ingredientes.map(ing => {
            const ingVerde = ing.estado === 'VERDE';
            const ingAmarillo = ing.estado === 'AMARILLO';
            return (
              <span 
                key={ing.ingredienteId}
                style={{
                  padding: '3px 8px',
                  borderRadius: '12px',
                  fontSize: '0.75rem',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '4px',
                  backgroundColor: ingVerde ? '#D1F4E0' : ingAmarillo ? '#FEF08A' : '#FBD5D5',
                  color: ingVerde ? '#22543D' : ingAmarillo ? '#744210' : '#742A2A',
                  fontWeight: '500'
                }}
              >
                {/* cambios aqui */}
                <SemaforoIcon estado={ing.estado} size={8} /> {getIngredienteEmoji(ing.nombreIngrediente)} {ing.nombreIngrediente}
                {/* hasta aqui */}
              </span>
            );
          })}
        </div>

        {/* Footer con tiempo, porciones y botón ver */}
        <div style={{
          marginTop: 'auto',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          paddingTop: '12px',
          borderTop: '1px solid #F3F4F6'
        }}>
          <div style={{ display: 'flex', gap: '12px', fontSize: '0.8rem', color: 'var(--text-muted)' }}>
            <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
              <Clock size={14} /> {receta.tiempoMinutos}m
            </span>
            <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
              <Users size={14} /> {receta.porciones}
            </span>
          </div>

          <span style={{
            fontSize: '0.85rem',
            color: 'var(--primary-dark)',
            fontWeight: '600',
            display: 'flex',
            alignItems: 'center',
            gap: '4px'
          }}>
            Ver receta <ArrowRight size={14} />
          </span>
        </div>
      </div>
    </div>
  );
}
