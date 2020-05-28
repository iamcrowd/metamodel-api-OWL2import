package com.gilia.metamodel.entitytype;

import org.json.simple.JSONObject;

import java.util.ArrayList;

import static com.gilia.utils.Constants.*;

/**
 * @author Emiliano Rios Gavagnin
 */
public class DataType extends EntityType {

    /**
     * Creates a basic instance of a Data Type without a name. This method creates a Data type without name
     * but it generates the object with a valid id.
     */
    public DataType() {
        super();
    }

    /**
     * Creates a basic instance of a Data Type without a name.
     */
    public DataType(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return "DataType{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    /**
     * Creates a JSONObject with the relevant information about the Data Type object. This method is temporally deprecated
     * because the Data Type only contains the name string, and no JSONObject is required during the Metamodel JSON construction.
     * <p>
     * Example: <p>
     * { <p>
     * "name": "http://crowd.fi.uncoma.edu.ar#String" <p>
     * } <p>
     *
     * @return JSONObject with information about the Data Type object
     */
    @Deprecated
    public JSONObject toJSONObject() {
        JSONObject objectType = new JSONObject();
        objectType.put(KEY_NAME, name);
        return objectType;
    }

    /**
     * Generates a JSONObject that represents the information of the Data type according to the
     * UML language. The JSONObject generated respects the UML Schema.
     *
     * @return JSONObject that represents the equivalent UML data type.
     */
    public JSONObject toUML() {
        JSONObject jsonObject = new JSONObject();

        return jsonObject;
    }



}
