export const asideRoot = {
  backgroundColor: '#C0E4DC',
  border: '20px solid #82B3A4',
  borderRadius: '44px',
  padding: '24px 20px',
  position: 'relative',
  display: 'flex',
  flexDirection: 'column',
  height: 'fit-content',
  alignSelf: 'flex-start'
};

export const title = {
  fontSize: '1.5rem',
  fontWeight: 700,
  color: '#1F2937',
  marginTop: '10px',
  marginBottom: '20px'
};

export const searchWrapper = {
  position: 'relative',
  marginBottom: '16px'
};

export const searchInput = {
  width: '100%',
  padding: '10px 38px 10px 14px',
  borderRadius: '50px',
  border: 'none',
  outline: 'none',
  fontSize: '0.9rem',
  boxShadow: 'inset 0 1px 3px rgba(0,0,0,0.08)'
};

export const searchIcon = {
  position: 'absolute',
  right: '14px',
  top: '10px'
};

export const addButton = {
  width: '100%',
  marginBottom: '20px',
  backgroundColor: 'white',
  color: 'var(--primary-dark)',
  gap: '6px'
};

export const formPanel = {
  backgroundColor: 'white',
  padding: '16px',
  borderRadius: 'var(--border-radius-md)',
  marginBottom: '20px',
  boxShadow: 'var(--shadow-sm)'
};

export const formHeader = {
  display: 'flex',
  justifyContent: 'space-between',
  alignItems: 'center',
  marginBottom: '12px'
};

export const formHeaderTitle = {
  fontSize: '0.95rem',
  color: 'var(--text-main)'
};

export const closeFormButton = {
  background: 'none',
  border: 'none',
  cursor: 'pointer',
  color: '#888'
};

export const errorBox = {
  display: 'flex',
  alignItems: 'center',
  gap: '6px',
  color: '#EF4444',
  fontSize: '0.8rem',
  marginBottom: '10px'
};

export const form = {
  display: 'flex',
  flexDirection: 'column',
  gap: '10px'
};

export const fieldLabel = {
  fontSize: '0.75rem',
  fontWeight: '600',
  color: 'var(--text-muted)'
};

export const emojiOptionText = {
  fontSize: '1.1rem'
};

export const inlineRow = {
  display: 'flex',
  gap: '8px'
};

export const flexField = {
  flex: 1
};

export const flexFieldWide = {
  flex: 1.4
};

export const flexFieldMedium = {
  flex: 1.5
};

export const formActionsRow = {
  display: 'flex',
  gap: '8px',
  marginTop: '6px'
};

export const submitButton = {
  flex: 1,
  padding: '8px',
  fontSize: '0.85rem'
};

export const cancelButton = {
  padding: '8px 12px',
  fontSize: '0.85rem'
};

export const listWrapper = {
  display: 'flex',
  flexDirection: 'column',
  gap: '8px'
};

export const loadingRow = {
  textAlign: 'center',
  padding: '24px 0',
  color: 'rgba(0,0,0,0.5)',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  gap: '8px'
};

export const emptyListBox = {
  backgroundColor: 'rgba(255,255,255,0.4)',
  borderRadius: 'var(--border-radius-md)',
  padding: '20px',
  textAlign: 'center',
  fontSize: '0.85rem',
  color: 'rgba(0,0,0,0.7)'
};

export const itemRow = {
  backgroundColor: 'rgba(255, 255, 255, 0.75)',
  backdropFilter: 'blur(4px)',
  borderRadius: 'var(--border-radius-sm)',
  padding: '10px 14px',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'space-between',
  boxShadow: '0 2px 4px rgba(0,0,0,0.03)'
};

export const editColumn = {
  display: 'flex',
  flexDirection: 'column',
  gap: '8px',
  width: '100%'
};

export const editNameRow = {
  fontWeight: '600',
  fontSize: '0.9rem',
  color: 'var(--text-main)',
  display: 'flex',
  alignItems: 'center',
  gap: '6px'
};

export const editControlsRow = {
  display: 'flex',
  gap: '6px',
  alignItems: 'center'
};

export const editQuantityInput = {
  padding: '6px 12px',
  width: '90px'
};

export const editUnitWrapper = {
  flex: 1
};

export const roundIconButton = (bgColor, color) => ({
  width: '28px',
  height: '28px',
  borderRadius: '50%',
  border: 'none',
  backgroundColor: bgColor,
  cursor: 'pointer',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  color,
  flexShrink: 0
});

export const itemViewRow = {
  display: 'flex',
  alignItems: 'center',
  gap: '8px'
};

export const itemEmoji = {
  fontSize: '1.25rem'
};

export const itemName = {
  fontWeight: '600',
  fontSize: '0.9rem',
  color: 'var(--text-main)'
};

export const itemQuantity = {
  fontSize: '0.75rem',
  color: 'var(--text-muted)'
};

export const itemActionsRow = {
  display: 'flex',
  alignItems: 'center',
  gap: '6px'
};

export const editIconButton = {
  width: '28px',
  height: '28px',
  borderRadius: '50%',
  border: 'none',
  backgroundColor: 'rgba(0,0,0,0.06)',
  cursor: 'pointer',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center'
};

export const deleteIconButton = {
  width: '28px',
  height: '28px',
  borderRadius: '50%',
  border: 'none',
  backgroundColor: 'rgba(239, 68, 68, 0.08)',
  cursor: 'pointer',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  color: '#DC2626'
};

export const footerCount = {
  marginTop: '16px',
  textAlign: 'center',
  fontSize: '0.75rem',
  color: 'rgba(0,0,0,0.6)'
};
