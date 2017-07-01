package de.toscana.transformator.engine;

import de.toscana.transformator.executor.SSHConnection;
import de.toscana.transformator.model.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;

/**
 * The engine class contains methods to deploy the application and manage it at runtime.
 *
 * @author Marvin Munoz Baron
 * @author Jens Mueller
 *
 */
public class Engine {

    private final Creator creator;
    private final ArrayList<Queue> lstMachineQueues;
    private SSHConnection ssh;
    private final File zip;

    /**
     * Constructor of the engine class
     * for each command you need a new instance of Constructor TODO: What does this mean?
     * @param topology The TOSCAlite model containing the complete application topology
     * @param inputZip The zip file containing all artifacts and models
     */
    public Engine(TOSCAliteModel topology, File inputZip) {
        creator=new Creator(topology);
        lstMachineQueues = creator.getAllQueues();
        ssh = null;
        zip = inputZip;
    }

    /**
     * Creates the whole application with all dependencies.
     */
    public boolean create() {
        helpCreateStart(ArtifactType.CREATE);
        return true;
    }

    /**
     * Starts all services in the application topology.
     */
    public void start() {
        helpCreateStart(ArtifactType.START);
    }

    /**
     * Stops all services in the application topology.
     */
    public void stop() {
        for(Queue<Node> nodesForCreation : lstMachineQueues){
            MachineNode mNode = (MachineNode) nodesForCreation.peek();
            String ip = mNode.getIpAdress();
            String user = mNode.getUsername();
            String pw = mNode.getPassword();
            // make ssh connection to Machine with these data
            ssh = new SSHConnection(user, pw, ip);
            ssh.connect();

            //get stop-artifact of nodes in descending order
            while (!nodesForCreation.isEmpty()) {
                Iterator<Node> iterator= nodesForCreation.iterator();
                ServiceNode currentNode=null;
                while(iterator.hasNext()){
                    currentNode = (ServiceNode) iterator.next();
                }
                nodesForCreation.remove(currentNode);
                String path=currentNode.getImplementationArtifact(ArtifactType.STOP).getAbsolutePath();
                //TODO: possibly change path to proper command? Does "path" work as a command?
                ssh.sendCommand(currentNode.getName(), path);
            }
            //close ssh-connection
            ssh.close();
        }
    }

    /**
     * Orchestrates ZIP upload and sends commands through SSH.
     * Depending on the artifact type the method will use create or start scripts.
     * @param type The artifact type which determines the nature of the method
     */
    private void helpCreateStart(ArtifactType type){
        for(Queue<Node> nodesForCreation : lstMachineQueues){
            Node mNode = nodesForCreation.poll();
            if (mNode instanceof MachineNode){
                String ip = ((MachineNode) mNode).getIpAdress();
                String user = ((MachineNode) mNode).getUsername();
                String pw = ((MachineNode) mNode).getPassword();
                // make ssh connection to Machine with these data
                ssh = new SSHConnection(user, pw, ip);
                ssh.connect();
                if (type == ArtifactType.CREATE){
                    ssh.uploadAndUnzipZip(zip);
                }
            }

            while (!nodesForCreation.isEmpty()) {
                Node nodeToInstall = nodesForCreation.poll();
                String path="";
                //instance of ssh Connection
                if (nodeToInstall instanceof ServiceNode){
                    path=((ServiceNode) nodeToInstall).getImplementationArtifact(type).getAbsolutePath();
                    //TODO: possibly change path to proper command? Does "path" work as a command?
                    ssh.sendCommand(nodeToInstall.getName(),path);
                }

            }
            //close ssh-connection
            ssh.close();
        }

    }
}
