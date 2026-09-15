import { useState, useEffect } from 'react';
import { X, Clock, Calendar, ChefHat, Loader2, Utensils } from 'lucide-react';
import axiosClient from '../api/axiosClient';
import '../styles/historyModal.css';

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
    <div className="history-overlay">
      <div className="history-panel">
        <div className="history-header">
          <div className="history-header-title-group">
            <ChefHat size={24} color="var(--primary-dark)" />
            <h2 className="history-header-title">Historial de Preparaciones</h2>
          </div>
          <button onClick={onClose} className="history-close-button">
            <X size={22} />
          </button>
        </div>

        {loading ? (
          <div className="history-loading-row">
            <Loader2 size={20} className="spin" /> Cargando historial...
          </div>
        ) : historial.length === 0 ? (
          <div className="history-empty-box">
            <Utensils size={40} color="#9CA3AF" className="history-empty-icon" />
            <p className="history-empty-title">Aún no has registrado recetas cocinadas</p>
            <p className="history-empty-subtitle">
              Cuando prepares un plato, abre los detalles de la receta y haz clic en "¡Cociné esta receta!".
            </p>
          </div>
        ) : (
          <div className="history-list">
            {historial.map((item) => (
              <div
                key={item.id}
                className="history-item-row"
              >
                <img
                  src={item.imagenUrl || 'https://images.unsplash.com/photo-1495521821757-a1efb6729352?w=200&auto=format&fit=crop&q=60'}
                  alt={item.tituloReceta}
                  onError={(e) => {
                    e.currentTarget.onerror = null;
                    e.currentTarget.src = 'https://images.unsplash.com/photo-1495521821757-a1efb6729352?w=200&auto=format&fit=crop&q=60';
                  }}
                  className="history-item-img"
                />

                <div className="history-item-body">
                  <h4 className="history-item-title">
                    {item.tituloReceta}
                  </h4>
                  <div className="history-item-date-row">
                    <Calendar size={14} /> {formatFecha(item.cocinadoAt)}
                  </div>
                  {item.notas && (
                    <p className="history-item-note">
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
