FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy the JAR file from target directory
ARG JAR_FILE=target/Appointment-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

EXPOSE 9090

ENTRYPOINT ["java", "-jar", "app.jar"]