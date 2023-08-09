FROM amazoncorretto:11
#COPY target/*.jar app.jar
#ENTRYPOINT ["java","-jar","/app.jar"]
COPY target/*.jar stats-server.jar
ENTRYPOINT ["java", "-jar", "/stats-server.jar"]