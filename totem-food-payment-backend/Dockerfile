FROM adoptopenjdk/openjdk17:alpine-jre
LABEL maintainer="IT Company - www.itcompany.com"
WORKDIR /opt/app
COPY target/*.jar totem-food-payment-backend.jar
ENTRYPOINT ["java","-jar","/opt/app/totem-food-payment-backend.jar"]
