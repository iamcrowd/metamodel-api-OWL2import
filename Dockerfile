FROM maven:3.5.3-jdk-11 as maven

COPY ./pom.xml ./pom.xml

COPY ./src ./src

RUN mvn package -Dmaven.test.skip=true

FROM openjdk:slim

WORKDIR /Metamodel

COPY --from=maven ./target/MetamodelAPI-0.0.1-SNAPSHOT.jar ./
COPY --from=maven ./src/main/resources/schemas ./src/main/resources/schemas

CMD ["java", "-jar", "MetamodelAPI-0.0.1-SNAPSHOT.jar"]