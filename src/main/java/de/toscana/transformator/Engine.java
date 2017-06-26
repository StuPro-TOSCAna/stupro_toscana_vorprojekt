package de.toscana.transformator;

import java.util.Queue;

/**
 * TODO: Description of Engine class
 *
 * @author Marvin Munoz Baron
 */
public class Engine {
    private final ApplicationState applicationState;
    private final Topology topology;

    Engine(Topology topology, ApplicationState applicationState) {
        this.topology = topology;
        this.applicationState = applicationState;
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
            //TODO: get the right information of the node and send it to the VM
        }

        return true;
    }

    /**
     * TODO: Description of start() method
     */
    public void start() {
        // TODO: Implementation of start() method
    }

    /**
     * TODO: Description function of stop() method
     */
    public void stop() {
        // TODO: Implementation of stop() method
    }
}
