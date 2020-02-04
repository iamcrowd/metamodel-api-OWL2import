package com.gilia.utils;

/**
 * It encapsulates every constant use in the application.
 * The only purpose of this class is to organize the constants (specially string constants)
 * the constants used throughout the application.
 *
 * @author Emiliano Rios Gavagnin
 */
public class Constants {

    // Endpoints

    public static final String UML_TO_META_ROUTE = "/umltometa";

    public static final String EER_TO_META_ROUTE = "/eertometa";

    public static final String ORM_TO_META_ROUTE = "/ormtometa";

    // Schemas

    public static final String UML_SCHEMA_PATH = "src/main/resources/schemas/umlSchema.json";

    public static final String EER_SCHEMA_PATH = "src/main/resources/schemas/eerSchema.json";

    public static final String ORM_SCHEMA_PATH = "src/main/resources/schemas/ormSchema.json";

    public static final String META_SCHEMA_PATH = "src/main/resources/schemas/metaSchema.json";

    // Common Strings

    public static final String CHARSET = "UTF-8";

    public static final String UML_STRING = "uml";

    public static final String EER_STRING = "eer";

    public static final String ORM_STRING = "orm";

    public static final String METAMODEL_STRING = "metamodel";

    public static final String ENTITY_STRING = "entity";

    public static final String RELATIONSHIP_STRING = "relationship";

    public static final String SUBSUMPTION_STRING = "subsumption";

    public static final String ROLE_STRING = "role";

    public static final String ERROR_STRING = "error";

    public static final String DISJOINT_STRING = "disjoint";

    public static final String COVERING_STRING = "covering";

    public static final String TIMESTAMP_STRING = "timestamp";

    public static final String MESSAGE_STRING = "message";

    public static final String STATUS_STRING = "status";

    // JSON Keys

    public static final String KEY_NAMESPACES = "namespaces";

    public static final String KEY_ONTOLOGY_IRI = "ontologyIRI";

    public static final String KEY_VALUE = "value";

    public static final String KEY_NAME = "name";

    public static final String KEY_CLASSES = "classes";

    public static final String KEY_LINKS = "links";

    public static final String KEY_TYPE = "type";

    public static final String KEY_ASSOCIATION = "association";

    public static final String KEY_ROLES = "roles";

    public static final String KEY_GENERALIZATION = "generalization";

    public static final String KEY_PARENT = "parent";

    public static final String KEY_MULTIPLICITY = "multiplicity";

    public static final String KEY_ENTITIES = "entities";

    public static final String KEY_ROLENAME = "rolename";

    public static final String KEY_ENTITY_TYPE = "entity type";

    public static final String KEY_OBJECT_TYPE_CARDINALITY = "object type cardinality";

    public static final String KEY_ENTITY_PARENT = "entity parent";

    public static final String KEY_ENTITY_CHILDREN = "entity children";

    public static final String KEY_MINIMUM = "minimum";

    public static final String KEY_MAXIMUM = "maximum";

    public static final String KEY_CONSTRAINT = "constraint";

    public static final String KEY_DISJOINTNESS_CONSTRAINT = "disjointness constraints";

    public static final String KEY_COMPLETENESS_CONSTRAINT = "completeness constraints";

    // Regex

    public static final String RANDOM_STRING_REGEX = "[^A-Za-z0-9]";

    public static final String CARDINALITY_REGEX = "(\\d+|N|\\*)\\.\\.(\\d+|N|M|\\*)";

    public static final String CARDINALITY_DIVIDER_REGEX = "\\.\\.";

    // Common Numbers

    public static final int RANDOM_STRING_LENGTH = 4;

    // Error Messages

    public static final String ALREADY_EXIST_ENTITY_ERROR = "The given entity already exists in the metamodel";

    public static final String ALREADY_EXIST_RELATIONSHIP_ERROR = "The given relationship already exists in the metamodel";

    public static final String CARDINALITY_SYNTAX_ERROR = "Cardinality syntax error creating";

    public static final String INCONSISTENT_ROLES_WITH_CARDINALITIES_ERROR = "The number of roles is inconsistent with the number of cardinalities";

    public static final String ENTITY_NOT_FOUND_ERROR = "The given entity was not found or is not valid";

    public static final String ENTITIES_INFORMATION_NOT_FOUND_ERROR = "Information about the entities was not found";

    public static final String RELATIONSHIPS_INFORMATION_NOT_FOUND_ERROR = "Information about the relationships was not found";

    public static final String ROLES_INFORMATION_NOT_FOUND_ERROR = "Information about the roles was not found";

    public static final String CONSTRAINTS_INFORMATION_NOT_FOUND_ERROR = "Information about the constraints was not found";

    public static final String ASSOCIATION_EXPECTED_ERROR = "An association link was expected";

    // Exception Names

    public static final String VALIDATION_EXCEPTION_NAME = "ValidationException";


}
