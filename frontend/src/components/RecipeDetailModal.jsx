import { useState } from 'react';
import { X, Clock, Users, ChefHat, CheckCircle2, AlertTriangle, XCircle, Heart, Utensils, Loader2, Pencil, Trash2 } from 'lucide-react';
import axiosClient from '../api/axiosClient';
import SemaforoIcon from './SemaforoIcon';
// cambios aqui
import { getIngredienteEmoji } from './Sidebar';
// hasta aqui

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
        {/* Imagen y botones de acción superior */}
        <div style={{ position: 'sticky', top: 0, zIndex: 10, height: '240px', width: '100%', overflow: 'hidden', flexShrink: 0 }}>
          <img 
            src={receta.imagenUrl || 'https://images.unsplash.com/photo-1495521821757-a1efb6729352?w=800&auto=format&fit=crop&q=60'} 
            alt={receta.titulo}
            style={{ width: '100%', height: '100%', objectFit: 'cover' }}
          />
          
          {/* Botón Favorito */}
          <button 
            onClick={() => onToggleFavorito && onToggleFavorito(receta.id)}
            title={isFavorito ? 'Quitar de favoritos' : 'Guardar en favoritos'}
            style={{
              position: 'absolute',
              top: '16px',
              left: '16px',
              backgroundColor: 'rgba(255, 255, 255, 0.9)',
              border: 'none',
              borderRadius: '50%',
              width: '40px',
              height: '40px',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              cursor: 'pointer',
              boxShadow: '0 2px 6px rgba(0,0,0,0.15)'
            }}
          >
            <Heart size={22} color={isFavorito ? '#EF4444' : '#666'} fill={isFavorito ? '#EF4444' : 'none'} />
          </button>

          {/* Botón Cerrar */}
          <button 
            onClick={onClose}
            style={{
              position: 'absolute',
              top: '16px',
              right: '16px',
              backgroundColor: 'rgba(255, 255, 255, 0.9)',
              border: 'none',
              borderRadius: '50%',
              width: '40px',
              height: '40px',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              cursor: 'pointer',
              boxShadow: '0 2px 6px rgba(0,0,0,0.15)'
            }}
          >
            <X size={22} color="#333" />
          </button>
        </div>

        {/* Contenido del modal */}
        <div style={{ padding: '24px 32px' }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '12px', marginBottom: '8px' }}>
            <h2 style={{ fontSize: '1.6rem', color: 'var(--text-main)', margin: 0 }}>
              {receta.titulo}
            </h2>
            <span style={{
              padding: '4px 12px',
              borderRadius: '20px',
              fontSize: '0.85rem',
              fontWeight: '600',
              display: 'inline-flex',
              alignItems: 'center',
              gap: '6px',
              backgroundColor: receta.estadoGeneral === 'VERDE' ? '#DEF7EC' : receta.estadoGeneral === 'AMARILLO' ? '#FEF08A' : '#FDE8E8',
              color: receta.estadoGeneral === 'VERDE' ? '#03543F' : receta.estadoGeneral === 'AMARILLO' ? '#854D0E' : '#9B1C1C'
            }}>
              <SemaforoIcon estado={receta.estadoGeneral} size={10} />
              {receta.estadoGeneral === 'VERDE' ? 'Listo para cocinar' : receta.estadoGeneral === 'AMARILLO' ? 'Faltan cantidades' : 'Faltan ingredientes'}
            </span>
          </div>

          {esPropia && (
            <div style={{ display: 'flex', gap: '8px', marginBottom: '16px' }}>
              <button
                className="btn btn-outline"
                onClick={() => onEditar && onEditar(receta)}
                style={{ padding: '8px 14px', fontSize: '0.85rem', gap: '6px' }}
              >
                <Pencil size={15} /> Editar receta
              </button>
              <button
                className="btn btn-outline"
                onClick={() => onEliminar && onEliminar(receta)}
                style={{ padding: '8px 14px', fontSize: '0.85rem', gap: '6px', color: '#DC2626', borderColor: '#FECACA' }}
              >
                <Trash2 size={15} /> Eliminar receta
              </button>
            </div>
          )}

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
                      backgroundColor: isVerde ? '#F0FDFA' : isAmarillo ? '#F0F9FF' : '#F9FAFB',
                      border: `1px solid ${isVerde ? '#99F6E4' : isAmarillo ? '#BAE6FD' : '#E5E7EB'}`
                    }}
                  >
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                      {isVerde && <CheckCircle2 size={18} color="#16A34A" />}
                      {isAmarillo && <AlertTriangle size={18} color="#CA8A04" />}
                      {!isVerde && !isAmarillo && <XCircle size={18} color="#DC2626" />}
                      {/* cambios aqui */}
                      <span style={{ fontWeight: '500', color: 'var(--text-main)', fontSize: '0.9rem' }}>
                        {getIngredienteEmoji(ing.nombreIngrediente)} {ing.nombreIngrediente}
                      </span>
                      {/* hasta aqui */}
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
          <div style={{ marginBottom: '28px' }}>
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

          {/* Sección ¡Cociné esta receta! */}
          <div style={{
            padding: '20px',
            backgroundColor: '#F0FDF4',
            borderRadius: 'var(--border-radius-md)',
            border: '1px solid #BBF7D0',
            display: 'flex',
            flexDirection: 'column',
            gap: '12px'
          }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '12px' }}>
              <div>
                <strong style={{ color: '#166534', fontSize: '1rem', display: 'flex', alignItems: 'center', gap: '6px' }}>
                  <Utensils size={18} /> ¿Preparaste este plato?
                </strong>
                <p style={{ margin: '4px 0 0', color: '#15803D', fontSize: '0.85rem' }}>
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
                  style={{ backgroundColor: '#16A34A', color: 'white', padding: '10px 18px', gap: '6px' }}
                >
                  {cooking ? <Loader2 size={16} className="spin" /> : '¡Cociné esta receta!'}
                </button>
              ) : (
                <div style={{ color: '#16A34A', fontWeight: 'bold', display: 'flex', alignItems: 'center', gap: '6px' }}>
                  <CheckCircle2 size={20} /> ¡Registrado en tu historial!
                </div>
              )}
            </div>

            {showNotasInput && !cookedSuccess && (
              <div style={{ display: 'flex', gap: '8px', marginTop: '6px' }}>
                <input 
                  type="text" 
                  placeholder="Nota opcional (ej: Le agregué más queso y quedó espectacular)"
                  value={notas}
                  onChange={(e) => setNotas(e.target.value)}
                  style={{ flex: 1, padding: '8px 12px', borderRadius: 'var(--border-radius-sm)', border: '1px solid #86EFAC', fontSize: '0.85rem' }}
                />
                <button 
                  className="btn btn-primary"
                  onClick={handleCocinar}
                  disabled={cooking}
                  style={{ backgroundColor: '#16A34A', color: 'white', padding: '8px 14px', fontSize: '0.85rem' }}
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
