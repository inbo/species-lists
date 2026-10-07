# Stage 1: Build
FROM ghcr.io/graalvm/native-image-community:21 AS builder
WORKDIR /build

# Install Maven if not present in base image (curl/diffutils needed for the cert step below)
RUN microdnf install -y maven curl diffutils

# Install AWS certificates
# NOTE: curl has explicit timeouts/retries so a stalled network fails fast
# instead of hanging the CI job indefinitely (ponytail: 111 sequential keytool
# JVM boots take ~1-2 min with no output; parallelize if this gets slower).
RUN curl --connect-timeout 20 --max-time 300 --retry 5 --retry-all-errors -fsSL -o /tmp/global-bundle.pem https://truststore.pki.rds.amazonaws.com/global/global-bundle.pem \
  && cd /tmp \
  # the bundle holds many certs; split and import each one individually
  && csplit -s -z -f rds-ca- -b '%02d.pem' global-bundle.pem '/-----BEGIN CERTIFICATE-----/' '{*}' \
  && echo "Importing $(echo rds-ca-*.pem | wc -w) RDS CA certs..." \
  && for cert in rds-ca-*.pem; do \
  keytool -importcert -noprompt -trustcacerts \
  -alias "docdb-$(basename "$cert" .pem)" \
  -file "$cert" \
  -keystore "$JAVA_HOME/lib/security/cacerts" \
  -storepass changeit ; \
  done \
  && rm -f /tmp/global-bundle.pem /tmp/rds-ca-*.pem

# ... then your existing native-image build runs and embeds the updated cacerts

COPY pom.xml .
COPY src ./src
# Compile native image (must use package lifecycle so process-aot runs first)
RUN --mount=type=cache,target=/root/.m2/repository \
  mvn -Pnative package -DskipTests -B

# Stage 2: Runtime
FROM public.ecr.aws/lambda/provided:al2023
# Lambda Web Adapter: bridges the Lambda Runtime API to the HTTP server below.
# Runs as a Lambda extension; forwards (ALB) invoke events to localhost:${PORT}.
COPY --from=public.ecr.aws/awsguru/aws-lambda-adapter:0.9.1 /lambda-adapter /opt/extensions/lambda-adapter
ENV PORT=8080
# Allow app startup to continue past Lambda's 10s init phase limit
ENV AWS_LWA_ASYNC_INIT=true
# Poll a path inside the servlet context (root "/" is outside /api/v2/species-lists)
ENV AWS_LWA_READINESS_CHECK_PATH=/api/v2/species-lists/actuator/health/readiness
WORKDIR /app
COPY --from=builder /build/target/lists-service /app/lists-service
EXPOSE 8080
ENTRYPOINT ["/app/lists-service"]
