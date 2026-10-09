FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN --mount=type=cache,target=/root/.m2/repository \
  mvn package -DskipTests -B

FROM amazoncorretto:21

ARG APP_ARTIFACT=lists-service
ENV SERVER_PORT="8080"

RUN mkdir -p /data/$APP_ARTIFACT 
WORKDIR /opt/atlas/$APP_ARTIFACT

COPY --from=builder --chown=1000:1000 /app/target/lists-service-*.jar ./app.jar

HEALTHCHECK CMD curl -fsS http://localhost:8080/api/v2/species-lists/actuator/health/readiness || exit 1

USER 1000:1000
EXPOSE $SERVER_PORT

ENTRYPOINT ["sh", "-c", "exec java ${JAVA_OPTS} -jar app.jar"]
