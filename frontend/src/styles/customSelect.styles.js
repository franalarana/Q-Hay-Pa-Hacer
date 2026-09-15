export const wrapper = {
  position: 'relative',
  width: '100%'
};

export const getTrigger = (isOpen) => ({
  display: 'flex',
  justifyContent: 'space-between',
  alignItems: 'center',
  borderColor: isOpen ? 'var(--primary-dark)' : '#C0E4DC',
  boxShadow: isOpen ? '0 0 0 3px rgba(108, 175, 161, 0.2)' : '0 2px 4px rgba(0,0,0,0.02)',
  userSelect: 'none'
});

export const getLabel = (selectedOption) => ({
  color: selectedOption ? 'var(--text-main)' : 'var(--text-muted)',
  display: 'flex',
  alignItems: 'center',
  gap: '8px'
});

export const getChevron = (isOpen) => ({
  transform: isOpen ? 'rotate(180deg)' : 'none',
  transition: 'transform 0.2s ease'
});

export const dropdown = {
  position: 'absolute',
  top: '100%',
  left: 0,
  right: 0,
  marginTop: '6px',
  backgroundColor: '#fff',
  borderRadius: '12px',
  border: '1px solid #E5E7EB',
  boxShadow: '0 10px 25px -5px rgba(0, 0, 0, 0.1)',
  maxHeight: '260px',
  overflowY: 'auto',
  zIndex: 50,
  padding: '6px'
};

export const getOption = (opt, value) => ({
  backgroundColor: opt.value === value ? 'var(--pastel-mint)' : 'transparent',
  fontWeight: opt.value === value ? '600' : '400',
  borderBottom: opt.isDivider ? '1px solid #E5E7EB' : 'none',
  marginBottom: opt.isDivider ? '4px' : '0'
});
