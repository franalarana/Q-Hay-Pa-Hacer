import { useMsal, useIsAuthenticated } from '@azure/msal-react';
import { useNavigate } from 'react-router-dom';
import { loginRequest } from '../authConfig';
import { ChefHat, Sparkles, CheckCircle2, AlertTriangle, XCircle, ArrowRight, ShieldCheck, Heart, Utensils, Zap, BookOpen, Carrot, Package, Ban } from 'lucide-react';
import '../styles/landing.css';

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
        <div className="landing-page">
            {/* Navbar */}
            <header className="landing-header">
                <div className="landing-brand-group">
                    <img
                        src="/logo-bowl.png?v=4"
                        alt="Logo"
                        className="landing-logo-img"
                    />
                    <span className="landing-brand-name">
                        ¿Qué hay pa' hacer?
                    </span>
                </div>

                <div className="landing-header-actions">
                    {isAuthenticated && (
                        <button
                            className="btn btn-primary landing-header-cta"
                            onClick={() => navigate('/dashboard')}
                        >
                            Ir a mi Despensa <ArrowRight size={18} />
                        </button>
                    )}
                </div>
            </header>

            {/* Hero Section */}
            <section className="landing-hero">
                <div className="landing-hero-left">


                    <h1 className="landing-hero-title">
                        Cocina delicioso con lo que <br />
                        <span className="landing-hero-title-highlight">
                            ya tienes en tu casa
                        </span>
                    </h1>

                    <p className="landing-hero-subtitle">
                        Registra tus ingredientes y descubre al instante qué cocinar con nuestro semáforo inteligente.
                    </p>

                    <div className="landing-hero-cta-wrapper">
                        <button
                            className="btn btn-primary landing-hero-cta"
                            onClick={handleLogin}
                        >
                            {isAuthenticated ? 'Abrir mi Despensa' : 'Comenzar Ahora Gratis'} <ArrowRight size={22} />
                        </button>
                    </div>
                </div>

                {/* Right Column: Features informative rectangle */}
                <div className="landing-features-panel">
                    <div className="landing-features-header-text">
                        <h3 className="landing-features-title">Todo lo que necesitas en una sola app</h3>
                        <p className="landing-features-subtitle">
                            Diseñado para optimizar tu cocina diaria y evitar el desperdicio.
                        </p>
                    </div>

                    <div className="landing-features-list">
                        {/* Feature 1 */}
                        <div className="landing-feature-item">
                            <div className="landing-feature-icon-wrapper">
                                <img src="/utensils/media_1789267134525.png" alt="Despensa" className="landing-feature-icon-img" />
                            </div>
                            <div>
                                <h4 className="landing-feature-title">Despensa en Tiempo Real</h4>
                                <p className="landing-feature-description">Registra y ajusta cantidades con un solo clic desde tu panel lateral intuitivo.</p>
                            </div>
                        </div>
                        {/* Feature 2 */}
                        <div className="landing-feature-item">
                            <div className="landing-feature-icon-wrapper">
                                <img src="/utensils/media_1789267129889.png" alt="Favoritos" className="landing-feature-icon-img" />
                            </div>
                            <div>
                                <h4 className="landing-feature-title">Recetas Favoritas</h4>
                                <p className="landing-feature-description">Guarda con un corazón las preparaciones que más te gustan para tenerlas siempre a mano.</p>
                            </div>
                        </div>
                        {/* Feature 3 */}
                        <div className="landing-feature-item">
                            <div className="landing-feature-icon-wrapper">
                                <img src="/utensils/media_1789267132580.png" alt="Recetas" className="landing-feature-icon-img" />
                            </div>
                            <div>
                                <h4 className="landing-feature-title">Crea tus Recetas</h4>
                                <p className="landing-feature-description">Publica tus recetas secretas de familia indicando ingredientes, pasos y tiempos.</p>
                            </div>
                        </div>
                    </div>
                    {/* Guante anclado a la esquina inferior derecha del recuadro informativo */}
                    <img src="/utensils/media_1789261651647.png" alt="Guante" className="landing-features-panel-glove" />
                </div>
            </section>

            {/* Demostración del Semáforo Inteligente */}
            <section className="landing-semaforo-section">

                <div className="landing-semaforo-card-outer">
                    {/* Utensilios decorativos pegados a las esquinas de la tarjeta */}
                    <img src="/utensils/media_1789261651571.png" alt="Batidor" className="landing-decorative-whisk" />
                    <img src="/utensils/media_1789261651598.png" alt="Espátula" className="landing-decorative-spatula" />
                    <img src="/utensils/media_1789261651623.png" alt="Sartén" className="landing-decorative-pan" />
                    <div className="landing-semaforo-header">
                        <h2 className="landing-semaforo-title">
                            El Semáforo de Ingredientes
                        </h2>
                        <p className="landing-semaforo-subtitle">
                            Así clasifica el sistema cada receta en tiempo real según lo que guardes en tu despensa.
                        </p>
                    </div>

                    <div className="landing-semaforo-grid">
                        {/* Estado Verde */}
                        <div className="semaforo-card semaforo-card-green landing-semaforo-card">
                            <div className="landing-semaforo-badge landing-semaforo-badge--green">
                                <CheckCircle2 size={16} strokeWidth={3} />
                            </div>
                            <div className="landing-semaforo-icon-circle landing-semaforo-icon-circle--green">
                                <Carrot size={40} />
                            </div>
                            <div>
                                <h3 className="landing-semaforo-card-title landing-semaforo-card-title--green">Verde</h3>
                                <p className="landing-semaforo-card-text landing-semaforo-card-text--green">
                                    Tienes todos los ingredientes en las cantidades necesarias. ¡Puedes empezar a cocinar de inmediato!
                                </p>
                            </div>
                        </div>

                        {/* Estado Amarillo */}
                        <div className="semaforo-card semaforo-card-yellow landing-semaforo-card">
                            <div className="landing-semaforo-badge landing-semaforo-badge--yellow">
                                <AlertTriangle size={16} strokeWidth={3} />
                            </div>
                            <div className="landing-semaforo-icon-circle landing-semaforo-icon-circle--yellow">
                                <Package size={40} />
                            </div>
                            <div>
                                <h3 className="landing-semaforo-card-title landing-semaforo-card-title--yellow">Amarillo</h3>
                                <p className="landing-semaforo-card-text landing-semaforo-card-text--yellow">
                                    Tienes el ingrediente pero la cantidad registrada es menor a la requerida por la porción.
                                </p>
                            </div>
                        </div>

                        {/* Estado Rojo */}
                        <div className="semaforo-card semaforo-card-red landing-semaforo-card">
                            <div className="landing-semaforo-badge landing-semaforo-badge--red">
                                <XCircle size={16} strokeWidth={3} />
                            </div>
                            <div className="landing-semaforo-icon-circle landing-semaforo-icon-circle--red">
                                <Ban size={40} />
                            </div>
                            <div>
                                <h3 className="landing-semaforo-card-title landing-semaforo-card-title--red">Rojo</h3>
                                <p className="landing-semaforo-card-text landing-semaforo-card-text--red">
                                    No tienes este ingrediente en tu despensa. Te indicamos qué comprar si deseas prepararlo.
                                </p>
                            </div>
                        </div>
                    </div>
                </div>

            </section>


            {/* Footer */}
            <footer className="landing-footer">
                <p className="landing-footer-text">
                    Cahuin Lab's &reg; 2026
                </p>
            </footer>
        </div>
    );
}
