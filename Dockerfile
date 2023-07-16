FROM openjdk:11
EXPOSE 8080:8080
RUN mkdir /app
COPY . /app
WORKDIR /app
RUN ./gradlew shadowJar
ENTRYPOINT ["java", "-jar", "/app/build/libs/wires-backend-0.0.1-all.jar"]
