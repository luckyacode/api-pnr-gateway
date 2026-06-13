FROM eclipse-temurin:21-jre-alpine
LABEL authors="PRAVEEN"
ADD target/api-pnr-gateway-1.0.0-SNAPSHOT.jar api-pnr-gateway-1.0.0-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","api-pnr-gateway-1.0.0-SNAPSHOT.jar"]