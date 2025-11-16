FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copia el pom.xml y los archivos de dependencias primero para aprovechar cache de Docker
COPY pom.xml .
COPY src ./src

# Instala Maven si no está disponible
RUN apk add --no-cache maven

# Compila la aplicación
RUN mvn clean package -DskipTests

# Copia el .jar compilado como app.jar
RUN cp target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
