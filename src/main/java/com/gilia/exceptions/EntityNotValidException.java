package com.gilia.exceptions;

/**
 * Exception for handling the problem of a not valid Entity reference. Not to be confused with AlreadyExistException and InformationNotFoundException.
 * This exception should be used when an entity is being referenced but it does not exist in the Metamodel or it can not be used in a given representation.
 * <p>
 * For example:  A subsumption should have an entity parent and an entity child. Both this entities can not be Constraints. Therefore, if a JSON describes
 * a situation where an entity parent is a constraint, then this exception should be thrown.
 *
 * @see AlreadyExistException
 * @see InformationNotFoundException
 */
public class EntityNotValidException extends MetamodelException {
    public EntityNotValidException(String message) {
        super("EntityNotValidException - " + message);
    }
}
