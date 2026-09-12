import axios from 'axios';
import { msalConfig, loginRequest } from '../authConfig';
import { PublicClientApplication } from '@azure/msal-browser';

// Crear una instancia de MSAL para el cliente de axios si no se pasa a través de react
const msalInstance = new PublicClientApplication(msalConfig);
msalInstance.initialize().then(() => {
    // Listo
});

const axiosClient = axios.create({
    baseURL: 'http://localhost:8080/api', // Puerto por defecto de Spring Boot
    headers: {
        'Content-Type': 'application/json'
    }
});

// Interceptor para peticiones (agregar token)
axiosClient.interceptors.request.use(
    async (config) => {
        const account = msalInstance.getActiveAccount() || msalInstance.getAllAccounts()[0];

        if (account) {
            try {
                const response = await msalInstance.acquireTokenSilent({
                    ...loginRequest,
                    account: account
                });
                
                // Agregar el token JWT al header Authorization
                config.headers.Authorization = `Bearer ${response.accessToken}`;
            } catch (error) {
                console.error('Error al obtener el token silenciosamente', error);
                // Si falla el silent, quizás la sesión expiró.
                // Idealmente, esto podría disparar un msalInstance.loginRedirect()
            }
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

export default axiosClient;
