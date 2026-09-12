import { useMsal, useIsAuthenticated } from '@azure/msal-react';
import { useNavigate } from 'react-router-dom';
import { loginRequest } from '../authConfig';
import { Utensils, ChefHat, Sparkles } from 'lucide-react';

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
            {/* Navbar sencilla para la landing */}
            <header style={{ padding: '24px 48px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '12px', color: 'var(--primary-dark)', fontWeight: 'bold', fontSize: '1.2rem' }}>
                    <ChefHat size={28} />
                    Qué hay pa' hacer
                </div>
                <nav>
                    {!isAuthenticated ? (
                        <button className="btn btn-outline" onClick={handleLogin}>
                            Iniciar Sesión
                        </button>
                    ) : (
                        <button className="btn btn-primary" onClick={() => navigate('/dashboard')}>
                            Ir a mi despensa
                        </button>
                    )}
                </nav>
            </header>

            {/* Hero Section */}
            <main style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', textAlign: 'center', padding: '24px', maxWidth: '800px', margin: '0 auto' }}>
                <div style={{ backgroundColor: 'var(--primary-color)', padding: '16px', borderRadius: '50%', marginBottom: '24px', color: 'white', boxShadow: 'var(--shadow-md)' }}>
                    <Utensils size={64} />
                </div>
                
                <h1 style={{ fontSize: '3.5rem', fontWeight: '700', color: 'var(--text-main)', marginBottom: '16px', lineHeight: '1.1' }}>
                    Tu despensa inteligente, <br />
                    <span style={{ color: 'var(--primary-dark)' }}>recetas al instante.</span>
                </h1>
                
                <p style={{ fontSize: '1.2rem', color: 'var(--text-muted)', marginBottom: '40px', maxWidth: '600px' }}>
                    Dinos qué ingredientes tienes en casa y nosotros te decimos qué puedes cocinar hoy. ¡Deja de pensar y empieza a cocinar!
                </p>

                <div style={{ display: 'flex', gap: '16px' }}>
                    <button className="btn btn-primary" onClick={handleLogin} style={{ padding: '16px 32px', fontSize: '1.1rem' }}>
                        {isAuthenticated ? 'Ir al Dashboard' : 'Comenzar ahora'} <Sparkles size={20} style={{ marginLeft: '8px' }} />
                    </button>
                </div>
            </main>
        </div>
    );
}
