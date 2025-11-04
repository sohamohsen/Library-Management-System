FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml .
RUN mvn -B -ntp -q dependency:go-offline

COPY src ./src
RUN mvn -B -ntp clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app

RUN apk add --no-cache tzdata tini
ENV TZ=UTC

COPY --from=build /workspace/target/*-SNAPSHOT.jar /app/app.jar

ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0"

EXPOSE 8080
ENTRYPOINT ["/sbin/tini","--","java","-jar","/app/app.jar"]
