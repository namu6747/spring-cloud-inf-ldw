FROM openjdk:21-ea-17-jdk-slim
VOLUME /tmp
COPY target/apigateway-service-1.0.jar ApiGatewayService.jar
ENTRYPOINT ["java","-jar","ApiGatewayService.jar"]