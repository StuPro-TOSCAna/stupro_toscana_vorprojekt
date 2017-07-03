package de.toscana.transformator;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertTrue;

/**
 * Created by nick on 28.06.17.
 */
@RunWith(JUnit4.class)
public class MainTest {
    private final String testResFolder ="src/test/resources/";
    @Rule
    public final ExpectedException exception = ExpectedException.none();


    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }
    @Test
    public void noArgument(){
        Main.main(new String[]{});
        String result = errContent.toString();
        boolean contains = result.contains("File-argument missing.");
        assertTrue(contains);
    }


  /*  @Test
    public void zipWithInvalidModel(){
        Main.main(new String[]{testResFolder+"zipwithinvalidmodel.zip"});
        String output = outContent.toString();
        boolean contains = output.contains("Parser could not initialize properly. The model might be invalid.");
        assertTrue(contains);
    }

    @Test
    public void zipWithOutModel(){
        Main.main(new String[]{testResFolder+"zipwithoutmodel.zip"});
        String output = outContent.toString();
        boolean contains = output.contains("Parsing the archive went wrong. Check if a model.xml is existent.");
        assertTrue(contains);
    }*/

    @After
    public void cleanUpStreams() {
        System.setOut(null);
        System.setErr(null);
    }

}
