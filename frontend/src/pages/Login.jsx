import { useMsal } from '@azure/msal-react';
import { loginRequest } from '../authConfig';
import { Utensils } from 'lucide-react';
import '../styles/login.css';

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
                <div className="login-logo-wrapper">
                    <img
                        src="/logo-bowl.png?v=4"
                        alt="Logo"
                        className="login-logo-img"
                    />
                    <h1 className="login-title">
                        Qué hay pa' hacer
                    </h1>
                </div>
                <p>Descubre qué cocinar con los ingredientes que ya tienes en casa. Inicia sesión para acceder a tu despensa inteligente.</p>

                <button className="btn btn-primary login-button" onClick={handleLogin}>
                    Iniciar sesión con Microsoft
                </button>
            </div>
        </div>
    );
}
