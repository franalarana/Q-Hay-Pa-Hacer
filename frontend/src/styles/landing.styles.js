export const page = {
  minHeight: '100vh',
  display: 'flex',
  flexDirection: 'column',
  backgroundColor: 'var(--bg-color)'
};

export const header = {
  padding: '6px 48px',
  display: 'flex',
  justifyContent: 'space-between',
  alignItems: 'center',
  backgroundColor: 'rgba(255, 255, 255, 0.85)',
  backdropFilter: 'blur(10px)',
  position: 'sticky',
  top: 0,
  zIndex: 100,
  borderBottom: '1px solid rgba(0,0,0,0.05)'
};

export const brandGroup = {
  display: 'flex',
  alignItems: 'center',
  gap: '16px'
};

export const logoImg = {
  height: '90px',
  objectFit: 'contain',
  filter: 'drop-shadow(0 3px 2px rgba(0, 0, 0, 0.3))',
  borderRadius: '50%'
};

export const brandName = {
  fontFamily: '"Caveat", cursive',
  fontSize: '2.4rem',
  fontWeight: '700',
  color: '#155D40'
};

export const headerActions = {
  display: 'flex',
  alignItems: 'center',
  gap: '16px'
};

export const headerCtaButton = {
  gap: '8px'
};

export const heroSection = {
  maxWidth: '1350px',
  margin: '0 auto',
  padding: '60px 24px 0',
  display: 'grid',
  gridTemplateColumns: 'repeat(auto-fit, minmax(450px, 1fr))',
  gap: '60px',
  alignItems: 'center',
  position: 'relative'
};

export const heroLeftColumn = {
  display: 'flex',
  flexDirection: 'column',
  alignItems: 'flex-start',
  textAlign: 'left'
};

export const heroTitle = {
  fontSize: 'clamp(3.4rem, 5vw, 4.8rem)',
  fontWeight: '900',
  fontFamily: 'system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif',
  letterSpacing: '-0.03em',
  color: '#2C3E3A',
  lineHeight: '1.1',
  marginBottom: '24px',
  maxWidth: '100%'
};

export const heroTitleHighlight = {
  color: '#58B08B'
};

export const heroSubtitle = {
  fontSize: '1.05rem',
  color: '#64748B',
  lineHeight: '1.6',
  marginBottom: '32px',
  fontWeight: '400',
  letterSpacing: '0.2px'
};

export const heroCtaWrapper = {
  display: 'flex',
  gap: '16px',
  flexWrap: 'wrap',
  justifyContent: 'flex-start'
};

export const heroCtaButton = {
  padding: '16px 40px',
  fontSize: '1.15rem',
  fontWeight: '700',
  gap: '10px',
  borderRadius: '50px',
  boxShadow: '0 10px 25px -5px rgba(16, 185, 129, 0.4)',
  color: '#064E3B'
};

export const featuresPanel = {
  backgroundColor: 'white',
  borderRadius: 'var(--border-radius-lg)',
  padding: '40px',
  boxShadow: 'var(--shadow-lg)',
  border: '1px solid #E5E7EB',
  display: 'flex',
  flexDirection: 'column',
  gap: '28px',
  position: 'relative'
};

export const featuresPanelHeaderText = {
  textAlign: 'left'
};

export const featuresPanelTitle = {
  fontSize: '1.5rem',
  color: '#1E293B',
  marginBottom: '6px',
  fontWeight: '800'
};

export const featuresPanelSubtitle = {
  color: 'var(--text-muted)',
  fontSize: '0.95rem',
  margin: 0
};

export const featuresList = {
  display: 'flex',
  flexDirection: 'column',
  gap: '20px'
};

export const featureItem = {
  display: 'flex',
  gap: '20px',
  alignItems: 'center',
  backgroundColor: '#F8FAFC',
  padding: '20px',
  borderRadius: '16px',
  border: '1px solid #F1F5F9',
  textAlign: 'left',
  transition: 'all 0.2s ease'
};

