import { useMsal, useIsAuthenticated } from '@azure/msal-react';
import { useNavigate } from 'react-router-dom';
import { loginRequest } from '../authConfig';
import { ChefHat, Sparkles, CheckCircle2, AlertTriangle, XCircle, ArrowRight, ShieldCheck, Heart, Utensils, Zap, BookOpen, Carrot, Package, Ban } from 'lucide-react';

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
                <div style={{ display: 'flex', alignItems: 'center', gap: '14px', color: 'var(--primary-dark)', fontWeight: '800', fontSize: '2rem', fontFamily: '"Nunito", "Quicksand", "Arial Rounded MT Bold", sans-serif', letterSpacing: '-0.5px' }}>
                    <div style={{
                        backgroundColor: 'var(--primary-color)',
                        padding: '10px',
                        borderRadius: '16px',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        color: 'white',
                        boxShadow: '0 4px 10px rgba(16, 185, 129, 0.3)'
                    }}>
                        <ChefHat size={36} />
                    </div>
                    <span>¿Qué hay pa' hacer?</span>
                </div>

                <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
                    {isAuthenticated && (
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
                maxWidth: '1350px',
                margin: '0 auto',
                padding: '60px 24px 0',
                display: 'grid',
                gridTemplateColumns: 'repeat(auto-fit, minmax(450px, 1fr))',
                gap: '60px',
                alignItems: 'center',
                position: 'relative'
            }}>
                <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-start', textAlign: 'left' }}>


                    <h1 style={{
                        fontSize: 'clamp(3.4rem, 5vw, 4.8rem)',
                        fontWeight: '900',
                        fontFamily: 'system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif',
                        letterSpacing: '-0.03em',
                        color: '#2C3E3A',
                        lineHeight: '1.1',
                        marginBottom: '24px',
                        maxWidth: '100%'
                    }}>
                        Cocina delicioso con lo que <br />
                        <span style={{ color: '#58B08B' }}>
                            ya tienes en tu casa
                        </span>
                    </h1>

                    <p style={{
                        fontSize: '1.05rem',
                        color: '#64748B',
                        lineHeight: '1.6',
                        marginBottom: '32px',
                        fontWeight: '400',
                        letterSpacing: '0.2px'
                    }}>
                        Registra tus ingredientes y descubre al instante qué cocinar con nuestro semáforo inteligente.
                    </p>

                    <div style={{ display: 'flex', gap: '16px', flexWrap: 'wrap', justifyContent: 'flex-start' }}>
                        <button
                            className="btn btn-primary"
                            onClick={handleLogin}
                            style={{ padding: '16px 40px', fontSize: '1.15rem', fontWeight: '700', gap: '10px', borderRadius: '50px', boxShadow: '0 10px 25px -5px rgba(16, 185, 129, 0.4)', color: '#064E3B' }}
                        >
                            {isAuthenticated ? 'Abrir mi Despensa' : 'Comenzar Ahora Gratis'} <ArrowRight size={22} />
                        </button>
                    </div>
                </div>

                {/* Right Column: Features informative rectangle */}
                <div style={{
                    backgroundColor: 'white',
                    borderRadius: 'var(--border-radius-lg)',
                    padding: '40px',
                    boxShadow: 'var(--shadow-lg)',
                    border: '1px solid #E5E7EB',
                    display: 'flex',
                    flexDirection: 'column',
                    gap: '28px',
                    position: 'relative'
                }}>
                    <div style={{ textAlign: 'left' }}>
                        <h3 style={{ fontSize: '1.5rem', color: '#1E293B', marginBottom: '6px', fontWeight: '800' }}>Todo lo que necesitas en una sola app</h3>
                        <p style={{ color: 'var(--text-muted)', fontSize: '0.95rem', margin: 0 }}>
                            Diseñado para optimizar tu cocina diaria y evitar el desperdicio.
                        </p>
                    </div>

                    <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
                        {/* Feature 1 */}
                        <div style={{ display: 'flex', gap: '20px', alignItems: 'center', backgroundColor: '#F8FAFC', padding: '20px', borderRadius: '16px', border: '1px solid #F1F5F9', textAlign: 'left', transition: 'all 0.2s ease' }}>
                            <div style={{ flexShrink: 0, width: '56px', height: '56px', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                                <img src="/utensils/media_1789267134525.png" alt="Despensa" style={{ width: '100%', height: '100%', objectFit: 'contain', transition: 'all 0.2s ease' }} />
                            </div>
                            <div>
                                <h4 style={{ margin: '0 0 6px', fontSize: '1.15rem', color: '#1E293B', fontWeight: '700' }}>Despensa en Tiempo Real</h4>
                                <p style={{ margin: 0, fontSize: '0.95rem', color: '#4A5568', lineHeight: '1.5' }}>Registra y ajusta cantidades con un solo clic (+/-) desde tu panel lateral intuitivo.</p>
                            </div>
                        </div>
                        {/* Feature 2 */}
                        <div style={{ display: 'flex', gap: '20px', alignItems: 'center', backgroundColor: '#F8FAFC', padding: '20px', borderRadius: '16px', border: '1px solid #F1F5F9', textAlign: 'left', transition: 'all 0.2s ease' }}>
                            <div style={{ flexShrink: 0, width: '56px', height: '56px', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                                <img src="/utensils/media_1789267129889.png" alt="Favoritos" style={{ width: '100%', height: '100%', objectFit: 'contain', transition: 'all 0.2s ease' }} />
                            </div>
                            <div>
                                <h4 style={{ margin: '0 0 6px', fontSize: '1.15rem', color: '#1E293B', fontWeight: '700' }}>Recetas Favoritas</h4>
                                <p style={{ margin: 0, fontSize: '0.95rem', color: '#4A5568', lineHeight: '1.5' }}>Guarda con un corazón las preparaciones que más te gustan para tenerlas siempre a mano.</p>
                            </div>
                        </div>
                        {/* Feature 3 */}
                        <div style={{ display: 'flex', gap: '20px', alignItems: 'center', backgroundColor: '#F8FAFC', padding: '20px', borderRadius: '16px', border: '1px solid #F1F5F9', textAlign: 'left', transition: 'all 0.2s ease' }}>
                            <div style={{ flexShrink: 0, width: '56px', height: '56px', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                                <img src="/utensils/media_1789267132580.png" alt="Recetas" style={{ width: '100%', height: '100%', objectFit: 'contain', transition: 'all 0.2s ease' }} />
                            </div>
                            <div>
                                <h4 style={{ margin: '0 0 6px', fontSize: '1.15rem', color: '#1E293B', fontWeight: '700' }}>Crea tus Recetas</h4>
                                <p style={{ margin: 0, fontSize: '0.95rem', color: '#4A5568', lineHeight: '1.5' }}>Publica tus recetas secretas de familia indicando ingredientes, pasos y tiempos.</p>
                            </div>
                        </div>
                    </div>
                    {/* Guante anclado a la esquina inferior derecha del recuadro informativo */}
                    <img src="/utensils/media_1789261651647.png" alt="Guante" style={{ position: 'absolute', bottom: '-40px', right: '-30px', transform: 'rotate(20deg)', width: '100px', zIndex: 10, pointerEvents: 'none' }} />
                </div>
            </section>

            {/* Demostración del Semáforo Inteligente */}
            <section style={{
                maxWidth: '1100px',
                margin: '100px auto 80px',
                padding: '0 24px',
                width: '100%',
                position: 'relative'
            }}>

                <div style={{
                    backgroundColor: 'white',
                    borderRadius: 'var(--border-radius-lg)',
                    padding: '40px',
                    boxShadow: 'var(--shadow-md)',
                    border: '1px solid #E5E7EB',
                    position: 'relative'
                }}>
                    {/* Utensilios decorativos pegados a las esquinas de la tarjeta */}
                    <img src="/utensils/media_1789261651571.png" alt="Batidor" style={{ position: 'absolute', top: '-40px', left: '-40px', transform: 'rotate(-25deg)', width: '110px', zIndex: 10, pointerEvents: 'none' }} />
                    <img src="/utensils/media_1789261651598.png" alt="Espátula" style={{ position: 'absolute', bottom: '-40px', left: '-30px', transform: 'rotate(15deg)', width: '90px', zIndex: 10, pointerEvents: 'none' }} />
                    <img src="/utensils/media_1789261651623.png" alt="Sartén" style={{ position: 'absolute', bottom: '-50px', right: '-50px', transform: 'rotate(-20deg)', width: '140px', zIndex: 10, pointerEvents: 'none' }} />
                    <div style={{ textAlign: 'center', marginBottom: '36px' }}>
                        <h2 style={{ fontSize: '1.8rem', color: '#1E293B', marginBottom: '8px', fontWeight: '800' }}>
                            El Semáforo de Ingredientes
                        </h2>
                        <p style={{ color: '#4A5568', fontSize: '0.95rem' }}>
                            Así clasifica el sistema cada receta en tiempo real según lo que guardes en tu despensa.
                        </p>
                    </div>

                    <div style={{
                        display: 'grid',
                        gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))',
                        gap: '24px'
                    }}>
                        {/* Estado Verde */}
                        <div className="semaforo-card semaforo-card-green" style={{
                            backgroundColor: '#FDFBF7',
                            borderRadius: '16px',
                            padding: '24px',
                            display: 'flex',
                            flexDirection: 'column',
                            alignItems: 'center',
                            gap: '16px',
                            position: 'relative'
                        }}>
                            <div style={{ position: 'absolute', top: '16px', right: '16px', backgroundColor: '#34D399', padding: '4px', borderRadius: '50%', color: 'white', display: 'flex' }}>
                                <CheckCircle2 size={16} strokeWidth={3} />
                            </div>
                            <div style={{ width: '80px', height: '80px', backgroundColor: '#EDF9F1', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#34D399' }}>
                                <Carrot size={40} />
                            </div>
                            <div>
                                <h3 style={{ margin: 0, fontSize: '1.2rem', color: '#064E3B', textAlign: 'center', fontWeight: '700', marginBottom: '8px' }}>Verde</h3>
                                <p style={{ margin: 0, fontSize: '0.85rem', color: '#064E3B', lineHeight: '1.5', textAlign: 'center' }}>
                                    Tienes todos los ingredientes en las cantidades necesarias. ¡Puedes empezar a cocinar de inmediato!
                                </p>
                            </div>
                        </div>

                        {/* Estado Amarillo */}
                        <div className="semaforo-card semaforo-card-yellow" style={{
                            backgroundColor: '#FDFBF7',
                            borderRadius: '16px',
                            padding: '24px',
                            display: 'flex',
                            flexDirection: 'column',
                            alignItems: 'center',
                            gap: '16px',
                            position: 'relative'
                        }}>
                            <div style={{ position: 'absolute', top: '16px', right: '16px', backgroundColor: '#FACC15', padding: '4px', borderRadius: '50%', color: 'white', display: 'flex' }}>
                                <AlertTriangle size={16} strokeWidth={3} />
                            </div>
                            <div style={{ width: '80px', height: '80px', backgroundColor: '#FEF9C3', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#FACC15' }}>
                                <Package size={40} />
                            </div>
                            <div>
                                <h3 style={{ margin: 0, fontSize: '1.2rem', color: '#713F12', textAlign: 'center', fontWeight: '700', marginBottom: '8px' }}>Amarillo</h3>
                                <p style={{ margin: 0, fontSize: '0.85rem', color: '#713F12', lineHeight: '1.5', textAlign: 'center' }}>
                                    Tienes el ingrediente pero la cantidad registrada es menor a la requerida por la porción.
                                </p>
                            </div>
                        </div>

                        {/* Estado Rojo */}
                        <div className="semaforo-card semaforo-card-red" style={{
                            backgroundColor: '#FDFBF7',
                            borderRadius: '16px',
                            padding: '24px',
                            display: 'flex',
                            flexDirection: 'column',
                            alignItems: 'center',
                            gap: '16px',
                            position: 'relative'
                        }}>
                            <div style={{ position: 'absolute', top: '16px', right: '16px', backgroundColor: '#F87171', padding: '4px', borderRadius: '50%', color: 'white', display: 'flex' }}>
                                <XCircle size={16} strokeWidth={3} />
                            </div>
                            <div style={{ width: '80px', height: '80px', backgroundColor: '#FEE2E2', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#F87171' }}>
                                <Ban size={40} />
                            </div>
                            <div>
                                <h3 style={{ margin: 0, fontSize: '1.2rem', color: '#7F1D1D', textAlign: 'center', fontWeight: '700', marginBottom: '8px' }}>Rojo</h3>
                                <p style={{ margin: 0, fontSize: '0.85rem', color: '#7F1D1D', lineHeight: '1.5', textAlign: 'center' }}>
                                    No tienes este ingrediente en tu despensa. Te indicamos qué comprar si deseas prepararlo.
                                </p>
                            </div>
                        </div>
                    </div>
                </div>

            </section>


            {/* Footer */}
            <footer style={{
                marginTop: 'auto',
                padding: '32px 24px',
                textAlign: 'center',
                backgroundColor: '#e5e9eed3',
                borderTop: '1px solid #F1Ece4',
                color: '#b69567ff',
                fontSize: '0.85rem'
            }}>
                <p style={{ margin: 0, fontWeight: '3700', color: 'grey' }}>
                    Cahuin Lab's &reg; 2026
                </p>
            </footer>
        </div>
    );
}
