# build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
ENV MAVEN_OPTS="-Xmx320m"
COPY back/pom.xml .
RUN mvn -B dependency:resolve -Dmaven.wagon.http.retryHandler.count=5
COPY back/src ./src
RUN mvn -B clean package -DskipTests -Dmaven.wagon.http.retryHandler.count=5

# runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
