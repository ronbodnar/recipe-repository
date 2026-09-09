FROM eclipse-temurin:21-jre-alpine

COPY target/*.jar app.jar

ENTRYPOINT ["java", "-XX:+UseSerialGC", "-Xss512k", "-Xmx512m", "-jar", "app.jar"]