package de.toscana.transformator.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class reperesents the ConnectsTo relationship in a model.xml
 */
public class ConnectsToRelationship extends Relationship {

    private static Logger LOG = LoggerFactory.getLogger(ConnectsToRelationship.class);

    private static final String IMPLEMENTATION_ARTIFACT_ELEMENT_NAME = "ImplementationArtifact";
    private ArtifactPath implementationArtifact;

    public ConnectsToRelationship(org.w3c.dom.Node relElement, TOSCAliteModel model) throws ParsingException {
        super(relElement, model);
    }

    @Override
    protected void parseSpecific(org.w3c.dom.Node element) throws ParsingException {
        for (int i = 0; i < element.getChildNodes().getLength(); i++) {
            org.w3c.dom.Node child = element.getChildNodes().item(i);
            if (child.getNodeName().equals(IMPLEMENTATION_ARTIFACT_ELEMENT_NAME)) {
                implementationArtifact = new ArtifactPath(child.getTextContent(), this);
                LOG.debug("Set implementation artifact for connectsTo relationship" +
                        " between: {} and {}.", source.name, target.name);
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

    public ArtifactPath getImplementationArtifact() {
        return implementationArtifact;
    }
}
