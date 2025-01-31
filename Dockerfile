FROM amazoncorretto:11

WORKDIR /app

COPY target/url-shortener-0.0.1-SNAPSHOT.jar  /app/url-shortener.jar

ENTRYPOINT ["java", "-jar", "/app/url-shortener.jar"]

