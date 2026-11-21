import { useState, useEffect, useCallback } from 'react';
import { X, TrendingUp, Loader2, RefreshCw, Refrigerator, CookingPot, Activity } from 'lucide-react';
import axiosClient from '../api/axiosClient';
import { getIngredienteEmoji } from './Sidebar';
import '../styles/analiticaModal.css';

// Intervalo de refresco: 5 segundos para actualizar métricas en tiempo real
const REFRESH_MS = 5000;
const LIMITE = 10;

export function getRecetaEmoji(titulo = '') {
  const t = (titulo || '').toLowerCase();
  if (t.includes('arroz') || t.includes('risotto')) return '🍚';
  if (t.includes('fideo') || t.includes('pasta') || t.includes('tallarin') || t.includes('espagueti') || t.includes('spaghetti')) return '🍝';
  if (t.includes('sopa') || t.includes('caldo') || t.includes('cazuela') || t.includes('charquicán')) return '🍲';
  if (t.includes('carne') || t.includes('asado') || t.includes('bife') || t.includes('costillar') || t.includes('vacuno')) return '🥩';
  if (t.includes('pollo')) return '🍗';
  if (t.includes('pescado') || t.includes('atun') || t.includes('atún') || t.includes('congrio') || t.includes('marisco') || t.includes('macha')) return '🐟';
  if (t.includes('huevo') || t.includes('tortilla') || t.includes('omelet')) return '🍳';
  if (t.includes('pan') || t.includes('sandwich') || t.includes('tostada') || t.includes('marraqueta')) return '🥪';
  if (t.includes('ensalada')) return '🥗';
  if (t.includes('torta') || t.includes('pastel') || t.includes('queque') || t.includes('postre') || t.includes('dulce') || t.includes('manjar')) return '🍰';
  if (t.includes('pizza')) return '🍕';
  if (t.includes('empanada')) return '🥟';
  if (t.includes('poroto') || t.includes('lenteja') || t.includes('garbanzo')) return '🥣';
  return '🍽️';
}

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

function RecetaRankingList({ titulo, icono, items, vacio }) {
  const max = items.reduce((m, i) => Math.max(m, i.totalCocinadas), 0) || 1;

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
              key={item.recetaId || item.titulo}
              className="analitica-ranking-row"
              title={`${item.titulo}: ${item.totalCocinadas} ${item.totalCocinadas === 1 ? 'vez cocinada' : 'veces cocinada'} (${item.porcentaje.toFixed(1)}%)`}
            >
              <span className="analitica-ranking-pos">{idx + 1}</span>
              <span className="analitica-ranking-emoji">{getRecetaEmoji(item.titulo)}</span>
              <div className="analitica-ranking-body">
                <div className="analitica-ranking-label-row">
                  <span className="analitica-ranking-name">{item.titulo}</span>
                  <span className="analitica-ranking-value">
                    {item.totalCocinadas} {item.totalCocinadas === 1 ? 'vez' : 'veces'} <small>· {item.porcentaje.toFixed(1)}%</small>
                  </span>
                </div>
                <div className="analitica-bar-track">
                  <div
                    className="analitica-bar-fill"
                    style={{ width: `${(item.totalCocinadas / max) * 100}%` }}
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
  const [topRecetasSemana, setTopRecetasSemana] = useState([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState('');
  const [ultimaActualizacion, setUltimaActualizacion] = useState(null);

  const cargar = useCallback(async () => {
    try {
      setRefreshing(true);
      const [resResumen, resDespensa, resRecetasSemana] = await Promise.all([
        axiosClient.get('/analitica/resumen'),
        axiosClient.get('/analitica/ingredientes/mas-comunes', { params: { limite: LIMITE } }),
        axiosClient.get('/analitica/recetas/top-semanal', { params: { limite: LIMITE } })
      ]);
      setResumen(resResumen.data);
      setTopDespensa(resDespensa.data);
      setTopRecetasSemana(resRecetasSemana.data);
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

  const totalCocinadasSemana = topRecetasSemana.reduce((sum, r) => sum + (r.totalCocinadas || 0), 0);

  return (
    <div className="analitica-overlay" onClick={onClose}>
      <div className="analitica-panel" onClick={(e) => e.stopPropagation()}>
        <div className="analitica-header">
          <div className="analitica-header-title-group">
            <TrendingUp size={24} color="var(--primary-dark)" />
            <div>
              <h2 className="analitica-header-title">Top Ingredientes y Recetas</h2>
              <p className="analitica-header-subtitle">
                Analítica en tiempo real y tendencias de cocina
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
                  <span className="analitica-stat-value">{totalCocinadasSemana}</span>
                  <span className="analitica-stat-label"><CookingPot size={13} /> Recetas (semana)</span>
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
              <RecetaRankingList
                titulo="Top semanal: más cocinadas"
                icono={<CookingPot size={18} color="var(--primary-dark)" />}
                items={topRecetasSemana}
                vacio='Aún no hay recetas cocinadas esta semana. Marca "¡Cociné esta receta!" en alguna receta.'
              />
            </div>

            <div className="analitica-footer">
              Fuente: <code>analitica.despensa.ingredientes</code> (Kafka) · <code>historial_cocina</code> (semanal)
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


