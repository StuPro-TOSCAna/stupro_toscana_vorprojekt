package de.toscana.transformator.model;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents a service node.
 */
public class ServiceNode extends Node{

    private List<String> deploymentArtifacts = new ArrayList<>();
    private Map<ArtifactType, String> implementationArtifacts = new HashMap<>();
    private Node parent;
    private List<ConnectsToRelationship> sourceConnections = new ArrayList<>();
    private List<ConnectsToRelationship> targetConnections = new ArrayList<>();


    public ServiceNode(org.w3c.dom.Node nodeElement) throws ParsingException {
        super(nodeElement);
    }

    @Override
    protected void parseSpecificData(org.w3c.dom.Node element) throws ParsingException {
        parseImplementationArtifacts(element);
        parseDeploymentArtifacts(element);
    }

    private void parseDeploymentArtifacts(org.w3c.dom.Node element) throws ParsingException{
        
    }

    private void parseImplementationArtifacts(org.w3c.dom.Node element) throws ParsingException {

    }

    @Override
    protected boolean isParsable(org.w3c.dom.Node element) {
        //Check if a Create implementation artifact exists
        boolean createFound= false;
        for (int i = 0; i < element.getChildNodes().getLength(); i++) {
            org.w3c.dom.Node child = element.getChildNodes().item(i);
            if(child.getNodeName().equals("ImplementationArtifacts")) {
                System.out.println(child.getNodeName());
                for (int j = 0; j < child.getChildNodes().getLength(); j++) {
                    org.w3c.dom.Node innerChild = child.getChildNodes().item(j);
                    System.out.println(innerChild.getNodeName());
                    if(innerChild.getNodeName().equals(ArtifactType.CREATE.getElementName())) {
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

    public String getImplementationArtifact(ArtifactType type) {
        if(hasImplementationArtifact(type)) {
            return implementationArtifacts.get(type);
        }
        return null;
    }

    public List<String> getDeploymentArtifacts() {
        return deploymentArtifacts;
    }

    public Node getParent() {
        return parent;
    }

    public List<ConnectsToRelationship> getSourceConnections() {
        return sourceConnections;
    }

    public List<ConnectsToRelationship> getTargetConnections() {
        return targetConnections;
    }
}
