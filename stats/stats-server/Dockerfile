FROM amazoncorretto:11
RUN ls -la
COPY ./target/*.jar stats-server.jar
ENTRYPOINT ["java", "-jar", "/stats-server.jar"]