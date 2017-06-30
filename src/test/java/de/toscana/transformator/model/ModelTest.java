package de.toscana.transformator.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import static org.junit.Assert.assertTrue;

/**
 * This class is used to test the functionality of the Model-classes while implementing
 */
@RunWith(JUnit4.class)
public class ModelTest {

    public static String readResource(String path) throws IOException {
        InputStream in = ModelTest.class.getClassLoader().getResourceAsStream(path);
        Scanner scn = new Scanner(in);
        String contents = "";
        while (scn.hasNextLine()) {
            contents += scn.nextLine().trim();
        }
        in.close();
        scn.close();
        return contents;
    }

    @Test
    public void testParseWordpressModel() throws Exception {
        TOSCAliteModel model = new TOSCAliteModel(readResource("valid_model_wordpress.xml"));
        assertTrue(model.getMachines().size() == 2);
    }

    @Test
    public void testParseSimpleTaskAppModel() throws Exception {
        TOSCAliteModel model = new TOSCAliteModel(readResource("valid_model_simple_task_app.xml"));
        assertTrue(model.getMachines().size() == 2);
    }

    @Test(expected = ParsingException.class)
    public void testParsingFailureInvalidRoot() throws Exception {
        new TOSCAliteModel("<Test></Test>");
    }

    @Test(expected = ParsingException.class)
    public void testParsingFailureMissingMachineNodeProperties() throws Exception {
        new TOSCAliteModel(readResource("invalid_machine_node.xml"));
    }

    @Test(expected = ParsingException.class)
    public void testParsingFailureInvalidNodeName() throws Exception {
        new TOSCAliteModel(readResource("invalid_node_name.xml"));
    }

    @Test(expected = ParsingException.class)
    public void testParsingFailureInvalidNodeType() throws Exception {
        new TOSCAliteModel(readResource("invalid_node_type.xml"));
    }
}
