package com.gilia.metastrategies;

import com.gilia.metamodel.Metamodel;
import org.json.simple.JSONObject;

/**
 *
 * @author emiliano
 */
@Deprecated
public abstract class Metastrategy {

    protected Metamodel model;

    protected Metastrategy() {
        model = new Metamodel();
    }

    public Metamodel getModel() {
        return model;
    }

    public void setModel(Metamodel model) {
        this.model = model;
    }


    /**
     *
     * @return
     */
    public JSONObject createModel(JSONObject model) {
        return null;
    }

}
