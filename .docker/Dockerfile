FROM maven:3.9.9-amazoncorretto-21-al2023 AS build
WORKDIR /app
COPY . .
RUN echo "Building projectmanagement-1.0.0.jar"
RUN mvn clean package -DskipTests=true

FROM eclipse-temurin:21-jre-alpine
ENV APP_NAME=projectmanagement
ENV APP_VERSION=1.0.0
WORKDIR /app
COPY --from=build /app/target/${APP_NAME}-${APP_VERSION}.jar /app/${APP_NAME}.jar
EXPOSE 8080
CMD ["java", "-jar", "/app/projectmanagement.jar"]