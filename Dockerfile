FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY payguard-core/pom.xml payguard-core/pom.xml
COPY payguard-spring-demo/pom.xml payguard-spring-demo/pom.xml

RUN mvn dependency:go-offline

COPY payguard-core payguard-core
COPY payguard-spring-demo payguard-spring-demo

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/payguard-spring-demo/target/payguard-spring-demo-1.0-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]