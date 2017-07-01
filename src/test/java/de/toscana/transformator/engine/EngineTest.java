package de.toscana.transformator.engine;
import de.toscana.transformator.model.*;
import org.testng.annotations.Test;
import sun.security.pkcs.ParsingException;


import java.io.IOException;


/**
 * This class is used to test the functionality of the Engine and Creator classes.
 *
 * @author Marvin Munoz Baron
 */
public class EngineTest {

    @Test
    public void testEngineInit() {
        //create an example topology
        TOSCAliteModel topology=null;
        try {
            topology = new TOSCAliteModel(ModelTest.readResource("valid_model_simple_task_app.xml"));
        } catch (ParsingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (de.toscana.transformator.model.ParsingException e) {
            e.printStackTrace();
        }
        Engine engine = new Engine(topology);
        assertTrue()
    }
}
