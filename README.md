## Conceptual Models based Reconstruction of OWL 2 Ontologies

Public URL [Try API](http://crowd.fi.uncoma.edu.ar/crowd2-metamodel/metamodelapi-owlimport/src/web/index.html)

## Description
This project involves the research, design and implementantion of an [OWL 2](https://www.w3.org/TR/2012/REC-owl2-xml-serialization-20121211/) importer.
The main idea is to implement an API that allows a user to send a URL or upload a file containing an OWL 2 specs 
and generate KF instances from them in order to be visualised in [crowd](http://crowd.fi.uncoma.edu.ar) (UML|EER|ORM 2).

## Tools
[KF Metamodel](https://www.sciencedirect.com/science/article/abs/pii/S0169023X1500049X)

[OWLAPI](https://github.com/owlcs/owlapi)

[OntologyUtils](https://bitbucket.org/gilia/ontologyutils-viz/)


## Getting started
The project is built with the Java framework *Spring boot*, and its dependencies are managed with *Maven*. In order to compile and execute this project you will need:
- Java JDK 11 (11.0.5)
- Apache Maven 3.6.0

Once you fulfil the requeriments listed above, clone this repository. Run the following commands from the root folder of the project to run the API and deploy it locally with port *8080*:
```
$ mvn install:install-file -Dfile=metamodelapi-owlimport\lib\ontologyutils-viz-0.0.1-SNAPSHOT.jar
$ mvn clean compile
$ mvn package
$ mvn exec:java

 - Run as Service
$ mvn clean dependency:copy-dependencies package spring-boot:repackage
$ java -jar target/app.jar 
```

### Using the API
At the moment, there are two ways of trying the API out. The first one involves using the endpoints defined, sending HTTP POST requests to one of the following endpoints:


## Code Conventions
- [General Naming Conventions](https://www.oracle.com/technetwork/java/codeconventions-135099.html)
- [Naming Package Conventions](https://docs.oracle.com/javase/tutorial/java/package/namingpkgs.html)

## License
