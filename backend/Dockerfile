# Stage 1: Build con Maven y JDK 21
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copiar archivos de Maven Wrapper y dependencias
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copiar código fuente y compilar artefacto JAR
COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# Stage 2: Runtime liviano con JRE 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Crear usuario no-root por seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar el JAR generado desde la etapa de compilación
COPY --from=builder /app/target/*.jar app.jar

# Variables de entorno por defecto
ENV SERVER_PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
