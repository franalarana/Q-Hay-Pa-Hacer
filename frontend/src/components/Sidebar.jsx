import { Search, Plus } from 'lucide-react';

export default function Sidebar() {
  return (
    <div className="sidebar">
      <h2>Tu Despensa</h2>
      <p className="subtitle">(Ingredientes que tienes)</p>
      
      <div style={{ position: 'relative', marginBottom: '24px' }}>
        <input 
          type="text" 
          placeholder="Añadir ingrediente..." 
          style={{
            width: '100%',
            padding: '12px 16px',
            borderRadius: '50px',
            border: 'none',
            outline: 'none',
            fontFamily: 'inherit',
            boxShadow: 'inset 0 1px 3px rgba(0,0,0,0.1)'
          }}
        />
        <Search size={18} color="#999" style={{ position: 'absolute', right: '16px', top: '10px' }} />
      </div>

      <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px', marginBottom: '24px' }}>
        {['Harina', 'Huevos', 'Leche', 'Azúcar', 'Mantequilla'].map(ing => (
          <span 
            key={ing} 
            style={{
              backgroundColor: 'rgba(255,255,255,0.4)',
              padding: '6px 12px',
              borderRadius: '20px',
              fontSize: '0.85rem',
              display: 'flex',
              alignItems: 'center',
              gap: '4px',
              cursor: 'pointer'
            }}
          >
            {ing} <span style={{ opacity: 0.5 }}>×</span>
          </span>
        ))}
      </div>

      <button className="btn btn-outline" style={{ width: '100%' }}>
        <Plus size={16} style={{ marginRight: '8px' }} /> Añadir más ingredientes
      </button>

      <div style={{ marginTop: 'auto', textAlign: 'center', opacity: 0.6 }}>
        {/* Placeholder for illustrations from the design */}
      </div>
    </div>
  );
}
