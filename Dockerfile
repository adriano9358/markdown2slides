# ---- Build Stage ----
FROM gradle:8.7.0-jdk21 AS builder
WORKDIR /app

# Copy the entire multi-module project
COPY . .

# Build the bootJar for the host module specifically
RUN gradle :host:bootJar --no-daemon

# ---- Runtime Stage ----
FROM eclipse-temurin:21-jre

RUN apt-get update && \
    apt-get install -y pandoc && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*


WORKDIR /app

# Copy the built JAR only
COPY --from=builder /app/host/build/libs/md2slides.jar md2slides.jar

ENTRYPOINT ["java", "-jar", "md2slides.jar"]
