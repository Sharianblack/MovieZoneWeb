# ==========================================
# ETAPA 1: Construcción (Build)
# ==========================================
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copiamos el archivo de configuración y el código fuente
COPY pom.xml .
COPY src ./src

# Compilamos el proyecto y generamos el archivo .war
RUN mvn clean package -DskipTests

# ==========================================
# ETAPA 2: Ejecución en Producción (Tomcat)
# ==========================================
FROM tomcat:10.1-jdk17

# Limpiamos la carpeta por defecto de Tomcat para evitar estorbos
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# Copiamos tu archivo .war y lo renombramos a ROOT.war
# (Esto hace que tu página cargue directo en el link principal sin añadir subrutas)
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

# Exponemos el puerto estándar
EXPOSE 8080

# Prendemos el motor
CMD ["catalina.sh", "run"]