import { useState, useEffect } from 'react';
import { X, Clock, Calendar, ChefHat, Loader2, Utensils } from 'lucide-react';
import axiosClient from '../api/axiosClient';
import * as styles from '../styles/historyModal.styles';

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
    <div style={styles.overlay}>
      <div style={styles.panel}>
        <div style={styles.header}>
          <div style={styles.headerTitleGroup}>
            <ChefHat size={24} color="var(--primary-dark)" />
            <h2 style={styles.headerTitle}>Historial de Preparaciones</h2>
          </div>
          <button onClick={onClose} style={styles.closeButton}>
            <X size={22} />
          </button>
        </div>

        {loading ? (
          <div style={styles.loadingRow}>
            <Loader2 size={20} className="spin" /> Cargando historial...
          </div>
        ) : historial.length === 0 ? (
          <div style={styles.emptyBox}>
            <Utensils size={40} color="#9CA3AF" style={styles.emptyIcon} />
            <p style={styles.emptyTitle}>Aún no has registrado recetas cocinadas</p>
            <p style={styles.emptySubtitle}>
              Cuando prepares un plato, abre los detalles de la receta y haz clic en "¡Cociné esta receta!".
            </p>
          </div>
        ) : (
          <div style={styles.list}>
            {historial.map((item) => (
              <div
                key={item.id}
                style={styles.itemRow}
              >
                <img
                  src={item.imagenUrl || 'https://images.unsplash.com/photo-1495521821757-a1efb6729352?w=200&auto=format&fit=crop&q=60'}
                  alt={item.tituloReceta}
                  onError={(e) => {
                    e.currentTarget.onerror = null;
                    e.currentTarget.src = 'https://images.unsplash.com/photo-1495521821757-a1efb6729352?w=200&auto=format&fit=crop&q=60';
                  }}
                  style={styles.itemImg}
                />

                <div style={styles.itemBody}>
                  <h4 style={styles.itemTitle}>
                    {item.tituloReceta}
                  </h4>
                  <div style={styles.itemDateRow}>
                    <Calendar size={14} /> {formatFecha(item.cocinadoAt)}
                  </div>
                  {item.notas && (
                    <p style={styles.itemNote}>
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
