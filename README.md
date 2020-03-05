## OWLAPI-based OWL 2 Importer to [KF Metamodel](https://www.sciencedirect.com/science/article/abs/pii/S0169023X1500049X) ##

Public URL [Try API](http://crowd.fi.uncoma.edu.ar/crowd2-metamodel/metamodelapi-owlimport/src/web/index.html)

## Description
This project involves the research, design and implementantion of an [OWL 2](https://www.w3.org/TR/2012/REC-owl2-xml-serialization-20121211/) importer.
The main idea is to implement an API that allows a user to send a URL or upload a file containing an OWL 2 specs 
and generate KF instances from them in order to be visualised in [crowd](http://crowd.fi.uncoma.edu.ar) (UML|EER|ORM 2).


## Getting started
The project is built with the Java framework *Spring boot*, and its dependencies are managed with *Maven*. In order to compile and execute this project you will need:
- Java JDK 11 (11.0.5)
- Apache Maven 3.6.0

Once you fulfil the requeriments listed above, clone this repository. Run the following commands from the root folder of the project to run the API and deploy it locally with port *8080*:
```
$ mvn clean compile
$ mvn package
$ mvn exec:java

 - Run as Service
$ mvn clean dependency:copy-dependencies package spring-boot:repackage
$ java -jar target/app.jar 
```

### Using the API
At the moment, there are two ways of trying the API out. The first one involves using the endpoints defined, sending HTTP POST requests to one of the following endpoints:

- `/owlclassestometa` - Given a OWL 2 ontology, import all the OWL classes into a KF Metamodel instance.    
- `/owlallsubstometa` - Given a OWL 2 ontology, import all the SubClasses into a KF Metamodel instance.  
- `/owlonesubstometa` - Given a OWL 2 ontology and a SuperClass, import all the SubClasses of SuperClass into a KF Metamodel instance.

The other way to try the API is by using the web page generated for this project. To use the web page, open `src/web/index.html` in any browser. This will display an static html page that uses the API endpoints. Note that this web page is isolated from the API itself in order to keep the definition and purpose of it. The web page consumes the API as any other client would.

## Next developments

- ~~OWL Classes ⟶ Meta~~ ✔️
- ~~OWL AllSubClasses ⟶ Meta~~ ✔️
- ~~OWL AnSubClass given a OWL SuperClass ⟶ Meta~~ ✔️
- OWL ObjectProperties ❌
- OWL DataProperties ❌
- OWL Disjoint Axioms ❌
- OWL Equivalence Axioms ❌

*Note: Additionally, other mappings, transformations and aproximations could be considered for future developments.*  
*Note: Model to model transformations will be trivial once the Model ⟶ Meta and Meta ⟶ Model endpoints are implemented.*
## Code Conventions
- [General Naming Conventions](https://www.oracle.com/technetwork/java/codeconventions-135099.html)
- [Naming Package Conventions](https://docs.oracle.com/javase/tutorial/java/package/namingpkgs.html)

## License
