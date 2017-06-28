package de.toscana.transformator.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Root class to represent a topology node.
 */
public abstract class Node {
    /**
     * Stores the name of the node. As in toscalite this represents the unique identifier for this node.
     */
    protected String name;
    /**
     * Stores all key-value properties described in the read model.xml file.
     * This map is always created. If a node has no properties, the map will be empty.
     */
    protected Map<String, String> properties = new HashMap<>();
    /**
     * Stores the children of this node (HostedOn Relationship). if a node has no children this is a empty list
     */
    protected List<Node> children = new ArrayList<>();

    public Node(org.w3c.dom.Node nodeElement) throws ParsingException {
        if (!isValidElement(nodeElement)) {
            throw new ParsingException("The given Element is not a valid Node!");
        }
        //TODO: Implement Parsing Operations for a Node
        parseCommonData(nodeElement);
        parseSpecificData(nodeElement);
    }

    private void parseCommonData(org.w3c.dom.Node nodeElement) throws ParsingException {
        for (int i = 0; i < nodeElement.getChildNodes().getLength(); i++) {
            org.w3c.dom.Node child = nodeElement.getChildNodes().item(i);
            switch (child.getNodeName()) {
                case "Name":
                    if (isValidNodeName(child.getTextContent())) {
                        name = child.getTextContent();
                    } else {
                        throw new ParsingException("Invalid document." +
                                " The node name " + child.getTextContent() + " is invalid!");
                    }
                    break;
                case "Properties":
                    parseProperties(child);
                    break;
            }
        }
    }

    protected void parseProperties(org.w3c.dom.Node nodeElement) throws ParsingException {
        for (int i = 0; i < nodeElement.getChildNodes().getLength(); i++) {
            org.w3c.dom.Node child = nodeElement.getChildNodes().item(i);
            if(child.getNodeName().equals("Property")) {
                parseProperty( child);
            }
        }
        System.out.println("Parsed properties for node "+name);
    }

    private void parseProperty(org.w3c.dom.Node child) throws ParsingException {
        String key = null;
        for (int j = 0; j < child.getAttributes().getLength(); j++) {
            org.w3c.dom.Node node = child.getAttributes().item(j);
            if (node == null || node.getNodeName() == null) {
                continue;
            }
            if (node.getNodeName().equalsIgnoreCase("key")) {
                key = node.getNodeValue();
            }
        }
        if (key == null) {
            throw new ParsingException("Invalid document." +
                    " Every property needs a key. Error occured in node: " + name);
        }
        properties.put(key, child.getTextContent());
    }

    private boolean isValidNodeName(String textContent) {
        char[] validChars = "abcdefghijklmnopqrstuvwxyz-_1234567890".toCharArray();
        int validCharCount = 0;
        for (char c : textContent.toCharArray()) {
            for (char validChar : validChars) {
                if (c == validChar) {
                    validCharCount++;
                }
            }
        }
        return validCharCount == textContent.length();
    }

    private boolean isValidElement(org.w3c.dom.Node nodeElement) {
        int paramCount = 0;
        for (int i = 0; i < nodeElement.getChildNodes().getLength(); i++) {
            org.w3c.dom.Node child = nodeElement.getChildNodes().item(i);
            switch (child.getNodeName()) {
                case "Name":
                    paramCount++;
                    break;
                default:
                    break;
            }
        }
        return paramCount == 1 && isParsable(nodeElement);
    }

    /**
     * This method has to implement parsing operations specific to the type of node being implemented.
     *
     * @param element The DOM element to parse From
     */
    protected abstract void parseSpecificData(org.w3c.dom.Node element) throws ParsingException;

    /**
     * This method has to check if a node is valid to be parsed.
     * It has to check if all the required elements/attributes needed for successful parsing are available.
     *
     * @param element The element to check on
     * @return True if the Element is parsable, false otherwise.
     */
    protected abstract boolean isParsable(org.w3c.dom.Node element);

    protected void addChild(Node child) {
        children.add(child);
    }

    /**
     * @return the name attribute (see name attribute)
     */
    public String getName() {
        return name;
    }

    /**
     * @return the Properties map (See properties attribute)
     */
    public Map<String, String> getProperties() {
        return properties;
    }

    /**
     * @return The list of children of this node. if the node has no children the list is empty.
     */
    public List<Node> getChildren() {
        return children;
    }
}
