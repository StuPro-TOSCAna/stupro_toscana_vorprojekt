package de.toscana.transformator.model;

import org.w3c.dom.Element;

/**
 * This class represents a Hosted on Relationship.
 */
public class HostedOnRelationship extends Relationship {
    public HostedOnRelationship(org.w3c.dom.Node relElement, TOSCAliteModel model) throws ParsingException {
        super(relElement, model);
    }
}
