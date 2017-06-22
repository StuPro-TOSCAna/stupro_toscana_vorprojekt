package de.toscana.transformator.model;

import org.w3c.dom.Element;

import static de.toscana.transformator.util.CheckUtils.checkNull;

/**
 * This type of node represents a instance of Ubuntu. It is the Lowest part in a topology stack.
 */
public class MachineNode extends Node {

    /**
     * Key constant to store the key for the IP address
     */
    private static final String IP_ADRESS_KEY = "IP";
    /**
     * Key constant to store the key for the SSH username
     */
    private static final String USERNAME_KEY = "Username";
    /**
     * Key constant to store the key for the SSH password
     */
    private static final String PASSWORD_KEY = "Password";

    /**
     * Attribute for storing the IP/hostname
     */
    private String ipAdress;
    /**
     * Attribute for storing the username
     */
    private String username;
    /**
     * Attribute for storing the password
     */
    private String password;

    public MachineNode(org.w3c.dom.Node nodeElement) throws ParsingException {
        super(nodeElement);
    }

    @Override
    protected void parseSpecificData(org.w3c.dom.Node element) throws ParsingException {
        ipAdress = properties.get(IP_ADRESS_KEY);
        username = properties.get(USERNAME_KEY);
        password = properties.get(PASSWORD_KEY);
        if (!checkNull(ipAdress, username, password)) {
            throw new ParsingException("Invalid document." +
                    " Cannot find all required properties in Machine node " + name);
        }
    }

    @Override
    protected boolean isParsable(org.w3c.dom.Node element) {
        return true;
    }

    public String getIpAdress() {
        return ipAdress;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
