# FROM eclipse-temurin:21-jdk AS build

# WORKDIR /workspace

# COPY . .

# RUN chmod +x gradlew
# RUN ./gradlew bootJar --no-daemon


# FROM eclipse-temurin:21-jre

# WORKDIR /app

# COPY --from=build /workspace/build/libs/*.jar app.jar

# EXPOSE 8080

# ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# FROM eclipse-temurin:21-jdk AS build
# WORKDIR /workspace
# COPY . .
# RUN chmod +x gradlew
# RUN --mount=type=cache,target=/root/.gradle \
#     ./gradlew bootJar --no-daemon

# FROM eclipse-temurin:21-jre
# WORKDIR /app
# COPY --from=build /workspace/build/libs/*.jar app.jar
# EXPOSE 8080
# ENTRYPOINT ["java", "-jar", "/app/app.jar"]


FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

# 1. Copy only the Gradle wrapper and configuration files first
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# 2. Download dependencies (this layer will be cached unless build.gradle changes)
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew build -x test --no-daemon || true

# 3. NOW copy the rest of your source code
COPY src src

# 4. Build the actual JAR (this will be fast because dependencies are already there)
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]