package de.toscana.transformator.engine;

import de.toscana.transformator.executor.SSHConnection;
import de.toscana.transformator.model.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;

/**
 * The Engine contains methods to deploy the application and manage it at runtime.
 *
 * @author Marvin Munoz Baron
 * @author Jens Mueller
 *
 */
public class Engine {

    private final Creator creator;
    private final ArrayList<Queue> lstMachineQueues;
    private SSHConnection ssh;

    /**
     * constructor of Engine Class
     * for each command you need a new instance of Constructor
     * @param topology
     */
    public Engine(TOSCAliteModel topology) {
        creator=new Creator(topology);
        lstMachineQueues = creator.getAllQueues();
        ssh = null;
    }

    /**
     * create the whole application with all dependencies
     * <p>
     * TODO: send files and commands to VM
     */
    public boolean create() {
        helpCreateStart(ArtifactType.CREATE);
        return true;
    }

    /**
     * start all services in the application topology
     */
    public void start() {
        helpCreateStart(ArtifactType.START);
    }

    /**
     * stop all services in the application topology
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
                //TODO: possibly change path String? Does "path" work as a command?
                ssh.sendCommand(path);
            }
            //close ssh-connection
            ssh.close();
        }
    }

    /**
     * depends on the type the method will create or start
     * @param type artifactType defines what should done
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
                    //TODO get name and location of ZIP
                    ssh.uploadAndUnzipZip(zipName, zipLocation);
                }
            }

            while (!nodesForCreation.isEmpty()) {
                Node nodeToInstall = nodesForCreation.poll();
                String path="";
                //instance of ssh Connection
                if (nodeToInstall instanceof ServiceNode){
                    path=((ServiceNode) nodeToInstall).getImplementationArtifact(type).getAbsolutePath();
                    //TODO: possibly change path to proper command? Does "path" work as a command?
                    ssh.sendCommand(path);
                }

            }
            //close ssh-connection
            ssh.close();
        }

    }
}
