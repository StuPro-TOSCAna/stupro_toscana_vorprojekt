package de.toscana.transformator.engine;

import de.toscana.transformator.model.*;

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
    private final TOSCAliteModel topology;

    Engine(TOSCAliteModel topology) {
        this.topology = topology;
        //this.applicationState = applicationState; TODO remove?
    }

    /**
     * create the whole application with all dependencies
     * <p>
     * TODO: send files and commands to VM
     */
    public boolean create() {
        Creator creator = new Creator(topology);
        Queue<Node> nodesForCreation = creator.getSortedNodes();

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
                path=((ServiceNode) nodeToInstall).getImplementationArtifact(ArtifactType.CREATE);
                //TODO: take the sshConnection instance and send path to VM
            }

        }

        //close ssh connection?

        return true;
    }

    /**
     * start all services in the application topology
     */
    public void start() {
        // TODO: Implementation of start() method
    }

    /**
     * stop all services in the application topology
     */
    public void stop() {
        // TODO: Implementation of stop() method
    }
}
