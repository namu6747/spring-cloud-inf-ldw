FROM openjdk:21-ea-17-jdk-slim
VOLUME /tmp
COPY apiEncryptionKey.jks apiEncryptionKey.jks
COPY build/libs/config-service-1.0.jar ConfigService.jar
ENTRYPOINT ["java","-jar","ConfigService.jar"]