package de.toscana.transformator.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static Logger LOG = LoggerFactory.getLogger(TOSCAliteModel.class);

    private static final String MODEL_ELEMENT_NAME = "Model";
    private static final String NODES_ELEMENT_NAME = "Nodes";
    private static final String RELATIONSHIPS_ELEMENT_NAME = "Relationships";
    private static final String NODE_ELEMENT_NAME = "Node";
    private static final String NODE_TYPE_MACHINE_NAME = "machine";
    private static final String NODE_TYPE_SERVICE_NAME = "service";
    private static final String RELATIONSHIP_ELEMENT_KEY = "Relationship";
    private static final String HOSTED_ON_RELATIONSHIP_TYPE = "hostedOn";
    private static final String CONNECTS_TO_RELATIONSHIP_TYPE = "connectsTo";
    private static final String TYPE_ELEMENT_NAME = "Type";

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
        //initialze document builder and document
        LOG.debug("Initializing parser");
        Document document = null;
        Element root = null;
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = builder.parse(new ByteArrayInputStream(xmlContent.getBytes())); //TODO Rethink reading of the XML document
            root = document.getDocumentElement();
        } catch (ParserConfigurationException | SAXException |  IOException e) {
            e.printStackTrace();
            throw new ParsingException("Parser could not initialize properly. The model might be invalid.",e);
        }
        LOG.debug("Parsing");
        if (!root.getNodeName().equals(MODEL_ELEMENT_NAME)) {
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
        LOG.debug("Parsing nodes");
        for (int i = 0; i < e.getChildNodes().getLength(); i++) {
            org.w3c.dom.Node n = e.getChildNodes().item(i);
            if (n.getNodeName().equals(NODE_ELEMENT_NAME)) {
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
            case NODE_TYPE_MACHINE_NAME:
                node = new MachineNode(n);
                machines.add((MachineNode) node);
                break;
            case NODE_TYPE_SERVICE_NAME:
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
        LOG.debug("Added {} {}", type, node.name);
    }

    private String determineElementType(org.w3c.dom.Node n) {
        for (int j = 0; j < n.getChildNodes().getLength(); j++) {
            org.w3c.dom.Node inner = n.getChildNodes().item(j);
            if (inner.getNodeName().equals(TYPE_ELEMENT_NAME)) {
                return inner.getTextContent();
            }
        }
        return null;
    }

    private void parseRelationships(org.w3c.dom.Node e) throws ParsingException {
        LOG.debug("Parsing relationships");
        for (int i = 0; i < e.getChildNodes().getLength(); i++) {
            org.w3c.dom.Node n = e.getChildNodes().item(i);
            if (n.getNodeName().equals(RELATIONSHIP_ELEMENT_KEY)) {
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
            case HOSTED_ON_RELATIONSHIP_TYPE:
                relationship = new HostedOnRelationship(node, this);
                break;
            case CONNECTS_TO_RELATIONSHIP_TYPE:
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
        LOG.debug("Parsed {} relationship from {} to {}", type,
                relationship.source.name,
                relationship.target.name);
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
