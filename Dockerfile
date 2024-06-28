FROM openjdk:21-jdk-slim AS build

WORKDIR /app

COPY .mvn .mvn
COPY mvnw .

COPY pom.xml .

RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline

COPY src src

RUN ./mvnw package -DskipTests


FROM openjdk:21-jdk-slim

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
