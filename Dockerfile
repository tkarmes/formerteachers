FROM eclipse-temurin:23-jdk AS build

WORKDIR /app

# Copy pom and source
COPY pom.xml .
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests || mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:23-jre

WORKDIR /app

# Copy the built jar
COPY --from=build /app/target/formerteachers-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]