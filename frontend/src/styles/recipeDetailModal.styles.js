export const overlay = {
  position: 'fixed',
  top: 0,
  left: 0,
  right: 0,
  bottom: 0,
  backgroundColor: 'rgba(0, 0, 0, 0.5)',
  backdropFilter: 'blur(4px)',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  zIndex: 1000,
  padding: '20px'
};

export const panel = {
  backgroundColor: 'white',
  borderRadius: 'var(--border-radius-lg)',
  maxWidth: '700px',
  width: '100%',
  maxHeight: '90vh',
  overflowY: 'auto',
  boxShadow: 'var(--shadow-md)',
  position: 'relative',
  display: 'flex',
  flexDirection: 'column'
};

export const imageWrapper = {
  position: 'sticky',
  top: 0,
  zIndex: 10,
  height: '240px',
  width: '100%',
  overflow: 'hidden',
  flexShrink: 0
};

export const image = {
  width: '100%',
  height: '100%',
  objectFit: 'cover'
};

export const roundTopButton = (side) => ({
  position: 'absolute',
  top: '16px',
  [side]: '16px',
  backgroundColor: 'rgba(255, 255, 255, 0.9)',
  border: 'none',
  borderRadius: '50%',
  width: '40px',
  height: '40px',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  cursor: 'pointer',
  boxShadow: '0 2px 6px rgba(0,0,0,0.15)'
});

export const content = {
  padding: '24px 32px'
};

export const titleRow = {
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'space-between',
  flexWrap: 'wrap',
  gap: '12px',
  marginBottom: '8px'
};

export const title = {
  fontSize: '1.6rem',
  color: 'var(--text-main)',
  margin: 0
};

const ESTADO_BADGE_BG = { VERDE: '#DEF7EC', AMARILLO: '#FEF08A', ROJO: '#FDE8E8' };
const ESTADO_BADGE_COLOR = { VERDE: '#03543F', AMARILLO: '#854D0E', ROJO: '#9B1C1C' };

export const getEstadoBadge = (estadoGeneral) => ({
  padding: '4px 12px',
  borderRadius: '20px',
  fontSize: '0.85rem',
  fontWeight: '600',
  display: 'inline-flex',
  alignItems: 'center',
  gap: '6px',
  backgroundColor: ESTADO_BADGE_BG[estadoGeneral] || ESTADO_BADGE_BG.ROJO,
  color: ESTADO_BADGE_COLOR[estadoGeneral] || ESTADO_BADGE_COLOR.ROJO
});

export const ownerActionsRow = {
  display: 'flex',
  gap: '8px',
  marginBottom: '16px'
};

export const editButton = {
  padding: '8px 14px',
  fontSize: '0.85rem',
  gap: '6px'
};

export const deleteButton = {
  padding: '8px 14px',
  fontSize: '0.85rem',
  gap: '6px',
  color: '#DC2626',
  borderColor: '#FECACA'
};

export const description = {
  color: 'var(--text-muted)',
  fontSize: '0.95rem',
  marginBottom: '20px',
  lineHeight: '1.5'
};

export const metadataRow = {
  display: 'flex',
  gap: '20px',
  marginBottom: '24px',
  paddingBottom: '16px',
  borderBottom: '1px solid #E5E7EB'
};

export const metadataItem = {
  display: 'flex',
  alignItems: 'center',
  gap: '6px',
  fontSize: '0.9rem',
  color: 'var(--text-muted)'
};

export const sectionBlock = {
  marginBottom: '28px'
};

export const sectionTitle = {
  fontSize: '1.1rem',
  marginBottom: '12px',
  color: 'var(--text-main)'
};

export const ingredientesList = {
  display: 'flex',
  flexDirection: 'column',
  gap: '8px'
};

export const getIngredienteRow = (isVerde, isAmarillo) => ({
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'space-between',
  padding: '10px 14px',
  borderRadius: 'var(--border-radius-sm)',
  backgroundColor: isVerde ? '#F3F4F6' : isAmarillo ? '#F0F9FF' : '#F9FAFB',
  border: `1px solid ${isVerde ? '#E5E7EB' : isAmarillo ? '#BAE6FD' : '#E5E7EB'}`
});

export const ingredienteNameGroup = {
  display: 'flex',
  alignItems: 'center',
  gap: '8px'
};

export const ingredienteName = {
  fontWeight: '500',
  color: 'var(--text-main)',
  fontSize: '0.9rem'
};

export const ingredienteRightBlock = {
  textAlign: 'right'
};

export const ingredienteRequerido = {
  fontSize: '0.85rem',
  fontWeight: '600',
  color: 'var(--text-main)'
};

export const getIngredienteMensaje = (isVerde, isAmarillo) => ({
  fontSize: '0.75rem',
  color: isVerde ? '#16A34A' : isAmarillo ? '#CA8A04' : '#DC2626'
});

export const instruccionesBox = {
  backgroundColor: '#F9FAFB',
  padding: '16px 20px',
  borderRadius: 'var(--border-radius-sm)',
  fontSize: '0.95rem',
  lineHeight: '1.7',
  whiteSpace: 'pre-line',
  color: '#374151',
  border: '1px solid #E5E7EB'
};

export const cookSection = {
  padding: '20px',
  backgroundColor: '#F0FDF4',
  borderRadius: 'var(--border-radius-md)',
  border: '1px solid #BBF7D0',
  display: 'flex',
  flexDirection: 'column',
  gap: '12px'
};

export const cookHeaderRow = {
  display: 'flex',
  justifyContent: 'space-between',
  alignItems: 'center',
  flexWrap: 'wrap',
  gap: '12px'
};

export const cookHeaderTitle = {
  color: '#166534',
  fontSize: '1rem',
  display: 'flex',
  alignItems: 'center',
  gap: '6px'
};

export const cookHeaderSubtitle = {
  margin: '4px 0 0',
  color: '#15803D',
  fontSize: '0.85rem'
};

export const cookButton = {
  backgroundColor: '#16A34A',
  color: 'white',
  padding: '10px 18px',
  gap: '6px'
};

export const cookedSuccess = {
  color: '#16A34A',
  fontWeight: 'bold',
  display: 'flex',
  alignItems: 'center',
  gap: '6px'
};

export const notasRow = {
  display: 'flex',
  gap: '8px',
  marginTop: '6px'
};

export const notasInput = {
  flex: 1,
  padding: '8px 12px',
  borderRadius: 'var(--border-radius-sm)',
  border: '1px solid #86EFAC',
  fontSize: '0.85rem'
};

export const confirmButton = {
  backgroundColor: '#16A34A',
  color: 'white',
  padding: '8px 14px',
  fontSize: '0.85rem'
};
