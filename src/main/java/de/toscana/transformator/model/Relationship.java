package de.toscana.transformator.model;

import org.w3c.dom.Element;

/**
 * This class is the base class representing the common properties of a relationship
 */
public abstract class Relationship {

    protected Node source;
    protected Node target;

    public Relationship(Element relElement, TOSCAliteModel model) throws ParsingException{
        //TODO Implement relationship parsing
        parseSpecific(relElement);
    }

    protected void parseSpecific(Element element) throws ParsingException {

    }

    public Node getSource() {
        return source;
    }

    public Node getTarget() {
        return target;
    }
}
