FROM maven:3.8.1-openjdk-16 AS build
COPY src /src
COPY pom.xml /pom.xml
ARG REVISION=0.0.1
RUN mvn --no-transfer-progress -B package --file pom.xml -Drevision=${REVISION}

FROM openjdk:16-jdk
COPY --from=build /target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]