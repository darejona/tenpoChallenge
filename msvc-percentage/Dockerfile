FROM eclipse-temurin:20-jdk-alpine as builder
LABEL authors="darej"
WORKDIR /app/msvc-percentage

COPY ./pom.xml /app
COPY ./msvc-percentage/.mvn ./.mvn
COPY ./msvc-percentage/mvnw .
COPY ./msvc-percentage/pom.xml .

RUN ./mvnw dependency:go-offline

COPY ./msvc-percentage/src ./src

RUN ./mvnw  clean package -DskipTests

FROM eclipse-temurin:20-jdk-alpine

WORKDIR /app

RUN mkdir ./logs

COPY --from=builder /app/msvc-percentage/target/msvc-percentage-0.0.1-SNAPSHOT.jar .

EXPOSE 8002

CMD ["java", "-jar", "./msvc-percentage-0.0.1-SNAPSHOT.jar"]