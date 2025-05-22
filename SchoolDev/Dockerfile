# Utiliser une image JDK allégée pour Java 23
FROM eclipse-temurin:23-jdk-alpine

# Définir le répertoire de travail
WORKDIR /app

# Copier le JAR généré
COPY target/*.jar app.jar

# Exposer le port 8080
EXPOSE 8080

# Lancer l'application
CMD ["java", "-jar", "app.jar"]
