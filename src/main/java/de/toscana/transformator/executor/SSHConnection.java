package de.toscana.transformator.executor;

/**
 * Created by Manuel on 15.06.2017.
 */

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;

public class SSHConnection implements Executor {
    private static final Logger LOG = LoggerFactory.getLogger(SSHConnection.class);

    private final int port = 22;
    private final int timeout = 7000;
    private final String username;
    private final String connectionIP;
    private final String password;

    private final JSch jschSSHChannel = new JSch();
    private Session sesConnection;

    /**
     * Standard constructor that takes the credentials and the hostname/ip. Also instantiates a SSH Channel
     *
     * @param username
     * @param password
     * @param connectionIP
     */
    public SSHConnection(String username, String password, String connectionIP) {
        // maybe insert known hosts via jschSSHChannel.setKnownHosts(knownHosts);
        // on unix should be in ~/.ssh/known_hosts
        // on windows might take it from putty? or create an own known_hosts file
        // for testing purpose we will make the connection not check for host key
        this.username = username;
        this.password = password;
        this.connectionIP = connectionIP;
    }

    /**
     * creates a session and connects to the remote host
     */
    public boolean connect() {
        try {
            // use this if you want to connect via a privatekey and comment setPassword function
            //jschSSHChannel.addIdentity("/path/to/key");
            sesConnection = jschSSHChannel.getSession(username, connectionIP, port);
            sesConnection.setPassword(password);

            // only for testing
            sesConnection.setConfig("StrictHostKeyChecking", "No");

            LOG.info("connecting to target '{}@{}' with password '{}' ...", username, connectionIP, password);
            sesConnection.connect(timeout);
            LOG.info("connection established");
            LOG.info("updating target");
            String updateCommand = "echo " + password + "| sudo -S apt-get update && sudo -S apt-get upgrade -y";
            sendAndPrintCommand(updateCommand);
            LOG.info("{}@{} > host system upgrade completed", username, connectionIP);
            //check if util files are there, if not upload them
            //upload util scripts to /usr/local/bin
            String targetPath = "/usr/local/bin/";
            String targetFiles = sendCommand("ls " + targetPath);
            File sourceFolder = new File("src/main/resources/util"); //probably gonna change
            File[] sourceFiles = sourceFolder.listFiles();
            for (File file : sourceFiles) {
                if (!(file.isFile() && targetFiles.contains(file.getName()))) {
                    LOG.info("{}@{} > uploading {}", username, connectionIP, file.getName());
                    //have to upload to home directory and then move with sudo because upload has no root privileges
                    uploadFile(file, "");
                    sendAndPrintCommand("echo " + password + "| sudo -S mv " + file.getName() + " " + targetPath);
                    //at least if i copy the file from windows i have mark them executable
                    String resultChmod = sendAndPrintCommand("echo " + password + "| sudo -S chmod 775 " + targetPath + file.getName());
                }
            }
            return true;
        } catch (JSchException ex) {
            LOG.error("Failed to connect to target '{}@{}' with password '{}'. Is host reachable?", username, connectionIP, password, ex);
            return false;
        }
    }

    /**
     * Executes a command on the remote host
     *
     * @param command
     * @return the generated output after executing the command
     */
     String sendCommand(String command) throws JSchException {
        StringBuilder out = new StringBuilder();
        try {
            PipedInputStream inStream = new PipedInputStream();
            PipedOutputStream outStream = new PipedOutputStream(inStream);
            Channel channel = sesConnection.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            channel.setOutputStream(outStream);
            ((ChannelExec) channel).setErrStream(outStream);
            channel.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
            String line;
            while ((line = reader.readLine()) != null) {
                if(out.length() != 0){
                    out.append("\n");
                }
                out.append(line);
            }
            reader.close();
            channel.disconnect();
        } catch (
                IOException ioExp) {
            ioExp.printStackTrace();
        }
        return out.toString();
    }

    /**
     * Like sendCommand(String command), but also prints the executed command and the resulting standard and error output to std out
     * @param command
     * @return
     * @throws JSchException
     */
    String sendAndPrintCommand(String command) throws JSchException {
        printCommand(command);
        String output = sendCommand(command);
        System.out.println(output);
        return output;

    }

    /**
     * Sets working directory onto nodename and executes a command on the remote host
     *
     * @param script
     * @return the output of the command
     */
    @Override
    public String executeScript(String script, Map<String, String> environment) throws JSchException {
        script = script.substring(1);
        String[] scriptSplit = script.split("/");
        String nodeName = scriptSplit[0];
        String scriptName = scriptSplit[1];
        String environmentChain = "";
        for (Map.Entry<String,String> entry: environment.entrySet()){
            String environmentEntry = nodeName.toUpperCase() + "_" + entry.getValue().toUpperCase() + "=" + entry.getValue() + " ";
            environmentChain += environmentEntry;
        }
        String output = sendAndPrintCommand("cd " + nodeName + " && " + "echo " + password + "| sudo -S "+ environmentChain +"./" + scriptName);
        LOG.info("{}@{} > executed operation {}:{}", username, connectionIP, nodeName, scriptName);
        System.out.println(output);
        return output;
    }


    /**
     * Uploading a Zip File to the machine using sftp
     * OVERWRITES already existing files with the same name
     *
     * @param file
     */
    boolean uploadFile(File file, String targetPath) throws JSchException {
        try {
            Channel channel = sesConnection.openChannel("sftp");
            channel.connect();
            ChannelSftp channelSftp = (ChannelSftp) channel;
            if (!"".equals(targetPath)) {
                channelSftp.cd(targetPath);
            }
            channelSftp.put(new FileInputStream(file), file.getName());
            channelSftp.disconnect();
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
        LOG.info("closed connection to {}@{}", username, connectionIP);
    }

    /**
     * Tests if Unzip is installed, if it is installed unzip the File on the Machine
     * otherwise install Unzip and then unzip the File
     * Overwrites all existing files
     */
    private String unzipFile(File zipFile) throws JSchException {
        String zip;
        //depends on the language the server is using
        if (sendCommand("apt -qq list unzip").contains("installed")) {
            zip = sendCommand("unzip -o " + zipFile.getName());
        } else {
            sendCommand("echo " + password + "| sudo -S apt-get install -y unzip");
            zip = sendCommand("unzip -o " + zipFile.getName());
        }
        return zip;
    }

    /**
     * Uploads a zip file and unzips it. Overwrites already existing files
     */
    @Override
    public String uploadAndUnzipZip(File zipFile) throws JSchException {
        //can be changed maybe?
        String targetDirectory = "";
        uploadFile(zipFile, targetDirectory);
        String output = unzipFile(zipFile);
        LOG.info("{}@{} > uploaded TOSCALite-archive to target machine", username, connectionIP);
        System.out.println(output);
        return output;
    }

    private void printCommand(String command) throws JSchException {
        System.out.println(getPrompt() + command);

    }

    private String getPrompt() throws JSchException {
       String workingDirectory = getWorkingDirectory();
       return username + "@" + connectionIP + ":" + workingDirectory + "# ";
    }

    private String getWorkingDirectory() throws JSchException {
        String workingDirectory = sendCommand("pwd");
        if (workingDirectory.equals("/home/" + username)) {
           workingDirectory = "~";
        }
        return workingDirectory;
    }
}
