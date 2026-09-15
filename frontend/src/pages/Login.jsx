import { useMsal } from '@azure/msal-react';
import { loginRequest } from '../authConfig';
import { Utensils } from 'lucide-react';

export default function Login() {
    const { instance } = useMsal();

    const handleLogin = () => {
        instance.loginRedirect(loginRequest).catch(e => {
            console.error(e);
        });
    };

    return (
        <div className="auth-container">
            <div className="auth-card">
                <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', marginBottom: '16px' }}>
                    <img 
                        src="/logo-bowl.png?v=4" 
                        alt="Logo" 
                        style={{ height: '105px', objectFit: 'contain', marginBottom: '8px', filter: 'drop-shadow(0 4px 3px rgba(0, 0, 0, 0.3))', borderRadius: '50%' }} 
                    />
                    <h1 style={{ fontFamily: '"Caveat", cursive', fontSize: '2.8rem', color: '#155D40', margin: 0, fontWeight: '700' }}>
                        Qué hay pa' hacer
                    </h1>
                </div>
                <p>Descubre qué cocinar con los ingredientes que ya tienes en casa. Inicia sesión para acceder a tu despensa inteligente.</p>
                
                <button className="btn btn-primary" onClick={handleLogin} style={{ width: '100%', padding: '12px', fontSize: '1rem' }}>
                    Iniciar sesión con Microsoft
                </button>
            </div>
        </div>
    );
}
