package de.toscana.transformator.executor;

/**
 * Created by Manuel on 15.06.2017.
 */




import com.jcraft.jsch.*;

import java.io.*;

public class SSHConnection implements Executor{
    private String username;
    private String connectionIP;
    private String password;
    private int port;
    private JSch jschSSHChannel;
    private Session sesConnection;
    private int timeout;

    /**
     * Standard constructor that takes the credentials and the hostname/ip. Also instantiates a SSH Channel
     *
     * @param username
     * @param password
     * @param connectionIP
     */
    public SSHConnection(String username, String password, String connectionIP) {

        this.jschSSHChannel = new JSch();

        // maybe insert known hosts via jschSSHChannel.setKnownHosts(knownHosts);
        // on unix should be in ~/.ssh/known_hosts
        // on windows might take it from putty? or create an own known_hosts file
        // for testing purpose we will make the connection not check for host key

        this.username = username;
        this.password = password;
        this.connectionIP = connectionIP;
        this.port = 22;
        this.timeout = 60000;
    }

    /**
     * creates a session and connects to the remote host
     */
    public void connect() {
        try {
            sesConnection = jschSSHChannel.getSession(username, connectionIP, port);
            sesConnection.setPassword(password);

            // only for testing
            sesConnection.setConfig("StrictHostKeyChecking", "No");

            sesConnection.connect(timeout);
        } catch (JSchException jschExp) {
            jschExp.printStackTrace();
        }
    }

    /**
     * Executes a command on the remote host
     *
     * @param command
     * @return
     */
    public String sendCommand(String command) {
        StringBuilder out = new StringBuilder();
        try {
            Channel channel = sesConnection.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            InputStream commandOutput = channel.getInputStream();
            channel.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(commandOutput));
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
            reader.close();
            channel.disconnect();
        } catch (
                JSchException jschExp) {
            jschExp.printStackTrace();
        } catch (
                IOException ioExp) {
            ioExp.printStackTrace();
        }
        return out.toString();
    }

    /**
     * Uploading a Zip File to the machine using sftp
     * OVERWRITES already existing files with the same name
     *
     * @param filename
     */
    public boolean uploadFile(String filename, String targetDirectory) {
        try {
            Channel channel = sesConnection.openChannel("sftp");
            channel.connect();
            ChannelSftp channelSftp = (ChannelSftp) channel;
            channelSftp.cd(targetDirectory);
            File f = new File(filename);
            channelSftp.put(new FileInputStream(f), f.getName());
            channelSftp.disconnect();
        } catch (JSchException jschExp) {
            jschExp.printStackTrace();
        } catch (FileNotFoundException fnfExp) {
            fnfExp.printStackTrace();
        } catch (SftpException sftpExp) {
            sftpExp.printStackTrace();
        }
        return true;
    }

    /**
     * closes the connected session
     */
    public void close() {
        sesConnection.disconnect();
    }

    /**
     * Tests if Unzip is installed, if it is installed unzip the File on the Machine
     * otherwise install Unzip and then unzip the File
     * Overwrites all existing files
     */
    private String unzipFile(String zipname) {
        String zip="";
        //depends on the language the server is using
        if(sendCommand("apt -qq list unzip").contains("installed")) {
            zip = sendCommand("unzip -o " + zipname);
        } else {
            sendCommand("echo "+password+"| sudo -S apt-get install -y unzip");
            zip = sendCommand("unzip -o " + zipname);
        }
        return zip;
    }

    /**
     * Uploads a zip file and unzips it. Overwrites already existing files
     */
    public String uploadAndUnzipZip(String zipFilename, String localDir) {
        //can be changed maybe?
        String targetDirectory = "./";
        uploadFile(localDir + zipFilename, targetDirectory);
        return unzipFile(zipFilename);
    }
}
