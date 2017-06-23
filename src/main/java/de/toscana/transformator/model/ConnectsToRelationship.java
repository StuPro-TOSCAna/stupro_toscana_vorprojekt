package de.toscana.transformator.model;

/**
 * This class reperesents the ConnectsTo relationship in a model.xml
 */
public class ConnectsToRelationship extends Relationship {

    private String implementationArtifact;

    public ConnectsToRelationship(org.w3c.dom.Node relElement, TOSCAliteModel model) throws ParsingException {
        super(relElement, model);
    }

    @Override
    protected void parseSpecific(org.w3c.dom.Node element) throws ParsingException {
        for (int i = 0; i < element.getChildNodes().getLength(); i++) {
            org.w3c.dom.Node child = element.getChildNodes().item(i);
            if (child.getNodeName().equals("ImplementationArtifact")) {
                implementationArtifact = child.getTextContent();
                System.out.println("Set implementation artifact for connectsTo relationship" +
                        " between: " + source.name + ", " + " and " + target.name);
            }
        }
    }

    @Override
    protected void updateNodes(Node source, Node target) throws ParsingException {
        if (source instanceof ServiceNode && target instanceof ServiceNode) {
            ((ServiceNode) source).getSourceConnections().add(this);
            ((ServiceNode) target).getTargetConnections().add(this);
        } else {
            throw new ParsingException("Invalid document." +
                    " Both Source and target have to be service nodes. " +
                    "(Source: " + source.name + ", " +
                    "Target: " + target.name + ")");
        }
    }

    public String getImplementationArtifact() {
        return implementationArtifact;
    }
}
