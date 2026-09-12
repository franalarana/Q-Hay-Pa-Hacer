import { X, Clock, Users, ChefHat, CheckCircle2, AlertTriangle, XCircle } from 'lucide-react';

export default function RecipeDetailModal({ receta, onClose }) {
  if (!receta) return null;

  return (
    <div style={{
      position: 'fixed',
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      backgroundColor: 'rgba(0, 0, 0, 0.5)',
      backdropFilter: 'blur(4px)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      zIndex: 1000,
      padding: '20px'
    }}>
      <div style={{
        backgroundColor: 'white',
        borderRadius: 'var(--border-radius-lg)',
        maxWidth: '700px',
        width: '100%',
        maxHeight: '90vh',
        overflowY: 'auto',
        boxShadow: 'var(--shadow-md)',
        position: 'relative',
        display: 'flex',
        flexDirection: 'column'
      }}>
        {/* Imagen y botón de cierre */}
        <div style={{ position: 'relative', height: '240px', width: '100%', overflow: 'hidden' }}>
          <img 
            src={receta.imagenUrl || 'https://images.unsplash.com/photo-1495521821757-a1efb6729352?w=800&auto=format&fit=crop&q=60'} 
            alt={receta.titulo}
            style={{ width: '100%', height: '100%', objectFit: 'cover' }}
          />
          <button 
            onClick={onClose}
            style={{
              position: 'absolute',
              top: '16px',
              right: '16px',
              backgroundColor: 'rgba(255, 255, 255, 0.9)',
              border: 'none',
              borderRadius: '50%',
              width: '36px',
              height: '36px',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              cursor: 'pointer',
              boxShadow: '0 2px 6px rgba(0,0,0,0.15)'
            }}
          >
            <X size={20} color="#333" />
          </button>
        </div>

        {/* Contenido del modal */}
        <div style={{ padding: '24px 32px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '8px' }}>
            <h2 style={{ fontSize: '1.6rem', color: 'var(--text-main)', margin: 0 }}>
              {receta.titulo}
            </h2>
            <span style={{
              padding: '4px 10px',
              borderRadius: '20px',
              fontSize: '0.8rem',
              fontWeight: '600',
              backgroundColor: receta.estadoGeneral === 'VERDE' ? '#DEF7EC' : receta.estadoGeneral === 'AMARILLO' ? '#FEF08A' : '#FDE8E8',
              color: receta.estadoGeneral === 'VERDE' ? '#03543F' : receta.estadoGeneral === 'AMARILLO' ? '#854D0E' : '#9B1C1C'
            }}>
              {receta.estadoGeneral === 'VERDE' ? '🟢 Listo para cocinar' : receta.estadoGeneral === 'AMARILLO' ? '🟡 Faltan cantidades' : '🔴 Faltan ingredientes'}
            </span>
          </div>

          <p style={{ color: 'var(--text-muted)', fontSize: '0.95rem', marginBottom: '20px', lineHeight: '1.5' }}>
            {receta.descripcion}
          </p>

          {/* Metadata chips */}
          <div style={{ display: 'flex', gap: '20px', marginBottom: '24px', paddingBottom: '16px', borderBottom: '1px solid #E5E7EB' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '6px', fontSize: '0.9rem', color: 'var(--text-muted)' }}>
              <Clock size={18} color="var(--primary-dark)" /> {receta.tiempoMinutos} min
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '6px', fontSize: '0.9rem', color: 'var(--text-muted)' }}>
              <Users size={18} color="var(--primary-dark)" /> {receta.porciones} porciones
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '6px', fontSize: '0.9rem', color: 'var(--text-muted)' }}>
              <ChefHat size={18} color="var(--primary-dark)" /> Dificultad: {receta.dificultad}
            </div>
          </div>

          {/* Ingredientes con estado semáforo */}
          <div style={{ marginBottom: '28px' }}>
            <h3 style={{ fontSize: '1.1rem', marginBottom: '12px', color: 'var(--text-main)' }}>
              Ingredientes ({receta.ingredientesVerdes}/{receta.totalIngredientes} listos)
            </h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
              {receta.ingredientes.map((ing) => {
                const isVerde = ing.estado === 'VERDE';
                const isAmarillo = ing.estado === 'AMARILLO';
                return (
                  <div 
                    key={ing.ingredienteId}
                    style={{
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'space-between',
                      padding: '10px 14px',
                      borderRadius: 'var(--border-radius-sm)',
                      backgroundColor: isVerde ? '#F0FDF4' : isAmarillo ? '#FEFCE8' : '#FEF2F2',
                      border: `1px solid ${isVerde ? '#BBF7D0' : isAmarillo ? '#FEF08A' : '#FECACA'}`
                    }}
                  >
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                      {isVerde && <CheckCircle2 size={18} color="#16A34A" />}
                      {isAmarillo && <AlertTriangle size={18} color="#CA8A04" />}
                      {!isVerde && !isAmarillo && <XCircle size={18} color="#DC2626" />}
                      <span style={{ fontWeight: '500', color: 'var(--text-main)', fontSize: '0.9rem' }}>
                        {ing.nombreIngrediente}
                      </span>
                    </div>

                    <div style={{ textAlign: 'right' }}>
                      <div style={{ fontSize: '0.85rem', fontWeight: '600', color: 'var(--text-main)' }}>
                        Requiere: {ing.cantidadRequerida} {ing.unidadRequerida.toLowerCase()}
                      </div>
                      <div style={{ fontSize: '0.75rem', color: isVerde ? '#16A34A' : isAmarillo ? '#CA8A04' : '#DC2626' }}>
                        {ing.mensajeEstado}
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>

          {/* Instrucciones de preparación */}
          <div>
            <h3 style={{ fontSize: '1.1rem', marginBottom: '12px', color: 'var(--text-main)' }}>
              Instrucciones de preparación
            </h3>
            <div style={{ 
              backgroundColor: '#F9FAFB', 
              padding: '16px 20px', 
              borderRadius: 'var(--border-radius-sm)', 
              fontSize: '0.95rem', 
              lineHeight: '1.7',
              whiteSpace: 'pre-line',
              color: '#374151',
              border: '1px solid #E5E7EB'
            }}>
              {receta.instrucciones}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
