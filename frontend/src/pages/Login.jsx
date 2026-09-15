import { useMsal } from '@azure/msal-react';
import { loginRequest } from '../authConfig';
import { Utensils } from 'lucide-react';
import * as styles from '../styles/login.styles';

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
                <div style={styles.logoWrapper}>
                    <img
                        src="/logo-bowl.png?v=4"
                        alt="Logo"
                        style={styles.logoImg}
                    />
                    <h1 style={styles.title}>
                        Qué hay pa' hacer
                    </h1>
                </div>
                <p>Descubre qué cocinar con los ingredientes que ya tienes en casa. Inicia sesión para acceder a tu despensa inteligente.</p>

                <button className="btn btn-primary" onClick={handleLogin} style={styles.loginButton}>
                    Iniciar sesión con Microsoft
                </button>
            </div>
        </div>
    );
}
