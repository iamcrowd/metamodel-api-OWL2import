package com.gilia.utils;

/**
 * It encapsulates every constant use in the application.
 * The only purpose of this class is to organize the constants (specially string constants)
 * the constants used throughout the application.
 *
 * @author Emiliano Rios Gavagnin
 */
public class Constants {

    // Schemas

    public static final String UML_SCHEMA_PATH = "src/main/resources/schemas/umlSchema.json";

    public static final String EER_SCHEMA_PATH = "src/main/resources/schemas/eerSchema.json";

    public static final String ORM_SCHEMA_PATH = "src/main/resources/schemas/ormSchema.json";

    public static final String META_SCHEMA_PATH = "src/main/resources/schemas/metaSchema.json";

    // Common Strings

    public static final String UML_STRING = "uml";

    public static final String EER_STRING = "eer";

    public static final String ORM_STRING = "orm";

    public static final String METAMODEL_STRING = "metamodel";

    public static final String ENTITY_STRING = "entity";

    public static final String RELATIONSHIP_STRING = "relationship";

    public static final String SUBSUMPTION_STRING = "subsumption";

    public static final String ROLE_STRING = "role";

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

    // Regex

    public static final String CARDINALITY_REGEX = "(\\d+|N|\\*)\\.\\.(\\d+|N|M|\\*)";

    public static final String CARDINALITY_DIVIDER_REGEX = "\\.\\.";

    // Error Messages

    public static final String ALREADY_EXIST_ENTITY_ERROR = "The given entitiy already exists in the metamodel";

    public static final String ALREADY_EXIST_RELATIONSHIP_ERROR = "The given relationship already exists in the metamodel";

    public static final String CARDINALITY_SYNTAX_ERROR = "Cardinality syntax error creating";

    public static final String INCONSISTENT_ROLES_WITH_CARDINALITIES_ERROR = "The number of roles is inconsistent with the number of cardinalities";

    public static final String ENTITY_NOT_FOUND_ERROR = "The given entity was not found or is not valid";

    public static final String ENTITIES_INFORMATION_NOT_FOUND_ERROR = "Information about the entities was not found";

    public static final String RELATIONSHIPS_INFORMATION_NOT_FOUND_ERROR = "Information about the relationships was not found";

    public static final String ROLES_INFORMATION_NOT_FOUND_ERROR = "Information about the roles was not found";

    public static final String CONSTRAINTS_INFORMATION_NOT_FOUND_ERROR = "Information about the constraints was not found";


}
