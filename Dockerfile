FROM gradle:7.2 as builder
COPY build.gradle .
COPY gradle.properties .
COPY src ./src
RUN gradle installDist

FROM openjdk:8-jdk
EXPOSE 8080:8080
RUN mkdir /app
COPY --from=builder /home/gradle/build/install/gradle /app/
WORKDIR /app/bin
CMD ["./gradle"]