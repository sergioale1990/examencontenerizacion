FROM eclipse-temurin:21-jdk-noble AS build
WORKDIR /workspace

COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon > /dev/null

COPY src ./src
RUN ./gradlew bootjar --no-daemon -x test

FROM eclipse-temurin:21-jre-noble

LABEL org.opencontainers.image.title="contenerizacion" \
      org.opencontainers.image.description="API de gestión de tareas - MIS-709" \
      org.opencontainers.image.authors="Sergio Alejandro Lema Estevez"

RUN groupadd --system --gid 10001 app \
    && useradd --system --uid 10001 --gid app --no-create-home app

WORKDIR /app
COPY --from=build --chown=10001:10001 /workspace/build/libs/*.jar app.jar

USER 10001:10001
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]