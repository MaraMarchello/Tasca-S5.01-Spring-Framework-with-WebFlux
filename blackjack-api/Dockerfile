FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app
COPY . .
RUN apt-get update && apt-get install -y curl unzip
RUN curl -L https://services.gradle.org/distributions/gradle-8.6-bin.zip -o gradle.zip
RUN unzip gradle.zip
WORKDIR /app/blackjack-api
RUN /app/gradle-8.6/bin/gradle build --no-daemon -x test --stacktrace

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/blackjack-api/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"] 