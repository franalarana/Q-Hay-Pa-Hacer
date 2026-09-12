import { useState, useEffect } from 'react';
import { X, Clock, Calendar, ChefHat, Loader2, Utensils } from 'lucide-react';
import axiosClient from '../api/axiosClient';

export default function HistoryModal({ onClose }) {
  const [historial, setHistorial] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    axiosClient.get('/historial')
      .then(res => setHistorial(res.data))
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  }, []);

  const formatFecha = (isoString) => {
    if (!isoString) return '';
    const date = new Date(isoString);
    return date.toLocaleDateString('es-CL', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
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
        maxWidth: '600px',
        width: '100%',
        maxHeight: '85vh',
        overflowY: 'auto',
        padding: '28px',
        boxShadow: 'var(--shadow-md)',
        position: 'relative'
      }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <ChefHat size={24} color="var(--primary-dark)" />
            <h2 style={{ fontSize: '1.4rem', margin: 0, color: 'var(--text-main)' }}>Historial de Preparaciones</h2>
          </div>
          <button onClick={onClose} style={{ background: 'none', border: 'none', cursor: 'pointer', color: '#666' }}>
            <X size={22} />
          </button>
        </div>

        {loading ? (
          <div style={{ textAlign: 'center', padding: '40px 0', color: 'var(--text-muted)', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px' }}>
            <Loader2 size={20} className="spin" /> Cargando historial...
          </div>
        ) : historial.length === 0 ? (
          <div style={{
            textAlign: 'center', padding: '40px 20px',
            backgroundColor: '#F9FAFB', borderRadius: 'var(--border-radius-md)',
            border: '2px dashed #E5E7EB'
          }}>
            <Utensils size={40} color="#9CA3AF" style={{ marginBottom: '8px' }} />
            <p style={{ color: 'var(--text-main)', fontWeight: '600', marginBottom: '4px' }}>Aún no has registrado recetas cocinadas</p>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>
              Cuando prepares un plato, abre los detalles de la receta y haz clic en "¡Cociné esta receta!".
            </p>
          </div>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            {historial.map((item) => (
              <div 
                key={item.id}
                style={{
                  display: 'flex',
                  gap: '16px',
                  padding: '14px',
                  backgroundColor: '#F9FAFB',
                  borderRadius: 'var(--border-radius-sm)',
                  border: '1px solid #E5E7EB',
                  alignItems: 'center'
                }}
              >
                <img 
                  src={item.imagenUrl || 'https://images.unsplash.com/photo-1495521821757-a1efb6729352?w=200&auto=format&fit=crop&q=60'} 
                  alt={item.tituloReceta}
                  style={{ width: '64px', height: '64px', borderRadius: 'var(--border-radius-sm)', objectFit: 'cover' }}
                />
                
                <div style={{ flex: 1 }}>
                  <h4 style={{ margin: '0 0 4px 0', fontSize: '1rem', color: 'var(--text-main)' }}>
                    {item.tituloReceta}
                  </h4>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '6px', fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                    <Calendar size={14} /> {formatFecha(item.cocinadoAt)}
                  </div>
                  {item.notas && (
                    <p style={{ margin: '4px 0 0 0', fontSize: '0.8rem', color: 'var(--primary-dark)', fontStyle: 'italic' }}>
                      "{item.notas}"
                    </p>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
