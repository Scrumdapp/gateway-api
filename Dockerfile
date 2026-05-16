# Stage 1: Cache Gradle dependencies
FROM gradle:9.2.0-jdk21-corretto AS cache
RUN mkdir -p /home/gradle/cache_home
ENV GRADLE_USER_HOME=/home/gradle/cache_home
COPY build.gradle settings.gradle /home/gradle/app/
COPY gradle /home/gradle/app/gradle
WORKDIR /home/gradle/app
RUN gradle dependencies --no-daemon

# Stage 1: Build Application
FROM gradle:9.2.0-jdk21-corretto AS build
COPY --from=cache /home/gradle/cache_home /home/gradle/.gradle
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
# Build the fat JAR, Gradle also supports shadow
# and boot JAR by default.
RUN gradle bootJar --no-daemon

# Stage 2: Create the Runtime Image
FROM amazoncorretto:21 AS runtime
EXPOSE 9999
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/gateway-api.jar
WORKDIR /app
ENTRYPOINT ["java","-jar","gateway-api.jar"]