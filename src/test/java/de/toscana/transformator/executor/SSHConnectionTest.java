package de.toscana.transformator.executor;

import com.jcraft.jsch.JSchException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Created by Manuel on 15.06.2017.
 * <p>
 * Test for class SSHConnection
 */
@RunWith(JUnit4.class)
public  class SSHConnectionTest {

    private SSHConnection instance = null;

    /**
     * instantiates a SSHConnection with given credentials of a raspberry
     */
    @Before
    public void getSSHConnectionInstance() {
        //using data of my raspberry
        String username = "ubuntu";
        String password = "pw";
        String connectionIp = "54.186.36.46";
        this.instance = new SSHConnection(username, password, connectionIp);
    }

    /**
     * starts the connection and executes the command ls and should find at least "test.txt" in the directory
     */
    @Test
    public void testSendCommand() throws JSchException {
        String command = "ls";
        instance.connect();
        assertTrue(instance.sendCommand(command).contains("text.txt"));
        instance.close();
    }
    /**
     * starts the connection and executes the command ls and should find at least "test.txt" in the directory
     */
    @Test
    public void testExecuteScript() throws JSchException {
        String script = "/apache/create";
        String script2 = "/apache/start";
        HashMap<String, String> map = new HashMap<>();
        map.put("passwd", "1234");
        instance.connect();
        instance.executeScript(script);
        instance.executeScript(script2);
        assertTrue(instance.sendCommand("systemctl status apache2").contains("running"));
        instance.close();
    }

    /**
     * starts the connections and uploads a file via sftp
     */
    @Test
    public void testUploadFile() throws JSchException {
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
    public void testUnzip() throws JSchException {
        instance.connect();
        String directory = "src/test/resources/";
        String filename = "empty.zip";
        instance.connect();
        String result = instance.uploadAndUnzipZip(new File(directory + filename));
        assertTrue(result.contains("empty.txt"));
        instance.close();
    }
}
