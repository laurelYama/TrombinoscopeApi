# Étape 1: Utiliser l'image OpenJDK comme base
FROM openjdk:17-jdk-slim

# Étape 2: Définir le répertoire de travail dans le conteneur
WORKDIR /app

# Étape 3: Copier le fichier JAR généré par Maven dans le conteneur
COPY target/trombinoscope-api-0.0.1-SNAPSHOT.jar trombinoscope-api.jar

# Étape 4: Exposer le port 8080 sur lequel l'application sera exécutée
EXPOSE 8080

# Étape 5: Lancer l'application Spring Boot
ENTRYPOINT ["java", "-jar", "trombinoscope-api.jar"]
