FROM maven:3.9.2-eclipse-temurin-17-alpine AS build
COPY totem-food-payment-backend /usr/src/app/totem-food-payment-backend
COPY totem-food-payment-application /usr/src/app/totem-food-payment-application
COPY totem-food-payment-domain /usr/src/app/totem-food-payment-domain
COPY totem-food-payment-framework /usr/src/app/totem-food-payment-framework
COPY pom.xml /usr/src/app/pom.xml
RUN mvn -f /usr/src/app/pom.xml clean package

FROM openjdk:17.0.2-slim-buster
LABEL maintainer="Totem Food Service"
WORKDIR /opt/app
COPY --from=build /usr/src/app/totem-food-payment-backend/target/*.jar totem-food-payment-service.jar
ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=0.0.0.0:8787", "-jar","/opt/app/totem-food-payment-service.jar"]