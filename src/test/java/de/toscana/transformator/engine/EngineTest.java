package de.toscana.transformator.engine;
import de.toscana.transformator.model.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import sun.security.pkcs.ParsingException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Queue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * This class is used to test the functionality of the Engine and Creator classes.
 *
 * @author Marvin Munoz Baron
 */
@RunWith(JUnit4.class)
public class EngineTest {

    private TOSCAliteModel topology;
    private Engine engine;

    @Before
    public void init() {
        File emptyZIP = null;
        //create an example topology
        try {
            topology = new TOSCAliteModel(ModelTest.readResource("valid_model_simple_task_app.xml"));
        } catch (ParsingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (de.toscana.transformator.model.ParsingException e) {
            e.printStackTrace();
        }
        engine = new Engine(topology, emptyZIP);
    }

    @Test
    public void testEngineInit(){
        assertTrue(engine!=null);
    }

    @Test
    public void testCreatorQueues(){
        Creator creator = new Creator(topology);
        ArrayList<Queue> allQueues = creator.getAllQueues();
        Queue<Node> firstQueue = allQueues.get(0);
        Queue<Node> secondQueue = allQueues.get(1);

        assertEquals(2,allQueues.size());
        assertTrue(firstQueue.peek() instanceof MachineNode);
        assertEquals("webmachine" , firstQueue.poll().getName());
        assertEquals("apache" , firstQueue.poll().getName());
        assertEquals("php" , firstQueue.poll().getName());
        assertEquals("php-app" , firstQueue.poll().getName());

        assertTrue(secondQueue.peek() instanceof MachineNode);
        assertEquals("dbmachine" , secondQueue.poll().getName());
        assertEquals("mysql" , secondQueue.poll().getName());
        assertEquals("database" , secondQueue.poll().getName());
    }

    @Test
    public void testMachineNodeProperties(){
        Creator creator = new Creator(topology);
        ArrayList<Queue> allQueues = creator.getAllQueues();
        Queue<Node> firstQueue = allQueues.get(0);
        Queue<Node> secondQueue = allQueues.get(1);
        MachineNode webMachine = (MachineNode) firstQueue.peek();
        MachineNode dbMachine = (MachineNode) secondQueue.peek();

        assertEquals("192.168.178.1" ,webMachine.getIpAdress());
        assertEquals("root" ,webMachine.getUsername());
        assertEquals("password1" ,webMachine.getPassword());

        assertEquals("192.168.178.2" ,dbMachine.getIpAdress());
        assertEquals("root2" ,dbMachine.getUsername());
        assertEquals("passsword2" ,dbMachine.getPassword());

    }

}
