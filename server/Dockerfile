FROM eclipse-temurin:21-jdk-alpine as build

WORKDIR /app

COPY . .

RUN ./gradlew buildFatJar

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/build/libs/*-all.jar ./app.jar

CMD ["java", "-jar", "app.jar"]
