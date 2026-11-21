import { useState, useEffect, useCallback } from 'react';
import { X, TrendingUp, Loader2, RefreshCw, Refrigerator, CookingPot, Activity } from 'lucide-react';
import axiosClient from '../api/axiosClient';
import { getIngredienteEmoji } from './Sidebar';
import '../styles/analiticaModal.css';

// Intervalo de refresco: las métricas se recalculan en el backend a medida que
// los consumidores Kafka procesan eventos, así que se consultan periódicamente.
const REFRESH_MS = 5000;
const LIMITE = 10;

function RankingList({ titulo, icono, items, vacio }) {
  const max = items.reduce((m, i) => Math.max(m, i.totalOcurrencias), 0) || 1;

  return (
    <section className="analitica-ranking">
      <h3 className="analitica-ranking-title">
        {icono} {titulo}
      </h3>
      {items.length === 0 ? (
        <p className="analitica-ranking-empty">{vacio}</p>
      ) : (
        <ol className="analitica-ranking-list">
          {items.map((item, idx) => (
            <li
              key={item.nombreIngrediente}
              className="analitica-ranking-row"
              title={`${item.nombreIngrediente}: ${item.totalOcurrencias} evento${item.totalOcurrencias === 1 ? '' : 's'} (${item.porcentaje.toFixed(1)}%)`}
            >
              <span className="analitica-ranking-pos">{idx + 1}</span>
              <span className="analitica-ranking-emoji">{getIngredienteEmoji(item.nombreIngrediente)}</span>
              <div className="analitica-ranking-body">
                <div className="analitica-ranking-label-row">
                  <span className="analitica-ranking-name">{item.nombreIngrediente}</span>
                  <span className="analitica-ranking-value">
                    {item.totalOcurrencias} <small>· {item.porcentaje.toFixed(1)}%</small>
                  </span>
                </div>
                <div className="analitica-bar-track">
                  <div
                    className="analitica-bar-fill"
                    style={{ width: `${(item.totalOcurrencias / max) * 100}%` }}
                  />
                </div>
              </div>
            </li>
          ))}
        </ol>
      )}
    </section>
  );
}

export default function AnaliticaModal({ onClose }) {
  const [resumen, setResumen] = useState(null);
  const [topDespensa, setTopDespensa] = useState([]);
  const [topCocina, setTopCocina] = useState([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState('');
  const [ultimaActualizacion, setUltimaActualizacion] = useState(null);

  const cargar = useCallback(async () => {
    try {
      setRefreshing(true);
      const [resResumen, resDespensa, resCocina] = await Promise.all([
        axiosClient.get('/analitica/resumen'),
        axiosClient.get('/analitica/ingredientes/mas-comunes', { params: { limite: LIMITE } }),
        axiosClient.get('/analitica/ingredientes/mas-cocinados', { params: { limite: LIMITE } })
      ]);
      setResumen(resResumen.data);
      setTopDespensa(resDespensa.data);
      setTopCocina(resCocina.data);
      setUltimaActualizacion(new Date());
      setError('');
    } catch (err) {
      console.error('Error al cargar analítica:', err);
      setError('No se pudo obtener la analítica en tiempo real.');
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, []);

  useEffect(() => {
    cargar();
    const id = setInterval(cargar, REFRESH_MS);
    return () => clearInterval(id);
  }, [cargar]);

  return (
    <div className="analitica-overlay" onClick={onClose}>
      <div className="analitica-panel" onClick={(e) => e.stopPropagation()}>
        <div className="analitica-header">
          <div className="analitica-header-title-group">
            <TrendingUp size={24} color="var(--primary-dark)" />
            <div>
              <h2 className="analitica-header-title">Top Ingredientes</h2>
              <p className="analitica-header-subtitle">
                Analítica en tiempo real con Apache Kafka
              </p>
            </div>
          </div>
          <div className="analitica-header-actions">
            <button
              onClick={cargar}
              className="analitica-icon-button"
              title="Actualizar ahora"
              disabled={refreshing}
            >
              <RefreshCw size={18} className={refreshing ? 'spin' : ''} />
            </button>
            <button onClick={onClose} className="analitica-icon-button" title="Cerrar">
              <X size={22} />
            </button>
          </div>
        </div>

        {loading ? (
          <div className="analitica-loading-row">
            <Loader2 size={20} className="spin" /> Cargando analítica...
          </div>
        ) : (
          <>
            {error && <div className="analitica-error-box">{error}</div>}

            {resumen && (
              <div className="analitica-stats-row">
                <div className="analitica-stat">
                  <span className="analitica-stat-value">{resumen.totalEventosProcesados}</span>
                  <span className="analitica-stat-label"><Activity size={13} /> Eventos procesados</span>
                </div>
                <div className="analitica-stat">
                  <span className="analitica-stat-value">{resumen.totalEventosDespensa}</span>
                  <span className="analitica-stat-label"><Refrigerator size={13} /> Despensa</span>
                </div>
                <div className="analitica-stat">
                  <span className="analitica-stat-value">{resumen.totalEventosCocina}</span>
                  <span className="analitica-stat-label"><CookingPot size={13} /> Cocina</span>
                </div>
              </div>
            )}

            <div className="analitica-rankings-grid">
              <RankingList
                titulo="Más comunes en despensas"
                icono={<Refrigerator size={18} color="var(--primary-dark)" />}
                items={topDespensa}
                vacio="Aún no hay eventos de despensa. Agrega ingredientes para alimentar el ranking."
              />
              <RankingList
                titulo="Más usados al cocinar"
                icono={<CookingPot size={18} color="var(--primary-dark)" />}
                items={topCocina}
                vacio='Aún no hay eventos de cocina. Marca "¡Cociné esta receta!" en alguna receta.'
              />
            </div>

            <div className="analitica-footer">
              Tópicos: <code>analitica.despensa.ingredientes</code> · <code>analitica.cocina.ingredientes</code>
              {ultimaActualizacion && (
                <span> · Actualizado {ultimaActualizacion.toLocaleTimeString('es-CL')} (cada {REFRESH_MS / 1000}s)</span>
              )}
            </div>
          </>
        )}
      </div>
    </div>
  );
}
