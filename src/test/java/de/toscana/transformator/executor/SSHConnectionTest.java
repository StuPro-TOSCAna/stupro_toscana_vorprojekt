package de.toscana.transformator.executor;

import org.junit.Before;
import org.junit.Test;

import static com.sun.xml.internal.ws.dump.LoggingDumpTube.Position.Before;
import static org.junit.Assert.*;

/**
 * Created by Manuel on 15.06.2017.
 * <p>
 * Test for class SSHConnection
 */
public class SSHConnectionTest {

    SSHConnection instance = null;

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
        assertTrue(instance.sendCommand(command).contains("empty.zip"));
        instance.close();
    }

    /**
     * starts the connections and uploads a file via sftp
     */
    @Test
    public void testUploadFile() {
        String directory = "C:/";
        String filename = "empty.zip";
        instance.connect();
        instance.uploadFile(directory + filename, ".");
        assertTrue(instance.sendCommand("ls").contains("empty.zip"));
        instance.close();
    }

    /**
     * starts the connection and waits 5 seconds before terminating the session
     * this time is used to check on the raspberry with netstat for a new connection
     */
    @Test
    public void testConnect() {
        instance.connect();
        //time to check netstat on device

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        instance.close();
    }

    /**
     * starts the connection and tests the unzip procedure
    */
    @Test
    public void testUnzip() {
        instance.connect();
        //time to check netstat on device
        assertTrue(instance.unzipFile("empty").contains("empty.txt"));
        instance.close();
    }
}