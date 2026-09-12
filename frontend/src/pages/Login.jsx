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
                <Utensils size={48} color="var(--primary-dark)" style={{ marginBottom: '16px' }} />
                <h1>Qué hay pa' hacer</h1>
                <p>Descubre qué cocinar con los ingredientes que ya tienes en casa. Inicia sesión para acceder a tu despensa inteligente.</p>
                
                <button className="btn btn-primary" onClick={handleLogin} style={{ width: '100%', padding: '12px', fontSize: '1rem' }}>
                    Iniciar sesión con Microsoft
                </button>
            </div>
        </div>
    );
}
