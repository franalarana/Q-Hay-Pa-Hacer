import { Circle } from 'lucide-react';
import '../styles/semaforoIcon.css';

const COLORES = { VERDE: '#10B981', AMARILLO: '#F59E0B', ROJO: '#EF4444' };

export default function SemaforoIcon({ estado, size = 10 }) {
  const color = COLORES[estado] || COLORES.ROJO;
  return (
    <Circle
      size={size}
      fill={color}
      color={color}
      className="semaforo-dot"
    />
  );
}
