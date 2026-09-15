import { useMsal, useIsAuthenticated } from '@azure/msal-react';
import { useNavigate } from 'react-router-dom';
import { loginRequest } from '../authConfig';
import { ChefHat, Sparkles, CheckCircle2, AlertTriangle, XCircle, ArrowRight, ShieldCheck, Heart, Utensils, Zap, BookOpen, Carrot, Package, Ban } from 'lucide-react';
import * as styles from '../styles/landing.styles';

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
        <div style={styles.page}>
            {/* Navbar */}
            <header style={styles.header}>
                <div style={styles.brandGroup}>
                    <img
                        src="/logo-bowl.png?v=4"
                        alt="Logo"
                        style={styles.logoImg}
                    />
                    <span style={styles.brandName}>
                        ¿Qué hay pa' hacer?
                    </span>
                </div>

                <div style={styles.headerActions}>
                    {isAuthenticated && (
                        <button
                            className="btn btn-primary"
                            onClick={() => navigate('/dashboard')}
                            style={styles.headerCtaButton}
                        >
                            Ir a mi Despensa <ArrowRight size={18} />
                        </button>
                    )}
                </div>
            </header>

            {/* Hero Section */}
            <section style={styles.heroSection}>
                <div style={styles.heroLeftColumn}>


                    <h1 style={styles.heroTitle}>
                        Cocina delicioso con lo que <br />
                        <span style={styles.heroTitleHighlight}>
                            ya tienes en tu casa
                        </span>
                    </h1>

                    <p style={styles.heroSubtitle}>
                        Registra tus ingredientes y descubre al instante qué cocinar con nuestro semáforo inteligente.
                    </p>

                    <div style={styles.heroCtaWrapper}>
                        <button
                            className="btn btn-primary"
                            onClick={handleLogin}
                            style={styles.heroCtaButton}
                        >
                            {isAuthenticated ? 'Abrir mi Despensa' : 'Comenzar Ahora Gratis'} <ArrowRight size={22} />
                        </button>
                    </div>
                </div>

                {/* Right Column: Features informative rectangle */}
                <div style={styles.featuresPanel}>
                    <div style={styles.featuresPanelHeaderText}>
                        <h3 style={styles.featuresPanelTitle}>Todo lo que necesitas en una sola app</h3>
                        <p style={styles.featuresPanelSubtitle}>
                            Diseñado para optimizar tu cocina diaria y evitar el desperdicio.
                        </p>
                    </div>

                    <div style={styles.featuresList}>
                        {/* Feature 1 */}
                        <div style={styles.featureItem}>
                            <div style={styles.featureIconWrapper}>
                                <img src="/utensils/media_1789267134525.png" alt="Despensa" style={styles.featureIconImg} />
                            </div>
                            <div>
                                <h4 style={styles.featureTitle}>Despensa en Tiempo Real</h4>
                                <p style={styles.featureDescription}>Registra y ajusta cantidades con un solo clic desde tu panel lateral intuitivo.</p>
                            </div>
                        </div>
                        {/* Feature 2 */}
                        <div style={styles.featureItem}>
                            <div style={styles.featureIconWrapper}>
                                <img src="/utensils/media_1789267129889.png" alt="Favoritos" style={styles.featureIconImg} />
                            </div>
                            <div>
                                <h4 style={styles.featureTitle}>Recetas Favoritas</h4>
                                <p style={styles.featureDescription}>Guarda con un corazón las preparaciones que más te gustan para tenerlas siempre a mano.</p>
                            </div>
                        </div>
                        {/* Feature 3 */}
                        <div style={styles.featureItem}>
                            <div style={styles.featureIconWrapper}>
                                <img src="/utensils/media_1789267132580.png" alt="Recetas" style={styles.featureIconImg} />
                            </div>
                            <div>
                                <h4 style={styles.featureTitle}>Crea tus Recetas</h4>
                                <p style={styles.featureDescription}>Publica tus recetas secretas de familia indicando ingredientes, pasos y tiempos.</p>
                            </div>
                        </div>
                    </div>
                    {/* Guante anclado a la esquina inferior derecha del recuadro informativo */}
                    <img src="/utensils/media_1789261651647.png" alt="Guante" style={styles.featuresPanelGlove} />
                </div>
            </section>

            {/* Demostración del Semáforo Inteligente */}
            <section style={styles.semaforoSection}>

                <div style={styles.semaforoCardOuter}>
                    {/* Utensilios decorativos pegados a las esquinas de la tarjeta */}
                    <img src="/utensils/media_1789261651571.png" alt="Batidor" style={styles.decorativeWhisk} />
                    <img src="/utensils/media_1789261651598.png" alt="Espátula" style={styles.decorativeSpatula} />
                    <img src="/utensils/media_1789261651623.png" alt="Sartén" style={styles.decorativePan} />
                    <div style={styles.semaforoHeader}>
                        <h2 style={styles.semaforoTitle}>
                            El Semáforo de Ingredientes
                        </h2>
                        <p style={styles.semaforoSubtitle}>
                            Así clasifica el sistema cada receta en tiempo real según lo que guardes en tu despensa.
                        </p>
                    </div>

                    <div style={styles.semaforoGrid}>
                        {/* Estado Verde */}
                        <div className="semaforo-card semaforo-card-green" style={styles.semaforoCard}>
                            <div style={styles.semaforoBadge('#34D399')}>
                                <CheckCircle2 size={16} strokeWidth={3} />
                            </div>
                            <div style={styles.semaforoIconCircle('#EDF9F1', '#34D399')}>
                                <Carrot size={40} />
                            </div>
                            <div>
                                <h3 style={styles.semaforoCardTitle('#064E3B')}>Verde</h3>
                                <p style={styles.semaforoCardText('#064E3B')}>
                                    Tienes todos los ingredientes en las cantidades necesarias. ¡Puedes empezar a cocinar de inmediato!
                                </p>
                            </div>
                        </div>

                        {/* Estado Amarillo */}
                        <div className="semaforo-card semaforo-card-yellow" style={styles.semaforoCard}>
                            <div style={styles.semaforoBadge('#FACC15')}>
                                <AlertTriangle size={16} strokeWidth={3} />
                            </div>
                            <div style={styles.semaforoIconCircle('#FEF9C3', '#FACC15')}>
                                <Package size={40} />
                            </div>
                            <div>
                                <h3 style={styles.semaforoCardTitle('#713F12')}>Amarillo</h3>
                                <p style={styles.semaforoCardText('#713F12')}>
                                    Tienes el ingrediente pero la cantidad registrada es menor a la requerida por la porción.
                                </p>
                            </div>
                        </div>

                        {/* Estado Rojo */}
                        <div className="semaforo-card semaforo-card-red" style={styles.semaforoCard}>
                            <div style={styles.semaforoBadge('#F87171')}>
                                <XCircle size={16} strokeWidth={3} />
                            </div>
                            <div style={styles.semaforoIconCircle('#FEE2E2', '#F87171')}>
                                <Ban size={40} />
                            </div>
                            <div>
                                <h3 style={styles.semaforoCardTitle('#7F1D1D')}>Rojo</h3>
                                <p style={styles.semaforoCardText('#7F1D1D')}>
                                    No tienes este ingrediente en tu despensa. Te indicamos qué comprar si deseas prepararlo.
                                </p>
                            </div>
                        </div>
                    </div>
                </div>

            </section>


            {/* Footer */}
            <footer style={styles.footer}>
                <p style={styles.footerText}>
                    Cahuin Lab's &reg; 2026
                </p>
            </footer>
        </div>
    );
}
