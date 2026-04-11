FROM amazoncorretto:21
WORKDIR /app
COPY target/*.jar app.jar
COPY .env .env
ENTRYPOINT ["java", "-jar", "app.jar"]