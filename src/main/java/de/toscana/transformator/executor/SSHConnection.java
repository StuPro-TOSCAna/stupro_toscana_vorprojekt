package de.toscana.transformator.executor;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;

/**
 * A connection to a machine via ssh. Provides several functions to execute on the remote machine.
 * @author mklopp
 */
public class SSHConnection implements Executor {
    private static final Logger LOG = LoggerFactory.getLogger(SSHConnection.class);

    private final int port = 22;
    private final int timeout = 7000;
    private final String username;
    private final String connectionIP;
    private final String password;
    private final Map<String, String> environment;
    private String environmentChain;

    private final JSch jschSSHChannel = new JSch();
    private Session sesConnection;

    /**
     * Standard constructor that takes the credentials and the hostname/ip. Also instantiates a SSH Channel
     *
     * @param username     username of the remote user
     * @param password     password of the remote user
     * @param connectionIP ip of the remote host
     * @param environment  environment variables for all nodes on all machines
     */
    public SSHConnection(String username, String password, String connectionIP, Map<String, String> environment) throws JSchException {
        // maybe insert known hosts via jschSSHChannel.setKnownHosts(knownHosts);
        // on unix should be in ~/.ssh/known_hosts
        // on windows might take it from putty? or create an own known_hosts file
        // for testing purpose we will make the connection not check for host key
        this.username = username;
        this.password = password;
        this.connectionIP = connectionIP;
        this.environment = environment;
        this.environmentChain = "";
        setupSafeShutdown();
    }

    /**
     * Executes close() if the jvm is shutdown for safety reasons
     */
    private void setupSafeShutdown() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            close();
        }));
    }

    /**
     * creates a session and connects to the remote host
     * also updates and upgrades the host
     * and sets up the EnvironmentChain used by the scripts
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
            String updateCommand = getRootEscalation() + "apt-get update && sudo -S apt-get upgrade -y";
            sendAndPrintCommand(updateCommand);
            LOG.info("host system upgrade completed", username, connectionIP);
            setUpEnvChain();
            return true;
        } catch (JSchException ex) {
            LOG.error("Failed to connect to target '{}@{}' with password '{}'. Is host reachable?", username, connectionIP, password, ex);
            return false;
        }
    }

    /**
     * Edits the global string with all environment variables and their values
     *
     * @throws JSchException
     */
    private void setUpEnvChain() throws JSchException {
        StringBuilder environmentChainBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : environment.entrySet()) {
            String environmentEntry = entry.getKey().toUpperCase() + "=" + entry.getValue() + " ";
            environmentChainBuilder.append(environmentEntry);
        }
        environmentChain = environmentChainBuilder.toString();
    }

    /**
     * Executes a command on the remote host and reads the output and error of that command
     *
     * @param command command to send to the remote host
     * @return the generated output after executing the command
     */
    protected String sendCommand(String command) throws JSchException {
        StringBuilder out = new StringBuilder();
        try {
            PipedInputStream inStream = new PipedInputStream();
            PipedOutputStream outStream = new PipedOutputStream(inStream);
            Channel channel = sesConnection.openChannel("exec");
            ChannelExec execChannel = (ChannelExec) channel;
            execChannel.setCommand(command);
            channel.setOutputStream(outStream);
            execChannel.setErrStream(outStream);
            channel.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
            String line;
            while ((line = reader.readLine()) != null) {
                if (out.length() != 0) {
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
     *
     * @param command command to send to the remote host
     * @return the string of standard and error output
     * @throws JSchException
     */
    private String sendAndPrintCommand(String command) throws JSchException {
        printCommand(command);
        String output = sendCommand(command);
        System.out.println(output);
        return output;
    }

    /**
     * Sets working directory onto nodename and executes the script on the remote host
     *
     * @param script script to execute
     * @return the output of the command
     */
    @Override
    public String executeScript(String script) throws JSchException {
        script = script.substring(1);
        String[] scriptSplit = script.split("/");
        String nodeName = scriptSplit[0];
        String scriptName = scriptSplit[1];
        LOG.info("executing operation {}:{}", nodeName, scriptName);
        return sendAndPrintCommand("cd " + nodeName + " && " + getRootEscalation() + environmentChain + " ./" + scriptName);
    }

    /**
     * return string with root password and sudo prefix
     * @return string with root
     */
    private String getRootEscalation() {
        return " echo \"" + password + "\" | sudo -S ";
    }

    /**
     * Uploading a Zip File to the machine using sftp
     * OVERWRITES already existing files with the same name
     *
     * @param file file to upload, should be .zip
     */
    private boolean uploadFile(File file, String targetPath) throws JSchException {
        try {
            Channel channel = sesConnection.openChannel("sftp");
            channel.connect();
            ChannelSftp channelSftp = (ChannelSftp) channel;
            if (!"".equals(targetPath)) {
                channelSftp.cd(targetPath);
            }
            channelSftp.put(new FileInputStream(file), file.getName());
            channelSftp.disconnect();
            return true;
        } catch (FileNotFoundException fnfExp) {
            LOG.error("File not found", fnfExp);
            return false;
        } catch (SftpException sftpExp) {
            LOG.error("File upload failed", sftpExp);
            return false;
        }
    }

    /**
     * closes the connected session
     */
    public void close() {
        if (sesConnection != null && sesConnection.isConnected()) {
            sesConnection.disconnect();
            LOG.info("closed connection to {}@{}", username, connectionIP);
        }
    }

    /**
     * Unzip is installed and the zipFile gets unziped
     * Overwrites all existing files
     * @param zipFile
     */
    private String unzipFile(File zipFile) throws JSchException {
        sendCommand(getRootEscalation() + "apt install -y unzip");
        String zip = sendCommand("unzip -o " + zipFile.getName());
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
        LOG.info("uploaded TOSCALite-archive to target machine", username, connectionIP);
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
