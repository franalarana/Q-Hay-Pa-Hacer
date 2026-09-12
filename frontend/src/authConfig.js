import { LogLevel } from "@azure/msal-browser";

export const msalConfig = {
    auth: {
        clientId: "36bb5da3-29f4-4386-988b-244a844b285c",
        authority: "https://login.microsoftonline.com/1abed6ed-c70a-4381-b9fe-0a67d2f0745e",
        redirectUri: "http://localhost:5173",
        postLogoutRedirectUri: "http://localhost:5173",
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
