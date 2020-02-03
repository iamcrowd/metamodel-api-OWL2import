package com.gilia.metamodel.entitytype.objecttype;

import com.gilia.metamodel.entitytype.EntityType;
import org.json.simple.JSONObject;

import static com.gilia.utils.Constants.KEY_NAME;

/**
 * Representation of the ObjectType class from the KF Metamodel
 *
 * @author Emiliano Rios Gavagnin
 */
public class ObjectType extends EntityType {

    /**
     * Creates a basic instance of an Object Type without a name. This method creates an Object type without name
     * but it generates the object with a valid id.
     */
    public ObjectType() {
        super();
    }

    /**
     * Creates an instance of an Object Type.
     *
     * @param name String that represents the name of the Object Type to be created
     */
    public ObjectType(String name) {
        super(name);
    }

    public String toString() {
        return "ObjectType{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    /**
     * Creates a JSONObject with the relevant information about the Object Type object. This method is temporally deprecated
     * because the Object Type only contains the name string, and no JSONObject is required during the Metamodel JSON construction.
     * <p>
     * Example: <p>
     * { <p>
     * "name": "http://crowd.fi.uncoma.edu.ar#Class2" <p>
     * } <p>
     *
     * @return JSONObject with information about the Object Type object
     */
    @Deprecated
    public JSONObject toJSONObject() {
        JSONObject objectType = new JSONObject();
        objectType.put(KEY_NAME, name);
        return objectType;
    }
}
