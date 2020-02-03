FROM maven:3.5.3-jdk-11 as maven

COPY ./pom.xml ./pom.xml

# RUN mvn dependecy:go-offline -B

COPY ./src ./src

RUN mvn clean compile

RUN mvn package -Dmaven.test.skip=true

FROM openjdk:8u171-jre-alpine

WORKDIR /Metamodel

COPY --from=maven ./target/MetamodelAPI-0.0.1-SNAPSHOT.jar ./
COPY --from=maven ./src/main/resources/schemas ./src/main/resources/schemas

# CMD ["mvn", "exec:java"]

CMD ["java", "-jar", "MetamodelAPI-0.0.1-SNAPSHOT.jar"]