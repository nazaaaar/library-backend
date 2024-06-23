FROM openjdk:8-jdk-alpine
MAINTAINER Bhargav <nazariikurei@gmail.com>
LABEL authors="nazar"
VOLUME /tmp
ARG JAR_FILE=target/Spring-Boot-Google-Cloud-0.0.1.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
