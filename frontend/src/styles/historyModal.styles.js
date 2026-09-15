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
  maxWidth: '600px',
  width: '100%',
  maxHeight: '85vh',
  overflowY: 'auto',
  padding: '28px',
  boxShadow: 'var(--shadow-md)',
  position: 'relative'
};

export const header = {
  display: 'flex', justifyContent: 'space-between', alignItems: 'center',
  marginBottom: '20px', position: 'sticky', top: '-28px', zIndex: 10,
  backgroundColor: 'white', paddingTop: '28px', marginTop: '-28px'
};

export const headerTitleGroup = {
  display: 'flex',
  alignItems: 'center',
  gap: '8px'
};

export const headerTitle = {
  fontSize: '1.4rem',
  margin: 0,
  color: 'var(--text-main)'
};

export const closeButton = {
  background: 'none',
  border: 'none',
  cursor: 'pointer',
  color: '#666'
};

export const loadingRow = {
  textAlign: 'center',
  padding: '40px 0',
  color: 'var(--text-muted)',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  gap: '8px'
};

export const emptyBox = {
  textAlign: 'center', padding: '40px 20px',
  backgroundColor: '#F9FAFB', borderRadius: 'var(--border-radius-md)',
  border: '2px dashed #E5E7EB'
};

export const emptyIcon = {
  marginBottom: '8px'
};

export const emptyTitle = {
  color: 'var(--text-main)',
  fontWeight: '600',
  marginBottom: '4px'
};

export const emptySubtitle = {
  color: 'var(--text-muted)',
  fontSize: '0.85rem'
};

export const list = {
  display: 'flex',
  flexDirection: 'column',
  gap: '12px'
};

export const itemRow = {
  display: 'flex',
  gap: '16px',
  padding: '14px',
  backgroundColor: '#F9FAFB',
  borderRadius: 'var(--border-radius-sm)',
  border: '1px solid #E5E7EB',
  alignItems: 'center'
};

export const itemImg = {
  width: '64px',
  height: '64px',
  borderRadius: 'var(--border-radius-sm)',
  objectFit: 'cover'
};

export const itemBody = {
  flex: 1
};

export const itemTitle = {
  margin: '0 0 4px 0',
  fontSize: '1rem',
  color: 'var(--text-main)'
};

export const itemDateRow = {
  display: 'flex',
  alignItems: 'center',
  gap: '6px',
  fontSize: '0.8rem',
  color: 'var(--text-muted)'
};

export const itemNote = {
  margin: '4px 0 0 0',
  fontSize: '0.8rem',
  color: 'var(--primary-dark)',
  fontStyle: 'italic'
};
