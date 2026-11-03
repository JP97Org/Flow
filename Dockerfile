FROM maven:3.9.16-amazoncorretto-21 AS backend
WORKDIR /workspace
COPY pom.xml ./
COPY src ./src
RUN mvn -B -DskipTests package

FROM node:24-bookworm-slim AS frontend
RUN apt-get update \
    && apt-get install -y --no-install-recommends openjdk-17-jre-headless \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /workspace
COPY frontend ./frontend
COPY --from=backend /workspace/target/openapi/openapi.json ./target/openapi/openapi.json
WORKDIR /workspace/frontend
RUN npm ci --no-audit --no-fund \
    && npm run generate:api \
    && npm run build

FROM maven:3.9.16-amazoncorretto-21 AS final
WORKDIR /workspace
COPY pom.xml ./
COPY src ./src
COPY --from=frontend /workspace/frontend/dist/frontend/browser ./src/main/resources/META-INF/resources
RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /workspace
COPY --from=final /workspace/target/flow.jar ./flow.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "flow.jar"]
