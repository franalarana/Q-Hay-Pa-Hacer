export const overlay = {
  position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
  backgroundColor: 'rgba(0, 0, 0, 0.5)',
  backdropFilter: 'blur(4px)',
  display: 'flex', alignItems: 'center', justifyContent: 'center',
  zIndex: 1000, padding: '20px'
};

export const panel = {
  backgroundColor: 'white',
  borderRadius: 'var(--border-radius-lg)',
  maxWidth: '680px',
  width: '100%',
  maxHeight: '90vh',
  overflowY: 'auto',
  padding: '32px',
  boxShadow: 'var(--shadow-md)',
  position: 'relative'
};

export const header = {
  display: 'flex', justifyContent: 'space-between', alignItems: 'center',
  marginBottom: '20px', position: 'sticky', top: '-32px', zIndex: 10,
  backgroundColor: 'white', paddingTop: '32px', marginTop: '-32px'
};

export const headerTitleGroup = {
  display: 'flex',
  alignItems: 'center',
  gap: '8px'
};

export const headerTitle = {
  fontSize: '1.5rem',
  margin: 0,
  color: 'var(--text-main)'
};

export const closeButton = {
  background: 'none',
  border: 'none',
  cursor: 'pointer',
  color: '#666'
};

export const errorBox = {
  display: 'flex',
  alignItems: 'center',
  gap: '6px',
  color: '#EF4444',
  backgroundColor: '#FEE2E2',
  padding: '10px 14px',
  borderRadius: 'var(--border-radius-sm)',
  marginBottom: '16px',
  fontSize: '0.85rem'
};

export const form = {
  display: 'flex',
  flexDirection: 'column',
  gap: '16px'
};

export const fieldLabel = {
  fontSize: '0.85rem',
  fontWeight: '600',
  color: 'var(--text-main)',
  display: 'block',
  marginBottom: '6px'
};

export const textInput = {
  width: '100%',
  padding: '10px 12px',
  borderRadius: 'var(--border-radius-sm)',
  border: '1px solid #D1D5DB',
  fontSize: '0.9rem'
};

export const threeColGrid = {
  display: 'grid',
  gridTemplateColumns: 'repeat(3, 1fr)',
  gap: '12px'
};

export const smallInput = {
  width: '100%',
  padding: '8px 12px',
  borderRadius: 'var(--border-radius-sm)',
  border: '1px solid #D1D5DB',
  fontSize: '0.9rem'
};

export const ingredientesHeaderRow = {
  display: 'flex',
  justifyContent: 'space-between',
  alignItems: 'center',
  marginBottom: '8px'
};

export const ingredientesHeaderLabel = {
  fontSize: '0.85rem',
  fontWeight: '600',
  color: 'var(--text-main)'
};

export const addIngredienteButton = {
  padding: '4px 10px',
  fontSize: '0.75rem',
  gap: '4px'
};

export const ingredientesList = {
  display: 'flex',
  flexDirection: 'column',
  gap: '8px'
};

export const ingredienteRow = {
  display: 'flex',
  gap: '8px',
  alignItems: 'center'
};

export const ingredienteSelect = {
  flex: 2,
  padding: '8px',
  borderRadius: 'var(--border-radius-sm)',
  border: '1px solid #D1D5DB',
  fontSize: '0.85rem'
};

export const ingredienteCantidadInput = {
  flex: 1,
  padding: '8px',
  borderRadius: 'var(--border-radius-sm)',
  border: '1px solid #D1D5DB',
  fontSize: '0.85rem'
};

export const ingredienteUnidadSelect = {
  flex: 1.5,
  padding: '8px',
  borderRadius: 'var(--border-radius-sm)',
  border: '1px solid #D1D5DB',
  fontSize: '0.85rem'
};

export const removeIngredienteButton = {
  background: 'none',
  border: 'none',
  color: '#EF4444',
  cursor: 'pointer',
  padding: '4px'
};

export const textarea = {
  width: '100%',
  padding: '10px 12px',
  borderRadius: 'var(--border-radius-sm)',
  border: '1px solid #D1D5DB',
  fontSize: '0.9rem',
  fontFamily: 'inherit',
  resize: 'vertical'
};

export const formActionsRow = {
  display: 'flex',
  gap: '12px',
  marginTop: '12px'
};

export const submitButton = {
  flex: 1,
  padding: '12px'
};

export const cancelButton = {
  padding: '12px 20px'
};
