package de.toscana.transformator;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Created by nick on 28.06.17.
 */

public  class ArchiveHandlerTest {
    private final String testResFolder ="src/test/resources/";
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testMissingFile() throws ArchiveHandler.ArchiveException {
        exception.expect(ArchiveHandler.ArchiveException.class);
        exception.expectMessage("There is no file with the name yeadfad.md.");
        new ArchiveHandler(new File("yeadfad.md"));
    }
    @Test
    public void fileIsNoZip() throws ArchiveHandler.ArchiveException {
        exception.expect(ArchiveHandler.ArchiveException.class);
        exception.expectMessage("test.md is not a valid archive file.");
        new ArchiveHandler(new File(testResFolder+"test.md"));
    }
    @Test
    public void zipWithoutModel() throws ArchiveHandler.ArchiveException {
        exception.expect(ArchiveHandler.ArchiveException.class);
        exception.expectMessage("Parsing the archive went wrong. Check if a model.xml is existent.");
        ArchiveHandler archiveHandler = new ArchiveHandler(new File(testResFolder+"zipwithoutmodel.zip"));
        archiveHandler.getModelXmlFromZip();
    }
    @Test
    public void zipWithModel() throws ArchiveHandler.ArchiveException {
        ArchiveHandler archiveHandler = new ArchiveHandler(new File(testResFolder+ "zipwithinvalidmodel.zip"));
        String result = archiveHandler.getModelXmlFromZip();
        String expected = "ThisisaverylongString.";
        assertEquals(expected,result);

    }

}
