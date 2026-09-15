import { Circle } from 'lucide-react';
import { iconStyle } from '../styles/semaforoIcon.styles';

const COLORES = { VERDE: '#10B981', AMARILLO: '#F59E0B', ROJO: '#EF4444' };

export default function SemaforoIcon({ estado, size = 10 }) {
  const color = COLORES[estado] || COLORES.ROJO;
  return (
    <Circle
      size={size}
      fill={color}
      color={color}
      style={iconStyle}
    />
  );
}
