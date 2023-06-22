FROM openjdk:21-ea-17-jdk-slim
VOLUME /tmp
COPY target/ecommerce-1.0.jar DiscoveryService.jar
ENTRYPOINT ["java","-jar","DiscoveryService.jar"]