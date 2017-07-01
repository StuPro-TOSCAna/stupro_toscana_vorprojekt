package de.toscana.transformator.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Root class to represent a topology node.
 */
public abstract class Node {

    private static Logger LOG = LoggerFactory.getLogger(ServiceNode.class);

    //Externalized Strings
    private static final String NAME_VALID_CHARACTERS = "abcdefghijklmnopqrstuvwxyz-_1234567890";
    private static final String PROPERTY_KEY_VALID_CHARACTERS = "abcdefghijklmnopqrstuvwxyz" +
            "abcdefghijklmnopqrstuvwxyz".toUpperCase();
    private static final String[] INVALID_NODE_NAMES = {"relationships"};
    private static final String PROPERTY_ELEMENT_NAME = "Property";
    private static final String PROPERTY_KEY_ATTRIBUTE_NAME = "key";
    private static final String PROPERTIES_ELEMENT_NAME = "Properties";
    private static final String NAME_ELEMENT_NAME = "Name";
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

    /**
     * Parses a new node from a given XML node
     *
     * @param nodeElement the XML node to parse from
     * @throws ParsingException gets thrown if the given nodeElement does not match
     *                          the requirements defined in the spec
     */
    public Node(org.w3c.dom.Node nodeElement) throws ParsingException {
        if (!isValidElement(nodeElement)) {
            throw new ParsingException("The given Element is not a valid Node!");
        }
        parseCommonData(nodeElement);
        parseSpecificData(nodeElement);
    }

    /**
     * This method parses the common data of every node i.e. Name and properties
     *
     * @param nodeElement the node element to parse from
     * @throws ParsingException gets thrown if the given node is not parsable
     */
    private void parseCommonData(org.w3c.dom.Node nodeElement) throws ParsingException {
        for (int i = 0; i < nodeElement.getChildNodes().getLength(); i++) {
            org.w3c.dom.Node child = nodeElement.getChildNodes().item(i);
            switch (child.getNodeName()) {
                case NAME_ELEMENT_NAME:
                    if (isValidNodeName(child.getTextContent())) {
                        name = child.getTextContent();
                    } else {
                        throw new ParsingException("Invalid document." +
                                " The node name " + child.getTextContent() + " is invalid!");
                    }
                    break;
                case PROPERTIES_ELEMENT_NAME:
                    parseProperties(child);
                    break;
            }
        }
    }

    /**
     * Parses all properties of a Properties node
     *
     * @param nodeElement the properties node to parse from
     * @throws ParsingException gets thrown if something went wrong while parsing (Usualy invalid input data)
     */
    protected void parseProperties(org.w3c.dom.Node nodeElement) throws ParsingException {
        for (int i = 0; i < nodeElement.getChildNodes().getLength(); i++) {
            org.w3c.dom.Node child = nodeElement.getChildNodes().item(i);
            if (child.getNodeName().equals(PROPERTY_ELEMENT_NAME)) {
                parseProperty(child);
            }
        }
        LOG.debug("Parsed properties for node {}.", name);
    }

    /**
     * This method parses a single property node and adds its key/value into the property map
     * it throws a ParsingException if the Key of the property is invalid
     *
     * @param child the node to analyze and add from
     * @throws ParsingException see description
     */
    private void parseProperty(org.w3c.dom.Node child) throws ParsingException {
        String key = null;
        for (int j = 0; j < child.getAttributes().getLength(); j++) {
            org.w3c.dom.Node node = child.getAttributes().item(j);
            if (node == null || node.getNodeName() == null) {
                continue;
            }
            if (node.getNodeName().equals(PROPERTY_KEY_ATTRIBUTE_NAME)) {
                key = node.getNodeValue();
            }
        }
        if (key == null || !isKeyValid(key)) {
            throw new ParsingException("Invalid document." +
                    " A Error occured in node: " + name + " while parsing the property key " + key);
        }
        properties.put(key, child.getTextContent());
    }

    /**
     * Checks a given key for the valid charset
     *
     * @param key the key to check
     * @return true if all characters in the key are in the valid charset.
     */
    private boolean isKeyValid(String key) {
        char[] keys = PROPERTY_KEY_VALID_CHARACTERS.toCharArray();
        int cnt = getValidCharCount(key, keys);
        return cnt == key.length();
    }

    /**
     * Checks if a node name matches the requirements: Charset and against a list of invalid names
     *
     * @param textContent the string to check
     * @return true if the string ist not on the list of forbidden names and all chars
     * are in the allowed charset
     */
    private boolean isValidNodeName(String textContent) {
        char[] validChars = NAME_VALID_CHARACTERS.toCharArray();
        int validCharCount = getValidCharCount(textContent, validChars);
        boolean forbiddenName = false;
        for (String invalidNodeName : INVALID_NODE_NAMES) {
            forbiddenName = forbiddenName || invalidNodeName.equals(textContent);
        }
        return validCharCount == textContent.length() && !forbiddenName;
    }

    /**
     * This method returns the count of characters in the string textContent
     * that are contained in the valid chars array
     */
    private int getValidCharCount(String textContent, char[] validChars) {
        int validCharCount = 0;
        for (char c : textContent.toCharArray()) {
            for (char validChar : validChars) {
                if (c == validChar) {
                    validCharCount++;
                }
            }
        }
        return validCharCount;
    }

    /**
     * This method checks the given element for validiy (i.e. it meets the minimal requirements to get parsed)
     *
     * @param nodeElement the element to check
     * @return true if the element can be parsed false otherwise
     */
    private boolean isValidElement(org.w3c.dom.Node nodeElement) {
        int paramCount = 0;
        for (int i = 0; i < nodeElement.getChildNodes().getLength(); i++) {
            org.w3c.dom.Node child = nodeElement.getChildNodes().item(i);
            switch (child.getNodeName()) {
                case NAME_ELEMENT_NAME:
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

    /**
     * Adds a hostedOn child to this node.
     * The added node gets hosted on the current node
     *
     * @param child the child node to add (Usualy this is a ServiceNode)
     */
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
