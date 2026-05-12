FROM eclipse-temurin:23-jdk

WORKDIR /app

# Copy Maven wrapper and pom
COPY .mvn/ .mvn
COPY mvnw .
COPY pom.xml .

# Download dependencies (better caching)
RUN ./mvnw dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:23-jre

WORKDIR /app

# Copy the built jar from the build stage
COPY --from=0 /app/target/formerteachers-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]