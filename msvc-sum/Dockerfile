FROM eclipse-temurin:20-jdk-alpine as builder
LABEL authors="darej"
WORKDIR /app/msvc-sum

COPY ./pom.xml /app
COPY ./msvc-sum/.mvn ./.mvn
COPY ./msvc-sum/mvnw .
COPY ./msvc-sum/pom.xml .

RUN ./mvnw dependency:go-offline

COPY ./msvc-sum/src ./src

RUN ./mvnw  clean package -DskipTests

FROM eclipse-temurin:20-jdk-alpine

WORKDIR /app

RUN mkdir ./logs

COPY --from=builder /app/msvc-sum/target/msvc-sum-0.0.1-SNAPSHOT.jar .

EXPOSE 8001

CMD sleep 30 && java -jar msvc-sum-0.0.1-SNAPSHOT.jar