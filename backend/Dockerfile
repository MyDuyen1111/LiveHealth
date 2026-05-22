# ==============================================================================
# STAGE 1: Build the Quarkus fast-jar application
# ==============================================================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /workspace

# Copy POM and download dependencies to leverage Docker layer caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy codebase and compile/package the app
COPY src src
RUN mvn package -DskipTests

# ==============================================================================
# STAGE 2: Lightweight production JRE environment
# ==============================================================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /deployments

# Create a secure, restricted non-root system group and user
RUN addgroup -S quarkus && adduser -S quarkus -G quarkus \
    && mkdir -p /deployments \
    && chown -R quarkus:quarkus /deployments

# Run as non-root user for production security hardening
USER quarkus

# Copy the built fast-jar structure from the build container
COPY --from=build --chown=quarkus:quarkus /workspace/target/quarkus-app/lib/ /deployments/lib/
COPY --from=build --chown=quarkus:quarkus /workspace/target/quarkus-app/*.jar /deployments/
COPY --from=build --chown=quarkus:quarkus /workspace/target/quarkus-app/app/ /deployments/app/
COPY --from=build --chown=quarkus:quarkus /workspace/target/quarkus-app/quarkus/ /deployments/quarkus/

# Expose standard port
EXPOSE 8080

# Configure execution parameters
ENV JAVA_OPTIONS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTIONS -jar /deployments/quarkus-run.jar" ]
