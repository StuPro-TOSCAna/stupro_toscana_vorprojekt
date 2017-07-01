package de.toscana.transformator.executor;

import com.sun.media.jfxmedia.logging.Logger;
import com.sun.xml.internal.ws.api.message.ExceptionHasMessage;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by Manuel on 15.06.2017.
 * <p>
 * Test for class SSHConnection
 */
public class SSHConnectionTest {

    private SSHConnection instance = null;

    /**
     * instantiates a SSHConnection with given credentials of a raspberry
     */
    @Before
    public void getSSHConnectionInstance() {
        //using data of my raspberry
        String username = "user";
        String password = "pw";
        String connectionIp = "ip";
        this.instance = new SSHConnection(username, password, connectionIp);
    }

    /**
     * starts the connection and executes the command ls and should find at least "test.txt" in the directory
     */
    @Test
    public void testSendCommand() {
        String command = "ls";
        instance.connect();
        assertTrue(instance.sendCommand(command).contains("text.txt"));
        instance.close();
    }

    /**
     * starts the connections and uploads a file via sftp
     */
    @Test
    public void testUploadFile() {
        String directory = "D:/";
        String filename = "test.txt";
        instance.connect();
        instance.uploadFile(new File(directory + filename),".");
        assertTrue(instance.sendCommand("ls").contains("test.txt"));
        instance.close();
    }

    /**
     * starts the connection and tests the unzip procedure
    */
    @Test
    public void testUnzip() throws Exception {
        String directory = "D:/";
        String filename = "empty.zip";
        instance.connect();
        String result = instance.uploadAndUnzipZip(new File(directory + filename));
        assertTrue(result.contains("empty.txt"));
        instance.close();
    }

}