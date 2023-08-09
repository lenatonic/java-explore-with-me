FROM amazoncorretto:11
RUN ls -la
COPY ./target/*.jar /ewm-server.jar
ENTRYPOINT ["java", "-jar", "/ewm-server.jar"]