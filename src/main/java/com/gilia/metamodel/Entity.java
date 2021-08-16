package com.gilia.metamodel;

import java.util.Objects;
import java.util.UUID;

import simplenlg.framework.NLGFactory;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.realiser.english.Realiser;

/**
 * Representation of the Entity class from the KF Metamodel
 *
 * @author Emiliano Rios Gavagnin
 */
public abstract class Entity {

    protected final String id;
    protected String name;
    
	protected Lexicon lexicon = Lexicon.getDefaultLexicon();
	protected NLGFactory nlgFactory = new NLGFactory(lexicon);
	protected Realiser realiser = new Realiser(lexicon);
	protected SPhraseSpec cnl = nlgFactory.createClause();

    public Entity() {
        this.id = UUID.randomUUID().toString();
        this.name = "";
    }

    public Entity(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        if (Objects.equals(id, entity.id)) return true;
        return Objects.equals(name, entity.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    public boolean isNameless() { // Used mainly for checking existence. It is a way to avoid checking/returning nulls
        return name.equals("") || name == null;
    }
    
    /**
     * 
     * @return
     */
    public void toCNLen() {
  	  this.cnl.setSubject(this.name);
  	  this.cnl.setVerb("is");
  	  this.cnl.setObject("an Entity");
    }
    
    public String getCNLen() {
    	String output = this.realiser.realiseSentence(this.cnl);
    	return output;
    }
}
