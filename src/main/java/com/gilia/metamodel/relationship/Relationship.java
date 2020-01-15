package com.gilia.metamodel.relationship;

import com.gilia.metamodel.Entity;
import com.gilia.metamodel.role.Role;

import java.util.ArrayList;

/**
 * @author Emiliano Rios Gavagnin
 */
public class Relationship extends Entity { // TODO: 1:1 Mapping

    protected ArrayList<String> entities;
    protected ArrayList<Role> roles;
}
