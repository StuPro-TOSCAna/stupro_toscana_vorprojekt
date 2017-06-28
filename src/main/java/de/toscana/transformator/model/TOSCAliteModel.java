package de.toscana.transformator.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents a complete, parsed topology.
 */
public class TOSCAliteModel {

    private static final String NODES_ELEMENT_NAME = "Nodes";
    private static final String RELATIONSHIPS_ELEMENT_NAME = "Relationships";

    /**
     * Stores all nodes in the Topology. Maps name to the node instance
     */
    private Map<String, Node> nodes;
    /**
     * Stores all Relationship instances
     */
    private List<Relationship> relationships;
    /**
     * This list stores all machine nodes in the parsed topology
     */
    private List<MachineNode> machines;

    /**
     * Given the contents of a model.xml this constructor parses a Object representation of the model.xml
     *
     * @param xmlContent Text content of a model.xml file
     */
    public TOSCAliteModel(String xmlContent) throws ParsingException {
        initializeAttributes();
        //TODO Implement Parsing
        //initialze document builder and document
        System.out.println("Initializing parser");
        Document document = null;
        Element root = null;
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = builder.parse(new ByteArrayInputStream(xmlContent.getBytes())); //TODO Rethink reading of the XML document
            root = document.getDocumentElement();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            throw new ParsingException("Parser could not initialize properly", e);
        }
        System.out.println("Parsing");
        if (!root.getNodeName().equals("Model")) {
            throw new ParsingException("Invalid document. Root element has to be called \"Model\"");
        }
        //Read the Root nodes chilren and parse the nodes. after the nodes
        //the relationships will be parsed, because they need the nodes created in order to link them.
        for (int i = 0; i < root.getChildNodes().getLength(); i++) {
            org.w3c.dom.Node e = root.getChildNodes().item(i);
            if (e.getNodeName().equals(NODES_ELEMENT_NAME)) {
                parseNodes(e);
                break;
            }
        }
        for (int i = 0; i < root.getChildNodes().getLength(); i++) {
            org.w3c.dom.Node e = root.getChildNodes().item(i);
            if (e.getNodeName().equals(RELATIONSHIPS_ELEMENT_NAME)) {
                parseRelationships(e);
                break;
            }
        }
    }

    private void parseNodes(org.w3c.dom.Node e) throws ParsingException {
        System.out.println("Parsing nodes");
        for (int i = 0; i < e.getChildNodes().getLength(); i++) {
            org.w3c.dom.Node n = e.getChildNodes().item(i);
            if (n.getNodeName().equals("Node")) {
                String type = determineElementType(n);
                parseNode(n, type);
            } else {
                throw new ParsingException("Invalid document." +
                        " Only \"Node\" elements are allowed" +
                        " in the \"Nodes\" block.");
            }
        }
    }

    private void parseNode(org.w3c.dom.Node n, String type) throws ParsingException {
        if (type == null) {
            throw new ParsingException("Invalid document." +
                    " Node type not found!");
        }
        Node node = null;
        switch (type) {
            case "machine":
                node = new MachineNode(n);
                machines.add((MachineNode) node);
                break;
            case "service":
                node = new ServiceNode(n);
                break;
            default:
                break;
        }
        if (node == null) {
            throw new ParsingException("Invalid document." +
                    " The node type " + type + " is not allowed!");
        }
        nodes.put(node.getName(), node);
        System.out.println("Added " + node.getName() + " " + type);
    }

    private String determineElementType(org.w3c.dom.Node n) {
        for (int j = 0; j < n.getChildNodes().getLength(); j++) {
            org.w3c.dom.Node inner = n.getChildNodes().item(j);
            if (inner.getNodeName().equals("Type")) {
                return inner.getTextContent();
            }
        }
        return null;
    }

    private void parseRelationships(org.w3c.dom.Node e) throws ParsingException {
        System.out.println("Parsing relationships");
        for (int i = 0; i < e.getChildNodes().getLength(); i++) {
            org.w3c.dom.Node n = e.getChildNodes().item(i);
            if (n.getNodeName().equals("Relationship")) {
                String type = determineElementType(n);
                parseRelationship(n, type);
            } else {
                throw new ParsingException("Invalid document." +
                        " Only \"Relationship\" elements are " +
                        "allowed in the \"Relationships\" block.");
            }
        }
    }

    private void parseRelationship(org.w3c.dom.Node node, String type) throws ParsingException {
        if (type == null) {
            throw new ParsingException("Invalid document." +
                    " Relationship type not found!");
        }
        Relationship relationship = null;
        switch (type) {
            case "hostedOn":
                relationship = new HostedOnRelationship(node, this);
                break;
            case "connectsTo":
                relationship = new ConnectsToRelationship(node, this);
                break;
            default:
                break;
        }
        if (relationship == null) {
            throw new ParsingException("Invalid document." +
                    " The relationship type " + type + " is not allowed!");
        }
        relationships.add(relationship);
        System.out.println("Parsed " + type + " relationship from "
                + relationship.source.getName() + " to "
                + relationship.target.getName());
    }

    private void initializeAttributes() {
        nodes = new HashMap<>();
        relationships = new ArrayList<>();
        machines = new ArrayList<>();
    }

    /**
     * Returns the Node instance for a given name.
     * If a node does not exist with the given name null gets returned
     */
    public Node getNodeByName(String name) {
        return nodes.get(name);
    }

    public Map<String, Node> getNodes() {
        return nodes;
    }

    public List<Relationship> getRelationships() {
        return relationships;
    }

    public List<MachineNode> getMachines() {
        return machines;
    }
}
