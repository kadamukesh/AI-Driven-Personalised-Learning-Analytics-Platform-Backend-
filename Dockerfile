FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . .

# ✅ FIX: give execute permission
RUN chmod +x mvnw

# Build jar
RUN ./mvnw clean package -DskipTests

ENTRYPOINT ["java", "-jar", "target/*.jar"]