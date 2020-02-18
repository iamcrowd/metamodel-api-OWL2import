# Metamodel

## Description
This project involves the research, design and implementantion of the [KF Metamodel](https://www.sciencedirect.com/science/article/abs/pii/S0169023X1500049X). The main idea of this project is to implement an API that allows a user to send a JSON that represents a model in a given modeling language (UML, EER or ORM) and generate the equivalent model in one of the languages mentioned previously.


## Getting started
The project is built with the Java framework *Spring boot*, and its dependencies are managed with *Maven*. In order to compile and execute this project you will need:
- Java JDK 11 (11.0.5)
- Apache Maven 3.6.0

Once you fulfil the requeriments listed above, clone this repository. Run the following commands from the root folder of the project to run the API and deploy it locally with port *8080*:
```
$ mvn clean compile
$ mvn package
$ mvn exec:java
```

### Using the API
At the moment, there are two ways of trying the API out. The first one involves using the endpoints defined, sending HTTP POST requests to one of the following endpoints:    
- `/umltometa`  - UML to Metamodel   
- `/ormtometa` - ORM to Metamodel  
- `/eertometa` - EER to Metamodel    
- `/metatouml` - Metamodel to UML  
- `/metatoorm` - Metamodel to ORM  
- `/metatoeer` - Metamodel to EER   
- `/eertouml` - EER to UML  
- `/eertoorm` - EER to ORM  
- `/ormtouml` - ORM to UML  
- `/ormtoeer` - ORM to EER  
- `/umltoorm` - UML to ORM  
- `/umltoeer` - UML to EER  

The other way to try the API is by using the web page generated for this project. To use the web page, open `src/web/index.html` in any browser. This will display an static html page that uses the API endpoints. Note that this web page is isolated from the API itself in order to keep the definition and purpose of it. The web page consumes the API as any other client would.

## Next developments
### 1:1 Mappings
- ~~UML ⟶ Meta~~ ✔️
- ~~EER ⟶ Meta~~ ✔️
- ~~ORM ⟶ Meta~~ ✔️
- ~~Meta ⟶ UML~~ ✔️
- ~~Meta ⟶ ORM~~ ✔️
- ~~Meta ⟶ EER~~ ✔️  

*Note: Additionally, other mappings, transformations and aproximations could be considered for future developments.*  
*Note: Model to model transformations will be trivial once the Model ⟶ Meta and Meta ⟶ Model endpoints are implemented.*
## Code Conventions
- [General Naming Conventions](https://www.oracle.com/technetwork/java/codeconventions-135099.html)
- [Naming Package Conventions](https://docs.oracle.com/javase/tutorial/java/package/namingpkgs.html)

## License