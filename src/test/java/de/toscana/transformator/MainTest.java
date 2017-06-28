package de.toscana.transformator;

import de.toscana.transformator.util.ConsoleColors;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

/**
 * Created by nick on 28.06.17.
 */
public class MainTest {
    String testResFolder ="src/test/resources/";
    @Test
    public void noArgument(){
        PrintStream stdout = System.out;
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        Main.main(new String[]{});
        String result = outputStream.toString();
        String expected = ConsoleColors.ANSI_RED+"File-argument missing."+ ConsoleColors.ANSI_RESET+"\n";
        System.setOut(stdout);
        assertEquals(expected,result);
    }

//    @Test
//    public void argument(){
//        PrintStream stdout = System.out;
//        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outputStream));
//        Main.main(new String[]{testResFolder+"zipwithmodel.zip"});
//        String result = outputStream.toString();
//        String expected = ConsoleColors.ANSI_RED+"File-argument missing."+ ConsoleColors.ANSI_RESET+"\n";
//        System.setOut(stdout);
//        assertEquals(expected,result);
//    }

}
