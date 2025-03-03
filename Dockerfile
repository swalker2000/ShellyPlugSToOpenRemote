FROM gradle:7.6.1-jdk17-alpine AS build
RUN mkdir /home/gradle/build
COPY . /home/gradle/build
WORKDIR /home/gradle/build
RUN gradle build --no-daemon
FROM amazoncorretto:17-alpine-jdk
RUN mkdir /opt/app
COPY --from=build /home/gradle/build/build/libs/ShellyPtugSToOpenRemoteFat-*.jar /opt/app/app.jar
WORKDIR /opt/app
CMD ["java", "-jar", "app.jar"]