export const featureIconWrapper = {
  flexShrink: 0,
  width: '56px',
  height: '56px',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center'
};

export const featureIconImg = {
  width: '100%',
  height: '100%',
  objectFit: 'contain',
  transition: 'all 0.2s ease'
};

export const featureTitle = {
  margin: '0 0 6px',
  fontSize: '1.15rem',
  color: '#1E293B',
  fontWeight: '700'
};

export const featureDescription = {
  margin: 0,
  fontSize: '0.95rem',
  color: '#4A5568',
  lineHeight: '1.5'
};

export const featuresPanelGlove = {
  position: 'absolute',
  bottom: '-40px',
  right: '-30px',
  transform: 'rotate(20deg)',
  width: '100px',
  zIndex: 10,
  pointerEvents: 'none'
};

export const semaforoSection = {
  maxWidth: '1100px',
  margin: '100px auto 80px',
  padding: '0 24px',
  width: '100%',
  position: 'relative'
};

export const semaforoCardOuter = {
  backgroundColor: 'white',
  borderRadius: 'var(--border-radius-lg)',
  padding: '40px',
  boxShadow: 'var(--shadow-md)',
  border: '1px solid #E5E7EB',
  position: 'relative'
};

export const decorativeWhisk = {
  position: 'absolute',
  top: '-40px',
  left: '-40px',
  transform: 'rotate(-25deg)',
  width: '110px',
  zIndex: 10,
  pointerEvents: 'none'
};

export const decorativeSpatula = {
  position: 'absolute',
  bottom: '-40px',
  left: '-30px',
  transform: 'rotate(15deg)',
  width: '90px',
  zIndex: 10,
  pointerEvents: 'none'
};

export const decorativePan = {
  position: 'absolute',
  bottom: '-50px',
  right: '-50px',
  transform: 'rotate(-20deg)',
  width: '140px',
  zIndex: 10,
  pointerEvents: 'none'
};

export const semaforoHeader = {
  textAlign: 'center',
  marginBottom: '36px'
};

export const semaforoTitle = {
  fontSize: '1.8rem',
  color: '#1E293B',
  marginBottom: '8px',
  fontWeight: '800'
};

export const semaforoSubtitle = {
  color: '#4A5568',
  fontSize: '0.95rem'
};

export const semaforoGrid = {
  display: 'grid',
  gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))',
  gap: '24px'
};

export const semaforoCard = {
  backgroundColor: '#FDFBF7',
  borderRadius: '16px',
  padding: '24px',
  display: 'flex',
  flexDirection: 'column',
  alignItems: 'center',
  gap: '16px',
  position: 'relative'
};

export const semaforoBadge = (bgColor) => ({
  position: 'absolute',
  top: '16px',
  right: '16px',
  backgroundColor: bgColor,
  padding: '4px',
  borderRadius: '50%',
  color: 'white',
  display: 'flex'
});

export const semaforoIconCircle = (bgColor, color) => ({
  width: '80px',
  height: '80px',
  backgroundColor: bgColor,
  borderRadius: '50%',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  color
});

export const semaforoCardTitle = (color) => ({
  margin: 0,
  fontSize: '1.2rem',
  color,
  textAlign: 'center',
  fontWeight: '700',
  marginBottom: '8px'
});

export const semaforoCardText = (color) => ({
  margin: 0,
  fontSize: '0.85rem',
  color,
  lineHeight: '1.5',
  textAlign: 'center'
});

export const footer = {
  marginTop: 'auto',
  padding: '32px 24px',
  textAlign: 'center',
  backgroundColor: '#e5e9eed3',
  borderTop: '1px solid #F1Ece4',
  color: '#b69567ff',
  fontSize: '0.85rem'
};

export const footerText = {
  margin: 0,
  fontWeight: '3700',
  color: 'grey'
};
