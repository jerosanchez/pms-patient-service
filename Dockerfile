FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests && mv target/*.jar target/service.jar

FROM eclipse-temurin:21-jdk-jammy AS runner
WORKDIR /app
COPY --from=builder /app/target/service.jar ./service.jar
EXPOSE 4000
CMD ["java", "-jar", "service.jar"]