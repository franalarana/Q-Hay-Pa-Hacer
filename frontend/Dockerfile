# Stage 1: Build de Vite con Node.js 20 Alpine
FROM node:20-alpine AS builder
WORKDIR /app

# Copiar manifests de dependencias
COPY package.json package-lock.json ./
RUN npm ci

# Argumentos de compilación para Vite (Variables de entorno inyectables)
ARG VITE_API_URL
ARG VITE_AZURE_CLIENT_ID
ARG VITE_AZURE_TENANT_ID
ARG VITE_REDIRECT_URI

ENV VITE_API_URL=$VITE_API_URL
ENV VITE_AZURE_CLIENT_ID=$VITE_AZURE_CLIENT_ID
ENV VITE_AZURE_TENANT_ID=$VITE_AZURE_TENANT_ID
ENV VITE_REDIRECT_URI=$VITE_REDIRECT_URI

# Copiar código fuente y compilar bundle de producción
COPY . ./
RUN npm run build

# Stage 2: Servidor Nginx Alpine ultra ligero
FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
