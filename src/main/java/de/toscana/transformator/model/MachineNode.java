package de.toscana.transformator.model;

import org.w3c.dom.Element;

/**
 * This type of node represents a instance of Ubuntu. It is the Lowest part in a topology stack.
 */
public class MachineNode extends Node{

    private static final String IP_ADRESS_KEY = "IP";
    private static final String USERNAME_KEY = "Username";
    private static final String PASSWORD_KEY = "Password";

    private String ipAdress;
    private String username;
    private String password;

    public MachineNode(Element nodeElement) throws ParsingException {
        super(nodeElement);
    }

    @Override
    protected void parseSpecificData(Element element) throws ParsingException {

    }

    @Override
    protected boolean isParsable(Element element) {
        return false;
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
