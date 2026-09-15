export const card = {
  backgroundColor: 'white',
  borderRadius: 'var(--border-radius-md)',
  overflow: 'hidden',
  boxShadow: 'var(--shadow-sm)',
  display: 'flex',
  flexDirection: 'column',
  transition: 'transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease',
  cursor: 'pointer',
  position: 'relative'
};

export const imageWrapper = {
  position: 'relative',
  height: '170px',
  width: '100%'
};

export const image = {
  width: '100%',
  height: '100%',
  objectFit: 'cover'
};

export const favoritoButton = {
  position: 'absolute',
  top: '12px',
  left: '12px',
  backgroundColor: 'rgba(255, 255, 255, 0.9)',
  border: 'none',
  borderRadius: '50%',
  width: '32px',
  height: '32px',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  cursor: 'pointer',
  boxShadow: '0 2px 4px rgba(0,0,0,0.15)',
  transition: 'transform 0.2s'
};

export const getBadge = (badgeBg, badgeColor) => ({
  position: 'absolute',
  top: '12px',
  right: '12px',
  backgroundColor: badgeBg,
  color: badgeColor,
  padding: '4px 10px',
  borderRadius: '20px',
  fontSize: '0.75rem',
  fontWeight: '700',
  boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
  display: 'inline-flex',
  alignItems: 'center',
  gap: '5px'
});

export const body = {
  padding: '16px 20px',
  display: 'flex',
  flexDirection: 'column',
  flex: 1
};

export const title = {
  fontSize: '1.15rem',
  color: 'var(--text-main)',
  marginBottom: '16px',
  fontWeight: '700',
  lineHeight: '1.4',
  minHeight: '2.8em',
  display: '-webkit-box',
  WebkitLineClamp: 2,
  WebkitBoxOrient: 'vertical',
  overflow: 'hidden'
};

export const getProgressSection = (isHovered) => ({
  marginBottom: isHovered ? '16px' : '0',
  transition: 'margin 0.3s ease'
});

export const progressLabelRow = {
  display: 'flex',
  justifyContent: 'space-between',
  fontSize: '0.75rem',
  fontWeight: '600',
  marginBottom: '4px'
};

export const progressLabelText = {
  color: 'var(--text-muted)'
};

export const getProgressPercentText = (color) => ({
  color
});

export const progressTrack = {
  width: '100%',
  height: '8px',
  backgroundColor: '#E5E7EB',
  borderRadius: '4px',
  overflow: 'hidden'
};

export const getProgressFill = (percent, color) => ({
  width: `${percent}%`,
  height: '100%',
  backgroundColor: color,
  borderRadius: '4px',
  transition: 'width 0.4s ease'
});

export const getHoverContent = (isHovered) => ({
  maxHeight: isHovered ? '300px' : '0px',
  opacity: isHovered ? 1 : 0,
  overflow: 'hidden',
  transition: 'all 0.3s ease-in-out',
  display: 'flex',
  flexDirection: 'column'
});

export const ingredientBadgesRow = {
  display: 'flex',
  flexWrap: 'wrap',
  gap: '6px',
  marginBottom: '16px',
  marginTop: '16px'
};

export const ingredientBadge = {
  padding: '3px 8px',
  borderRadius: '12px',
  fontSize: '0.75rem',
  display: 'flex',
  alignItems: 'center',
  gap: '4px',
  backgroundColor: '#F3F4F6',
  color: '#4B5563',
  fontWeight: '500'
};

export const footer = {
  marginTop: 'auto',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'space-between',
  paddingTop: '12px',
  borderTop: '1px solid #F3F4F6'
};

export const footerMetaGroup = {
  display: 'flex',
  gap: '12px',
  fontSize: '0.8rem',
  color: 'var(--text-muted)'
};

export const footerMetaItem = {
  display: 'flex',
  alignItems: 'center',
  gap: '4px'
};

export const footerLink = {
  fontSize: '0.85rem',
  color: 'var(--primary-dark)',
  fontWeight: '600',
  display: 'flex',
  alignItems: 'center',
  gap: '4px'
};
