package com.gilia.metamodel.constraint.mandatory

import com.gilia.metamodel.role.Role
import org.json.simple.JSONObject
import spock.lang.Specification

import static com.gilia.utils.Constants.*

class MandatoryTest extends Specification {

    void 'Empty Mandatory creation'() {
        when:
        Mandatory newMandatory = new Mandatory()

        then:
        newMandatory.isNameless()
        newMandatory.getId() != null
    }

    void 'Mandatory creation with name'() {
        when:
        Mandatory newMandatory = new Mandatory("MyNewMandatory")

        then:
        newMandatory.getName() == "MyNewMandatory"
        newMandatory.getId() != null
    }

    void 'Mandatory creation with name and role'() {
        when:
        Role newRole = new Role("MyNewRole")
        Mandatory newMandatory = new Mandatory("MyNewMandatory", newRole)

        then:
        newMandatory.getName() == "MyNewMandatory"
        newMandatory.getDeclaredOn() == newRole
        newMandatory.getId() != null
    }

    void 'Set role to a Mandatory'() {
        given:
        Mandatory newMandatory = new Mandatory("MyNewMandatory")

        when:
        Role newRole = new Role("MyNewRole")
        newMandatory.setDeclaredOn(newRole)

        then:
        newMandatory.getName() == "MyNewMandatory"
        newMandatory.getDeclaredOn() == newRole
        newMandatory.getId() != null
    }

    void 'toJSONObject'() {
        given:
        Role newRole = new Role("MyNewRole")
        Mandatory newMandatory = new Mandatory("MyNewMandatory", newRole)

        when:
        JSONObject mandatoryJSON = newMandatory.toJSONObject()

        then:
        mandatoryJSON.containsKey(KEY_NAME)
        mandatoryJSON.get(KEY_NAME) == "MyNewMandatory"
        mandatoryJSON.containsKey(KEY_ROLE_DECLARED_ON)
        mandatoryJSON.get(KEY_ROLE_DECLARED_ON) == "MyNewRole"
    }
}
