package com.gilia.metamodel.constraint.mandatory;

import com.gilia.metamodel.role.Role;
import org.json.simple.JSONObject;

import static com.gilia.utils.Constants.KEY_NAME;
import static com.gilia.utils.Constants.KEY_ROLE_DECLARED_ON;

/**
 * Representation of the Mandatory class from the KF Metamodel
 *
 * @author Emiliano Rios Gavagnin
 */
public class Mandatory extends MandatoryConstraint {

    private Role declaredOn;

    public Mandatory() {
        super();
    }

    public Mandatory(String name) {
        super(name);
    }

    public Mandatory(String name, Role declaredOn) {
        super(name);
        this.declaredOn = declaredOn;
    }

    public Role getDeclaredOn() {
        return declaredOn;
    }

    public void setDeclaredOn(Role declaredOn) {
        this.declaredOn = declaredOn;
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(KEY_NAME, this.name);
        jsonObject.put(KEY_ROLE_DECLARED_ON, this.declaredOn.getName());
        return jsonObject;
    }
}
