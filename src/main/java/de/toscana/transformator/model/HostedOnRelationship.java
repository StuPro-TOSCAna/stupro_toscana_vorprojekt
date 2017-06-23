package de.toscana.transformator.model;

/**
 * This class represents a Hosted on Relationship.
 */
public class HostedOnRelationship extends Relationship {
    public HostedOnRelationship(org.w3c.dom.Node relElement, TOSCAliteModel model) throws ParsingException {
        super(relElement, model);
    }

    @Override
    protected void updateNodes(Node source, Node target) throws ParsingException {
        if (source instanceof ServiceNode) {
            ((ServiceNode) source).setParent(target);
            target.addChild(source);
        } else {
            throw new ParsingException("Invalid document." +
                    "The source of a hostedOn relationship " +
                    "has to be a service node. (Source: " + source.name + ", " +
                    "Target: " + target.name + ")");
        }
    }
}
