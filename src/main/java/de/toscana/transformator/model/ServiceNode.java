package de.toscana.transformator.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents a service node.
 */
public class ServiceNode extends Node {

    private static final String IMPLEMENTATION_ARTIFACTS_ELEMENT_NAME = "ImplementationArtifacts";
    private static final String DEPLOYMENT_ARTIFACTS_ELEMENT_NAME = "DeploymentArtifacts";
    private static final String DEPLOYMENT_ARTIFACT_ELEMENT_NAME = "DeploymentArtifact";

    private List<ArtifactPath> deploymentArtifacts;
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
        deploymentArtifacts = new ArrayList<>();
        //Parse specific data
        parseImplementationArtifacts(element);
        parseDeploymentArtifacts(element);
    }

    private void parseImplementationArtifacts(org.w3c.dom.Node element) throws ParsingException {
        System.out.print("Parsing implementation artifacts for " + name + "... ");
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
        System.out.println("Parsed " + implementationArtifacts.size() + " artifacts.");
    }

    private void parseDeploymentArtifacts(org.w3c.dom.Node element) throws ParsingException {
        System.out.print("Parsing deployment artifacts for " + name + "... ");
        for (int i = 0; i < element.getChildNodes().getLength(); i++) {
            org.w3c.dom.Node child = element.getChildNodes().item(i);
            if (child.getNodeName().equals(DEPLOYMENT_ARTIFACTS_ELEMENT_NAME)) {
                for (int j = 0; j < child.getChildNodes().getLength(); j++) {
                    org.w3c.dom.Node innerChild = child.getChildNodes().item(j);
                    if (innerChild.getNodeName().equals(DEPLOYMENT_ARTIFACT_ELEMENT_NAME)) {
                        deploymentArtifacts.add(new ArtifactPath(innerChild.getTextContent(), this));
                    } else {
                        throw new ParsingException("Invalid document." +
                                " Elements in the deployment artifacts" +
                                " list have to be called DeploymentArtifact." +
                                " The error occured on node " + name);
                    }
                }
            }
        }
        System.out.println("Parsed " + deploymentArtifacts.size() + " artifacts.");
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

    public List<ArtifactPath> getDeploymentArtifacts() {
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
