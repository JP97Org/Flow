FROM maven:3.9.16-amazoncorretto-21 AS build
WORKDIR /workspace
COPY pom.xml ./
COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /workspace
COPY --from=build /workspace/target/flow.jar ./flow.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "flow.jar"]
