import { useMsal, useIsAuthenticated } from '@azure/msal-react';
import { useNavigate } from 'react-router-dom';
import { loginRequest } from '../authConfig';
import { ChefHat, Sparkles, CheckCircle2, AlertTriangle, XCircle, ArrowRight, ShieldCheck, Heart, Utensils, Zap, BookOpen } from 'lucide-react';

export default function Landing() {
    const { instance } = useMsal();
    const isAuthenticated = useIsAuthenticated();
    const navigate = useNavigate();

    const handleLogin = () => {
        if (isAuthenticated) {
            navigate('/dashboard');
        } else {
            instance.loginRedirect(loginRequest).catch(e => {
                console.error(e);
            });
        }
    };

    return (
        <div style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column', backgroundColor: 'var(--bg-color)' }}>
            {/* Navbar */}
            <header style={{
                padding: '20px 48px',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                backgroundColor: 'rgba(255, 255, 255, 0.85)',
                backdropFilter: 'blur(10px)',
                position: 'sticky',
                top: 0,
                zIndex: 100,
                borderBottom: '1px solid rgba(0,0,0,0.05)'
            }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '10px', color: 'var(--primary-dark)', fontWeight: '800', fontSize: '1.3rem' }}>
                    <div style={{
                        backgroundColor: 'var(--primary-color)',
                        padding: '8px',
                        borderRadius: '12px',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        color: 'white'
                    }}>
                        <ChefHat size={24} />
                    </div>
                    <span>¿Qué hay pa' hacer?</span>
                </div>

                <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
                    <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>EP1 DSY1107</span>
                    {!isAuthenticated ? (
                        <button 
                            className="btn btn-primary" 
                            onClick={handleLogin}
                            style={{ gap: '8px' }}
                        >
                            <ShieldCheck size={18} /> Iniciar Sesión con Entra ID
                        </button>
                    ) : (
                        <button 
                            className="btn btn-primary" 
                            onClick={() => navigate('/dashboard')}
                            style={{ gap: '8px' }}
                        >
                            Ir a mi Despensa <ArrowRight size={18} />
                        </button>
                    )}
                </div>
            </header>

            {/* Hero Section */}
            <section style={{
                padding: '80px 24px 60px',
                textAlign: 'center',
                maxWidth: '900px',
                margin: '0 auto',
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center'
            }}>
                <div style={{
                    display: 'inline-flex',
                    alignItems: 'center',
                    gap: '8px',
                    backgroundColor: 'var(--pastel-mint)',
                    color: 'var(--primary-dark)',
                    padding: '8px 16px',
                    borderRadius: '30px',
                    fontSize: '0.85rem',
                    fontWeight: '700',
                    marginBottom: '24px',
                    boxShadow: 'var(--shadow-sm)'
                }}>
                    <Sparkles size={16} /> Tu Despensa Inteligente y Recetas al Instante
                </div>

                <h1 style={{
                    fontSize: 'clamp(2.5rem, 5vw, 4rem)',
                    fontWeight: '800',
                    color: 'var(--text-main)',
                    lineHeight: '1.15',
                    marginBottom: '20px'
                }}>
                    Cocina delicioso con lo que <br />
                    <span style={{
                        background: 'linear-gradient(135deg, var(--primary-dark) 0%, #10B981 100%)',
                        WebkitBackgroundClip: 'text',
                        WebkitTextFillColor: 'transparent'
                    }}>
                        ya tienes en tu casa
                    </span>
                </h1>

                <p style={{
                    fontSize: '1.2rem',
                    color: 'var(--text-muted)',
                    maxWidth: '650px',
                    lineHeight: '1.6',
                    marginBottom: '36px'
                }}>
                    Registra tus ingredientes y cantidades. Nuestro motor compara automáticamente contra cientos de preparaciones y te muestra con un <strong>semáforo inteligente</strong> qué puedes cocinar hoy.
                </p>

                <div style={{ display: 'flex', gap: '16px', flexWrap: 'wrap', justifyContent: 'center' }}>
                    <button 
                        className="btn btn-primary" 
                        onClick={handleLogin}
                        style={{ padding: '16px 36px', fontSize: '1.1rem', gap: '8px' }}
                    >
                        {isAuthenticated ? 'Abrir mi Despensa' : 'Comenzar Ahora Gratis'} <ArrowRight size={20} />
                    </button>
                </div>
            </section>

            {/* Demostración del Semáforo Inteligente */}
            <section style={{
                maxWidth: '1100px',
                margin: '0 auto 80px',
                padding: '0 24px',
                width: '100%'
            }}>
                <div style={{
                    backgroundColor: 'white',
                    borderRadius: 'var(--border-radius-lg)',
                    padding: '40px',
                    boxShadow: 'var(--shadow-md)',
                    border: '1px solid #E5E7EB'
                }}>
                    <div style={{ textAlign: 'center', marginBottom: '36px' }}>
                        <h2 style={{ fontSize: '1.8rem', color: 'var(--text-main)', marginBottom: '8px' }}>
                            El Semáforo de Ingredientes
                        </h2>
                        <p style={{ color: 'var(--text-muted)', fontSize: '0.95rem' }}>
                            Así clasifica el sistema cada receta en tiempo real según lo que guardes en tu despensa:
                        </p>
                    </div>

                    <div style={{
                        display: 'grid',
                        gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))',
                        gap: '24px'
                    }}>
                        {/* Estado Verde */}
                        <div style={{
                            backgroundColor: '#F0FDF4',
                            border: '2px solid #BBF7D0',
                            borderRadius: 'var(--border-radius-md)',
                            padding: '24px',
                            display: 'flex',
                            flexDirection: 'column',
                            gap: '12px'
                        }}>
                            <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                                <div style={{ backgroundColor: '#DEF7EC', padding: '10px', borderRadius: '50%', color: '#16A34A' }}>
                                    <CheckCircle2 size={24} />
                                </div>
                                <div>
                                    <h3 style={{ margin: 0, fontSize: '1.1rem', color: '#166534' }}>🟢 Verde: ¡Listo!</h3>
                                    <span style={{ fontSize: '0.8rem', color: '#15803D' }}>Cantidad suficiente</span>
                                </div>
                            </div>
                            <p style={{ margin: 0, fontSize: '0.9rem', color: '#166534', lineHeight: '1.4' }}>
                                Tienes todos los ingredientes en las cantidades necesarias. ¡Puedes empezar a cocinar de inmediato!
                            </p>
                        </div>

                        {/* Estado Amarillo */}
                        <div style={{
                            backgroundColor: '#FEFCE8',
                            border: '2px solid #FEF08A',
                            borderRadius: 'var(--border-radius-md)',
                            padding: '24px',
                            display: 'flex',
                            flexDirection: 'column',
                            gap: '12px'
                        }}>
                            <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                                <div style={{ backgroundColor: '#FEF08A', padding: '10px', borderRadius: '50%', color: '#CA8A04' }}>
                                    <AlertTriangle size={24} />
                                </div>
                                <div>
                                    <h3 style={{ margin: 0, fontSize: '1.1rem', color: '#854D0E' }}>🟡 Amarillo: Falta poco</h3>
                                    <span style={{ fontSize: '0.8rem', color: '#A16207' }}>Faltan cantidades</span>
                                </div>
                            </div>
                            <p style={{ margin: 0, fontSize: '0.9rem', color: '#854D0E', lineHeight: '1.4' }}>
                                Tienes el ingrediente pero la cantidad registrada es menor a la requerida por la porción.
                            </p>
                        </div>

                        {/* Estado Rojo */}
                        <div style={{
                            backgroundColor: '#FEF2F2',
                            border: '2px solid #FECACA',
                            borderRadius: 'var(--border-radius-md)',
                            padding: '24px',
                            display: 'flex',
                            flexDirection: 'column',
                            gap: '12px'
                        }}>
                            <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                                <div style={{ backgroundColor: '#FDE8E8', padding: '10px', borderRadius: '50%', color: '#DC2626' }}>
                                    <XCircle size={24} />
                                </div>
                                <div>
                                    <h3 style={{ margin: 0, fontSize: '1.1rem', color: '#991B1B' }}>🔴 Rojo: No disponible</h3>
                                    <span style={{ fontSize: '0.8rem', color: '#B91C1C' }}>No está en despensa</span>
                                </div>
                            </div>
                            <p style={{ margin: 0, fontSize: '0.9rem', color: '#991B1B', lineHeight: '1.4' }}>
                                No tienes este ingrediente en tu despensa. Te indicamos qué comprar si deseas prepararlo.
                            </p>
                        </div>
                    </div>
                </div>
            </section>

            {/* Características clave */}
            <section style={{
                maxWidth: '1100px',
                margin: '0 auto 80px',
                padding: '0 24px',
                width: '100%'
            }}>
                <div style={{ textAlign: 'center', marginBottom: '48px' }}>
                    <h2 style={{ fontSize: '2rem', color: 'var(--text-main)', marginBottom: '8px' }}>
                        Todo lo que necesitas en una sola app
                    </h2>
                    <p style={{ color: 'var(--text-muted)' }}>
                        Diseñado para optimizar tu cocina diaria y evitar el desperdicio.
                    </p>
                </div>

                <div style={{
                    display: 'grid',
                    gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))',
                    gap: '24px'
                }}>
                    <div style={{ backgroundColor: 'white', padding: '28px', borderRadius: 'var(--border-radius-md)', boxShadow: 'var(--shadow-sm)', border: '1px solid #E5E7EB' }}>
                        <div style={{ backgroundColor: 'var(--pastel-mint)', color: 'var(--primary-dark)', width: '48px', height: '48px', borderRadius: '12px', display: 'flex', alignItems: 'center', justifyContent: 'center', marginBottom: '16px' }}>
                            <Utensils size={24} />
                        </div>
                        <h3 style={{ fontSize: '1.15rem', marginBottom: '8px', color: 'var(--text-main)' }}>Despensa en Tiempo Real</h3>
                        <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', lineHeight: '1.5' }}>
                            Registra y ajusta cantidades con un solo clic (+/-) desde tu panel lateral intuitivo.
                        </p>
                    </div>

                    <div style={{ backgroundColor: 'white', padding: '28px', borderRadius: 'var(--border-radius-md)', boxShadow: 'var(--shadow-sm)', border: '1px solid #E5E7EB' }}>
                        <div style={{ backgroundColor: '#FEE2E2', color: '#EF4444', width: '48px', height: '48px', borderRadius: '12px', display: 'flex', alignItems: 'center', justifyContent: 'center', marginBottom: '16px' }}>
                            <Heart size={24} />
                        </div>
                        <h3 style={{ fontSize: '1.15rem', marginBottom: '8px', color: 'var(--text-main)' }}>Recetas Favoritas</h3>
                        <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', lineHeight: '1.5' }}>
                            Guarda con un corazón las preparaciones que más te gustan para tenerlas siempre a mano.
                        </p>
                    </div>

                    <div style={{ backgroundColor: 'white', padding: '28px', borderRadius: 'var(--border-radius-md)', boxShadow: 'var(--shadow-sm)', border: '1px solid #E5E7EB' }}>
                        <div style={{ backgroundColor: '#EDE9FE', color: '#8B5CF6', width: '48px', height: '48px', borderRadius: '12px', display: 'flex', alignItems: 'center', justifyContent: 'center', marginBottom: '16px' }}>
                            <ChefHat size={24} />
                        </div>
                        <h3 style={{ fontSize: '1.15rem', marginBottom: '8px', color: 'var(--text-main)' }}>Crea tus Recetas</h3>
                        <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', lineHeight: '1.5' }}>
                            Publica tus recetas secretas de familia indicando ingredientes, pasos y tiempos.
                        </p>
                    </div>

                    <div style={{ backgroundColor: 'white', padding: '28px', borderRadius: 'var(--border-radius-md)', boxShadow: 'var(--shadow-sm)', border: '1px solid #E5E7EB' }}>
                        <div style={{ backgroundColor: '#E0F2FE', color: '#0284C7', width: '48px', height: '48px', borderRadius: '12px', display: 'flex', alignItems: 'center', justifyContent: 'center', marginBottom: '16px' }}>
                            <ShieldCheck size={24} />
                        </div>
                        <h3 style={{ fontSize: '1.15rem', marginBottom: '8px', color: 'var(--text-main)' }}>Microsoft Entra ID</h3>
                        <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', lineHeight: '1.5' }}>
                            Autenticación empresarial con MSAL, tokens JWT seguros y validación en BFF backend.
                        </p>
                    </div>
                </div>
            </section>

            {/* Footer */}
            <footer style={{
                marginTop: 'auto',
                padding: '32px 24px',
                textAlign: 'center',
                backgroundColor: 'white',
                borderTop: '1px solid #E5E7EB',
                color: 'var(--text-muted)',
                fontSize: '0.85rem'
            }}>
                <p style={{ margin: '0 0 6px 0', fontWeight: '600', color: 'var(--text-main)' }}>
                    ¿Qué hay pa' hacer? — Evaluación Práctica 1 (EP1 DSY1107)
                </p>
                <p style={{ margin: 0 }}>
                    Desarrollado con React 19, Spring Boot (Java 21), MySQL, Azure Entra ID y AWS.
                </p>
            </footer>
        </div>
    );
}
