import { LogLevel, PublicClientApplication, EventType } from "@azure/msal-browser";

export const msalConfig = {
    auth: {
        clientId: import.meta.env?.VITE_AZURE_CLIENT_ID || "36bb5da3-29f4-4386-988b-244a844b285c",
        authority: import.meta.env?.VITE_AZURE_AUTHORITY || "https://login.microsoftonline.com/1abed6ed-c70a-4381-b9fe-0a67d2f0745e",
        redirectUri: import.meta.env?.VITE_REDIRECT_URI || (typeof window !== 'undefined' ? window.location.origin : "http://localhost:5173"),
        postLogoutRedirectUri: import.meta.env?.VITE_POST_LOGOUT_REDIRECT_URI || (typeof window !== 'undefined' ? window.location.origin : "http://localhost:5173"),
    },
    cache: {
        cacheLocation: "sessionStorage", 
        storeAuthStateInCookie: false, 
    },
    system: {
        loggerOptions: {
            loggerCallback: (level, message, containsPii) => {
                if (containsPii) {
                    return;
                }
                switch (level) {
                    case LogLevel.Error:
                        console.error(message);
                        return;
                    case LogLevel.Warning:
                        console.warn(message);
                        return;
                    default:
                        return;
                }
            }
        }
    }
};

export const loginRequest = {
    scopes: ["api://36bb5da3-29f4-4386-988b-244a844b285c/access_as_user"]
};

// Instancia única compartida de MSAL para la aplicación y axios
export const msalInstance = new PublicClientApplication(msalConfig);

// Registrar callback para fijar automáticamente la cuenta activa tras el login
msalInstance.addEventCallback((event) => {
    if (event.eventType === EventType.LOGIN_SUCCESS && event.payload?.account) {
        msalInstance.setActiveAccount(event.payload.account);
    }
});

