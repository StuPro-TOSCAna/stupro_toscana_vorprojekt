package de.toscana.transformator.engine;

import de.toscana.transformator.model.*;

import java.util.ArrayList;
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
    //private final ApplicationState applicationState; TODO remove?
    private final Creator creator;
    private final ArrayList<Queue> lstMachineQueues;

    public Engine(TOSCAliteModel topology) {
        creator=new Creator(topology);
        lstMachineQueues = creator.getAllQueues();
        //this.applicationState = applicationState; TODO remove?
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
        // TODO: Implementation of start() method
        helpCreateStart(ArtifactType.START);

    }

    /**
     * stop all services in the application topology
     */
    public void stop() {
        // TODO: Implementation of stop() method
    }

    /**
     * depends on the type the method will create or start
     * @param type artifactType defines what should done
     */
    private void helpCreateStart(ArtifactType type){
        for(Queue<Node> nodesForCreation : lstMachineQueues){
            while (!nodesForCreation.isEmpty()) {
                Node nodeToInstall = nodesForCreation.poll();
                String path="";
                //instance of ssh Connection

                if (nodeToInstall instanceof MachineNode){
                    String ip = ((MachineNode) nodeToInstall).getIpAdress();
                    String user = ((MachineNode) nodeToInstall).getUsername();
                    String pw = ((MachineNode) nodeToInstall).getPassword();
                    //TODO: make ssh connection to Machine with these data
                }

                if (nodeToInstall instanceof ServiceNode){
                    path=((ServiceNode) nodeToInstall).getImplementationArtifact(type).getAbsolutePath();
                    //TODO: take the sshConnection instance and send path to VM
                }

            }
            //close ssh connection because now it follows another machine
        }




    }
}
