# BUILD
FROM maven:3.8.2-jdk-11 as build

WORKDIR /home/usersdb/registry
COPY pom.xml .
RUN mvn clean package -Dmaven.main.skip -Dmaven.test.skip && rm -rf target

COPY src ./src
RUN mvn clean package

# PACKAGE
FROM openjdk:11

WORKDIR /usr/local/bin
COPY --from=build /home/usersdb/registry/target/registry-1.0-SNAPSHOT.jar ./registry.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "./registry.jar"]
