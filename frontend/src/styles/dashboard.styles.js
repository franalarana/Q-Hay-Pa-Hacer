export const mainContent = {
  position: 'relative',
  overflow: 'hidden'
};

export const navTabsGroup = {
  display: 'flex',
  alignItems: 'center',
  gap: '22px',
  flexWrap: 'wrap'
};

export const getTabButton = (isActive) => ({
  background: 'none',
  border: 'none',
  cursor: 'pointer',
  color: isActive ? 'var(--primary-dark)' : 'var(--text-main)',
  fontWeight: isActive ? '700' : '500',
  borderBottom: isActive ? '2px solid var(--primary-dark)' : 'none',
  paddingBottom: '4px',
  fontSize: '0.95rem'
});

export const getTabButtonWithIcon = (isActive) => ({
  ...getTabButton(isActive),
  display: 'flex',
  alignItems: 'center',
  gap: '4px'
});

export const historyButton = {
  background: 'none',
  border: 'none',
  cursor: 'pointer',
  color: 'var(--text-main)',
  fontWeight: '500',
  paddingBottom: '4px',
  fontSize: '0.95rem',
  display: 'flex',
  alignItems: 'center',
  gap: '4px'
};

export const createButton = {
  padding: '8px 16px',
  fontSize: '0.85rem',
  gap: '6px'
};

export const userGroup = {
  display: 'flex',
  alignItems: 'center',
  gap: '14px'
};

export const userName = {
  fontSize: '0.9rem',
  color: 'var(--text-muted)',
  maxWidth: '220px',
  overflow: 'hidden',
  textOverflow: 'ellipsis',
  whiteSpace: 'nowrap'
};

export const userAvatar = {
  width: '38px',
  height: '38px',
  borderRadius: '50%',
  backgroundColor: '#E0E0E0',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  flexShrink: 0
};

export const logoutButton = {
  padding: '8px 12px',
  borderRadius: 'var(--border-radius-sm)',
  display: 'flex',
  alignItems: 'center',
  gap: '6px',
  flexShrink: 0
};

export const summaryHeaderRow = {
  display: 'flex',
  justifyContent: 'space-between',
  alignItems: 'flex-start',
  flexWrap: 'wrap',
  gap: '16px',
  marginBottom: '24px'
};

export const summaryTitle = {
  marginBottom: '6px',
  fontSize: '1.9rem',
  display: 'flex',
  alignItems: 'center',
  gap: '10px'
};

export const summarySubtitle = {
  color: 'var(--text-muted)',
  fontSize: '0.95rem',
  display: 'flex',
  alignItems: 'center',
  gap: '6px'
};

export const searchWrapper = {
  position: 'relative',
  minWidth: '260px'
};

export const searchInput = {
  width: '100%',
  padding: '10px 38px 10px 14px',
  borderRadius: '50px',
  border: '1px solid #E5E7EB',
  outline: 'none',
  fontSize: '0.9rem',
  backgroundColor: '#F9FAFB'
};

export const searchIcon = {
  position: 'absolute',
  right: '14px',
  top: '11px'
};

export const filtersRow = {
  display: 'flex',
  gap: '8px',
  flexWrap: 'wrap',
  marginBottom: '28px'
};

export const filterButtonBase = {
  fontSize: '0.85rem',
  padding: '6px 14px'
};

export const getFilterButtonWithIcon = (isActive, activeBg) => ({
  fontSize: '0.85rem',
  padding: '6px 14px',
  gap: '6px',
  backgroundColor: isActive ? activeBg : undefined,
  color: isActive ? 'white' : undefined
});

export const loadingBlock = {
  textAlign: 'center',
  padding: '60px 0',
  color: 'var(--text-muted)',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  gap: '8px'
};

export const emptyBlock = {
  textAlign: 'center',
  padding: '60px 20px',
  backgroundColor: '#F9FAFB',
  borderRadius: 'var(--border-radius-lg)',
  border: '2px dashed #E5E7EB'
};

export const emptyIcon = {
  marginBottom: '12px'
};

export const emptyTitle = {
  color: 'var(--text-main)',
  marginBottom: '8px'
};

export const emptySubtitle = {
  color: 'var(--text-muted)',
  fontSize: '0.9rem'
};

export const inlineHeartIcon = {
  display: 'inline',
  verticalAlign: 'middle'
};

export const recipesGrid = {
  display: 'grid',
  gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))',
  alignItems: 'start',
  gap: '24px',
  marginBottom: '32px'
};

export const backendStatusBar = {
  backgroundColor: '#F9FAFB',
  borderRadius: 'var(--border-radius-sm)',
  padding: '12px 16px',
  fontSize: '0.8rem',
  color: 'var(--text-muted)',
  display: 'flex',
  justifyContent: 'space-between',
  alignItems: 'center',
  borderTop: '1px solid #E5E7EB'
};
