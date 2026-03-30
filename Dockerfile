FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy full project
COPY . .

# Build jar inside Docker
RUN ./mvnw clean package -DskipTests

# Run jar
ENTRYPOINT ["java", "-jar", "target/*.jar"]