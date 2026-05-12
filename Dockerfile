FROM eclipse-temurin:23-jdk AS build

WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Install Maven and build
RUN apt-get update && apt-get install -y maven \
    && mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:23-jre

WORKDIR /app

COPY --from=build /app/target/formerteachers-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]