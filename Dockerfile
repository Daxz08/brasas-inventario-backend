# ---- Etapa 1: Compilación con Maven ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# ---- Etapa 2: Imagen de ejecución ligera ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# Render asigna un puerto dinámicamente, lo exponemos
EXPOSE 8080
# El comando de entrada usa el puerto que Render asigne vía variable de entorno
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]