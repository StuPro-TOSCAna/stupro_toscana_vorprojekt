package de.toscana.transformator;

import de.toscana.transformator.util.ConsoleColors;
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

    private PrintStream stdout;
    private PrintStream stderr;
    private ByteArrayOutputStream outputStream;

    @Test
    public void noArgument(){
        setUpOutPutStream();
        Main.main(new String[]{});
        String result = getOutPutString();
        boolean contains = result.contains(ConsoleColors.ANSI_RED+"File-argument missing."+ ConsoleColors.ANSI_RESET+"\n");
        assertTrue(contains);
    }

    private String getOutPutString() {
        String result = outputStream.toString();
        System.setOut(stdout);
        return result;
    }
    private String getErrOutPutString() {
        String result = outputStream.toString();
        System.setErr(stderr);
        return result;
    }


    @Test
    public void zipWithInvalidModel(){
        setUpErrOutPutStream();
        Main.main(new String[]{testResFolder+"zipwithinvalidmodel.zip"});
        String output = getErrOutPutString();
        boolean contains = output.contains("Parser could not initialize properly. The model might be invalid.");
        assertTrue(contains);
    }
    @Test
    public void zipWithOutModel(){
        setUpErrOutPutStream();
        Main.main(new String[]{testResFolder+"zipwithoutmodel.zip"});
        String output = getErrOutPutString();
        boolean contains = output.contains("Parsing the archive went wrong. Check if a model.xml is existent.");
        assertTrue(contains);
    }

    private void setUpOutPutStream(){
        stdout = System.out;
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }
    private void setUpErrOutPutStream(){
        stderr = System.err;
        outputStream = new ByteArrayOutputStream();
        System.setErr(new PrintStream(outputStream));
    }

}
