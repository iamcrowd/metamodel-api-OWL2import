package com.gilia.metamodel.entitytype.objecttype;

import com.gilia.metamodel.entitytype.EntityType;
import org.json.simple.JSONObject;

import java.util.ArrayList;

import static com.gilia.utils.Constants.*;

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

    /**
     * Generates a JSONObject that represents the information of the Object type according to the
     * UML language. The JSONObject generated respects the UML Schema.
     *
     * @return JSONObject that represents the equivalent UML Class.
     */
    public JSONObject toUML() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(KEY_NAME, this.name);
        jsonObject.put(KEY_ATTRS, new ArrayList());
        jsonObject.put(KEY_METHODS, new ArrayList());
        return jsonObject;
    }

    /**
     * Generates a JSONObject that represents the information of the Object type according to the
     * ORM language. The JSONObject generated respects the ORM Schema.
     *
     * @return JSONObject that represents the equivalent ORM Entity.
     */
    public JSONObject toORM() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(KEY_NAME, this.name);
        jsonObject.put(KEY_TYPE, ENTITY_STRING);
        return jsonObject;
    }

    /**
     * Generates a JSONObject that represents the information of the Object type according to the
     * EER language. The JSONObject generated respects the EER Schema.
     *
     * @return JSONObject that represents the equivalent EER Entity.
     */
    public JSONObject toEER() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(KEY_NAME, this.name);
        return jsonObject;
    }
}
