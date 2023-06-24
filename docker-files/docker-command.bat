rem RABBITMQ
docker run -d --name rabbitmq --network ecommerce-network -p 15672:15672 -p 5672:5672 -p 15671:15671 -p 5671:5671 -p 4369:4369 -e RABBITMQ_DEFAULT_USER=guest	-e RABBITMQ_DEFAULT_PASS=guest rabbitmq:management

rem CONFIG_SERVER
docker run -d -p 8888:8888 --network ecommerce-network -e "spring.rabbitmq.host=rabbitmq" -e "spring.profiles.active=default" --name config-service namu6747/config-service

rem DISCOVERY_SERVICE (ECOMMERCE)
docker run -d -p 8761:8761 --network ecommerce-network -e "spring.config.import=optional:configserver:http://config-service:8888" --name discovery-service  namu6747/discovery-service:1.0

rem API_GATEWAY_SERVICE
docker run -d -p 8000:8000 --network ecommerce-network -e "spring.config.import=configserver:http://config-service:8888" -e "spring.rabbitmq.host=rabbitmq" -e "eureka.client.serviceUrl.defaultZone=http://discovery-service:8761/eureka/" --name apigateway-service namu6747/apigateway-service:1.0

rem MARIA_DB
docker run -d -p 3307:3306 --network ecommerce-network --name mariadb namu6747/my-mariadb:1.0

rem ZOOKEEPER & KAFKA
docker-compose -f docker-compose-single-broker.yml up -d

rem ZIPKIN
docker run -d -p 9411:9411 --network ecommerce-network --name zipkin openzipkin/zipkin

rem PROMETHEUS
docker run -d -p 9090:9090 --network ecommerce-network --name prometheus -v C:\Work\prometheus-2.37.8.windows-amd64\prometheus.yml:/etc/prometheus/prometheus.yml prom/prometheus

rem GRAFANA
docker run -d -p 3000:3000 --network ecommerce-network --name grafana grafana/grafana

rem USER_SERVICE
docker run -d --network ecommerce-network --name user-service -e "spring.config.import=configserver:http://config-service:8888" -e "spring.rabbitmq.host=rabbitmq" -e "spring.zipkin.base-url=http://zipkin:9411" -e "eureka.client.serviceUrl.defaultZone=http://discovery-service:8761/eureka/" -e "logging.file=/api-logs/users-ws/log" namu6747/user-service:1.0

rem ORDER_SERVICE
docker run -d --network ecommerce-network --name order-service -e "spring.config.import=configserver:http://config-service:8888" -e "spring.rabbitmq.host=rabbitmq" -e "spring.zipkin.base-url=http://zipkin:9411" -e "spring.datasource.url=jdbc:mariadb://mariadb:3306/mydb" -e "eureka.client.serviceUrl.defaultZone=http://discovery-service:8761/eureka/" -e "logging.file=/api-logs/orders-ws/log" namu6747/order-service:1.0

rem CATALOG_SERVICE
docker run -d --network ecommerce-network --name catalog-service -e "spring.config.import=configserver:http://config-service:8888" -e "spring.rabbitmq.host=rabbitmq" -e "spring.datasource.url=jdbc:mariadb://mariadb:3306/mydb" -e "eureka.client.serviceUrl.defaultZone=http://discovery-service:8761/eureka/" -e "logging.file=/api-logs/catalogs-ws/log" namu6747/catalog-service:1.0