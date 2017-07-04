package de.toscana.transformator.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents a service node.
 */
public class ServiceNode extends Node {

    public static final String DEPLOYMENT_ARTIFACT_ATRIBUTE_NAME = "key";
    private static Logger LOG = LoggerFactory.getLogger(ServiceNode.class);

    private static final String IMPLEMENTATION_ARTIFACTS_ELEMENT_NAME = "ImplementationArtifacts";
    private static final String DEPLOYMENT_ARTIFACTS_ELEMENT_NAME = "DeploymentArtifacts";
    private static final String DEPLOYMENT_ARTIFACT_ELEMENT_NAME = "DeploymentArtifact";

    private Map<String, ArtifactPath> deploymentArtifacts;
    private Map<ArtifactType, ArtifactPath> implementationArtifacts;
    private Node parent;
    private List<ConnectsToRelationship> sourceConnections = new ArrayList<>();
    private List<ConnectsToRelationship> targetConnections = new ArrayList<>();


    public ServiceNode(org.w3c.dom.Node nodeElement) throws ParsingException {
        super(nodeElement);
    }

    @Override
    protected void parseSpecificData(org.w3c.dom.Node element) throws ParsingException {
        //Initialize storage collections. This has to be done here instead of the declaration
        //because this gets executed by the parent constructor
        implementationArtifacts = new HashMap<>();
        deploymentArtifacts = new HashMap<>();
        //Parse specific data
        parseImplementationArtifacts(element);
        parseDeploymentArtifacts(element);
    }

    private void parseImplementationArtifacts(org.w3c.dom.Node element) throws ParsingException {
        for (int i = 0; i < element.getChildNodes().getLength(); i++) {
            org.w3c.dom.Node child = element.getChildNodes().item(i);
            if (child.getNodeName().equals(IMPLEMENTATION_ARTIFACTS_ELEMENT_NAME)) {
                for (int j = 0; j < child.getChildNodes().getLength(); j++) {
                    org.w3c.dom.Node innerChild = child.getChildNodes().item(j);
                    try {
                        ArtifactType type = ArtifactType.getByNodeName(innerChild.getNodeName());
                        implementationArtifacts.put(type, new ArtifactPath(innerChild.getTextContent(), this));
                    } catch (IllegalArgumentException e) {
                        throw new ParsingException("Invalid document." +
                                " The Implementation Artifact type " + innerChild.getNodeName()
                                + " is invalid!");
                    }
                }
            }
        }
        LOG.debug("Parsed {} implementation artifacts for node {}.", implementationArtifacts.size(), name);
    }

    private void parseDeploymentArtifacts(org.w3c.dom.Node element) throws ParsingException {
        for (int i = 0; i < element.getChildNodes().getLength(); i++) {
            org.w3c.dom.Node child = element.getChildNodes().item(i);
            if (child.getNodeName().equals(DEPLOYMENT_ARTIFACTS_ELEMENT_NAME)) {
                for (int j = 0; j < child.getChildNodes().getLength(); j++) {
                    org.w3c.dom.Node innerChild = child.getChildNodes().item(j);

                    String key = getKeyAttribute(innerChild);

                    if (key == null | !isKeyValid(key)) {
                        throw new ParsingException("Invalid document." +
                                " Every deployment artifact needs a key." +
                                " The error occured on node " + name);
                    }

                    if (innerChild.getNodeName().equals(DEPLOYMENT_ARTIFACT_ELEMENT_NAME)) {
                        deploymentArtifacts.put(key, new ArtifactPath(innerChild.getTextContent(), this));
                    } else {
                        throw new ParsingException("Invalid document." +
                                " Elements in the deployment artifacts" +
                                " list have to be called DeploymentArtifact." +
                                " The error occured on node " + name);
                    }
                }
            }
        }
        LOG.debug("Parsed {} deployment artifacts for node {}.", deploymentArtifacts.size(), name);
    }


    @Override
    protected boolean isParsable(org.w3c.dom.Node element) {
        //Check if a Create implementation artifact exists
        boolean createFound = false;
        for (int i = 0; i < element.getChildNodes().getLength(); i++) {
            org.w3c.dom.Node child = element.getChildNodes().item(i);
            if (child.getNodeName().equals(IMPLEMENTATION_ARTIFACTS_ELEMENT_NAME)) {
                for (int j = 0; j < child.getChildNodes().getLength(); j++) {
                    org.w3c.dom.Node innerChild = child.getChildNodes().item(j);
                    if (innerChild.getNodeName().equals(ArtifactType.CREATE.getElementName())) {
                        createFound = true;
                        break;
                    }
                }
                if (createFound) {
                    break;
                }
            }
        }

        return createFound;
    }

    public boolean hasImplementationArtifact(ArtifactType type) {
        return implementationArtifacts.containsKey(type);
    }

    public ArtifactPath getImplementationArtifact(ArtifactType type) {
        if (hasImplementationArtifact(type)) {
            return implementationArtifacts.get(type);
        }
        return null;
    }

    public Map<String, ArtifactPath> getDeploymentArtifacts() {
        return deploymentArtifacts;
    }

    public Node getParent() {
        return parent;
    }

    protected void setParent(Node parent) {
        this.parent = parent;
    }

    public List<ConnectsToRelationship> getSourceConnections() {
        return sourceConnections;
    }

    public List<ConnectsToRelationship> getTargetConnections() {
        return targetConnections;
    }
}
