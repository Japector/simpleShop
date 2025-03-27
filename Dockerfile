FROM openjdk:17
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} shopping-list-1.0-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/shopping-list-1.0-SNAPSHOT.jar"]