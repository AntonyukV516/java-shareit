FROM maven:3.8.6-openjdk-21 AS build
WORKDIR /app
COPY pom.xml .
COPY shareit-server/pom.xml ./shareit-server/
COPY shareit-gateway/pom.xml ./shareit-gateway/
RUN mvn dependency:go-offline

COPY . .
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/shareit-server/target/*.jar app.jar
EXPOSE 9090
HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -f http://localhost:9090/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]