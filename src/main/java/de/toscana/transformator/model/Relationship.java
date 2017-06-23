package de.toscana.transformator.model;

import static de.toscana.transformator.util.CheckUtils.checkNull;

/**
 * This class is the base class representing the common properties of a relationship
 */
public abstract class Relationship {

    /**
     * Stores the instance of the source node
     */
    protected Node source;
    /**
     * Stores the instance of the target node
     */
    protected Node target;

    /**
     * Parses a relationship from the given DOM element
     *
     * @param relElement the DOM element to parse from
     * @param model      needed to get Node instances by name1
     * @throws ParsingException gets thrown if the element cannot get parsed into a java object.
     *                          For example: the element is no Relationship
     */
    public Relationship(org.w3c.dom.Node relElement, TOSCAliteModel model) throws ParsingException {
        String sourceName = null;
        String targetName = null;
        for (int i = 0; i < relElement.getChildNodes().getLength(); i++) {
            org.w3c.dom.Node n = relElement.getChildNodes().item(i);
            switch (n.getNodeName()) {
                case "Source":
                    sourceName = n.getTextContent();
                    break;
                case "Target":
                    targetName = n.getTextContent();
                    break;
                default:
                    break;
            }
        }
        try {
            source = model.getNodeByName(sourceName);
            target = model.getNodeByName(targetName);
            if (!checkNull(source, target)) {
                throw new IllegalArgumentException("Cannot find Source or Target!");
            }
        } catch (RuntimeException e) {
            throw new ParsingException("Invalid document." +
                    " Could not parse relationship from "
                    + sourceName + " to " + targetName);
        }
        parseSpecific(relElement);
        updateNodes(source, target);
    }

    /**
     * This method gets called during the parsing process. It should be used to parse all
     * relationship specific Elements/attributes
     *
     * @param element The element to Parse from
     * @throws ParsingException see Constructor
     */
    protected void parseSpecific(org.w3c.dom.Node element) throws ParsingException {
        //Not implemented here, the implementation of this method is Optional!
    }

    protected abstract void updateNodes(Node source, Node target) throws ParsingException;

    public Node getSource() {
        return source;
    }

    public Node getTarget() {
        return target;
    }
}
