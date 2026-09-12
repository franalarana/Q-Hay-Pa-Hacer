import axios from 'axios';
import { msalInstance, loginRequest } from '../authConfig';

const axiosClient = axios.create({
    baseURL: import.meta.env?.VITE_API_URL || 'http://localhost:8080/api',
    headers: {
        'Content-Type': 'application/json'
    }
});

// Interceptor para peticiones HTTP: adjuntar token JWT de Azure AD automáticamente
axiosClient.interceptors.request.use(
    async (config) => {
        let account = msalInstance.getActiveAccount();
        if (!account) {
            const accounts = msalInstance.getAllAccounts();
            if (accounts.length > 0) {
                account = accounts[0];
                msalInstance.setActiveAccount(account);
            }
        }

        // cambios aqui
        const isPublicEndpoint = config.url && (config.url.includes('/public/') || config.url.includes('/public'));
        if (account && !isPublicEndpoint) {
        // hasta aqui
            try {
                const response = await msalInstance.acquireTokenSilent({
                    ...loginRequest,
                    account: account
                });
                
                if (response?.accessToken) {
                    config.headers.Authorization = `Bearer ${response.accessToken}`;
                }
            } catch (error) {
                console.warn('No se pudo obtener el token silenciosamente:', error);
                // Si la interacción es requerida, el flujo de UI puede manejar la reautenticación
            }
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

export default axiosClient;

