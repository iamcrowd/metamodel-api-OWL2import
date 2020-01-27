package com.gilia.builder;

import com.gilia.builder.jsontranslator.*;
import com.gilia.builder.metabuilder.*;
import com.gilia.exceptions.NoMetamodelCreatedException;
import com.gilia.metamodel.Metamodel;
import org.json.simple.JSONObject;

/**
 * Representation of the director class in the Builder design pattern. This class is in charge of managing
 * the translation between models and the metamodel itself. It contains a JSONTranslator, which generates
 * the metamodel instance from a JSON, and it contains a MetaBuilder, which generates a JSON from the metamodel
 * instance.
 *
 * @author Emiliano Rios Gavagnin
 */
public class MetaDirector {

    private Metamodel metamodel;
    private JSONTranslator translator;
    private MetaBuilder builder;

    public MetaDirector() {
        this.metamodel = null;
        this.translator = null;
        this.builder = null;
    }

    public Metamodel getMetamodel() {
        return metamodel;
    }

    public JSONTranslator getTranslator() {
        return translator;
    }

    public MetaBuilder getBuilder() {
        return builder;
    }

    public void createMetamodelFromUML(JSONObject json) {
        this.translator = new UMLTranslator();
        this.metamodel = translator.createMetamodel(json);
    }

    public void createMetamodelFromEER(JSONObject json) {
        this.translator = new EERTranslator();
        this.metamodel = translator.createMetamodel(json);
    }

    public void createMetamodelFromORM(JSONObject json) {
        this.translator = new ORMTranslator();
        this.metamodel = translator.createMetamodel(json);
    }

    public void createMetamodelFromMeta(JSONObject json) {
        this.translator = new MetaTranslator();
        this.metamodel = translator.createMetamodel(json);
    }

    public JSONObject generateUML() throws NoMetamodelCreatedException {
        if (metamodel != null) {
            builder = new UMLConverter();
            return builder.generateJSON(metamodel);
        } else {
            throw new NoMetamodelCreatedException("UML can not be generated, no Metamodel created");
        }
    }

    public JSONObject generateEER() throws NoMetamodelCreatedException {
        if (metamodel != null) {
            builder = new EERConverter();
            return builder.generateJSON(metamodel);
        } else {
            throw new NoMetamodelCreatedException("EER can not be generated, no Metamodel created");
        }
    }

    public JSONObject generateORM() throws NoMetamodelCreatedException {
        if (metamodel != null) {
            builder = new ORMConverter();
            return builder.generateJSON(metamodel);
        } else {
            throw new NoMetamodelCreatedException("ORM can not be generated, no Metamodel created");
        }
    }

    public JSONObject generateMeta() throws NoMetamodelCreatedException {
        if (metamodel != null) {
            builder = new MetaConverter();
            return builder.generateJSON(metamodel);
        } else {
            throw new NoMetamodelCreatedException("Metamodel can not be generated, no Metamodel created");
        }
    }
}
