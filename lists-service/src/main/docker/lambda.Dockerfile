# Stage 1: Build
FROM ghcr.io/graalvm/native-image-community:21 AS builder
WORKDIR /build

# Install AWS certificates
RUN curl -fsSL -o /tmp/global-bundle.pem https://truststore.pki.rds.amazonaws.com/global/global-bundle.pem \
  && cd /tmp \
  # the bundle holds many certs; split and import each one individually
  && csplit -z -f rds-ca- -b '%02d.pem' global-bundle.pem '/-----BEGIN CERTIFICATE-----/' '{*}' \
  && for cert in rds-ca-*.pem; do \
  keytool -importcert -noprompt -trustcacerts \
  -alias "docdb-$(basename "$cert" .pem)" \
  -file "$cert" \
  -keystore "$JAVA_HOME/lib/security/cacerts" \
  -storepass changeit ; \
  done

# ... then your existing native-image build runs and embeds the updated cacerts

# Install Maven if not present in base image
RUN microdnf install -y maven
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
WORKDIR /app
COPY --from=builder /build/target/lists-service /app/lists-service
EXPOSE 8080
ENTRYPOINT ["/app/lists-service"]
