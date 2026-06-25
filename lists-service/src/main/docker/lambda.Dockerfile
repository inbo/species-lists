# Stage 1: Build
FROM ghcr.io/graalvm/native-image-community:21 AS builder
WORKDIR /build
# Install Maven if not present in base image
RUN microdnf install -y maven
COPY pom.xml .
COPY src ./src
# Compile native image (must use package lifecycle so process-aot runs first)
RUN --mount=type=cache,target=/root/.m2/repository \
  mvn -Pnative package -DskipTests -B

ARG test=1
RUN ls -la /build
RUN ls -la /build/target

# Stage 2: Runtime
FROM public.ecr.aws/lambda/provided:al2023
WORKDIR /app
COPY --from=builder /build/target/lists-service /app/lists-service
EXPOSE 8080
ENTRYPOINT ["/app/lists-service"]
