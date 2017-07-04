package de.toscana.transformator.engine;
import de.toscana.transformator.model.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import sun.security.pkcs.ParsingException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * This class is used to test the functionality of the engine and creator classes.
 *
 * @author Marvin Munoz Baron
 */
@RunWith(JUnit4.class)
public class EngineTest {

    private TOSCAliteModel topology;
    private Engine engine;

    @Before
    public void init() throws de.toscana.transformator.model.ParsingException {
        File emptyZIP = null;
        //create an example topology
        try {
            topology = new TOSCAliteModel(ModelTest.readResource("valid_model_simple_task_app.xml"));
        } catch (ParsingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        engine = new Engine(topology, emptyZIP);
    }

    @Test
    public void testEngineInit(){
        assertTrue(engine!=null);
    }

    @Test
    public void testCreatorBranches(){
        Creator creator = new Creator(topology);
        ArrayList<ArrayList<Node>> allBranches = creator.getAllBranches();
        ArrayList<Node> firstBranch= allBranches.get(0);
        ArrayList<Node> secondBranch = allBranches.get(1);


        assertEquals(2,allBranches.size());
        assertTrue(firstBranch.get(0) instanceof MachineNode);
        assertEquals("webmachine" , firstBranch.get(0).getName());
        assertEquals("apache" , firstBranch.get(1).getName());
        assertEquals("php" , firstBranch.get(2).getName());
        assertEquals("phpapp" , firstBranch.get(3).getName());

        assertTrue(secondBranch.get(0) instanceof MachineNode);
        assertEquals("dbmachine" , secondBranch.get(0).getName());
        assertEquals("mysql" , secondBranch.get(1).getName());
        assertEquals("database" , secondBranch.get(2).getName());
    }

    @Test
    public void testMachineNodeProperties(){
        Creator creator = new Creator(topology);
        ArrayList<ArrayList<Node>> allBranches = creator.getAllBranches();
        ArrayList<Node> firstBranch= allBranches.get(0);
        ArrayList<Node> secondBranch = allBranches.get(1);
        MachineNode webMachine = (MachineNode) firstBranch.get(0);
        MachineNode dbMachine = (MachineNode) secondBranch.get(0);

        assertEquals("192.168.178.1" ,webMachine.getIpAdress());
        assertEquals("root" ,webMachine.getUsername());
        assertEquals("password1" ,webMachine.getPassword());

        assertEquals("192.168.178.2" ,dbMachine.getIpAdress());
        assertEquals("root2" ,dbMachine.getUsername());
        assertEquals("passsword2" ,dbMachine.getPassword());
    }

}
