package de.toscana.transformator.engine;

import de.toscana.transformator.model.ArtifactType;
import de.toscana.transformator.model.Node;
import de.toscana.transformator.model.TOSCAliteModel;

import java.util.Map;
import java.util.Queue;

/**
 * TODO: Description of Engine class
 *
 * @author Marvin Munoz Baron, Jens Mueller
 *
 */
public class Engine {
    //private final ApplicationState applicationState;
    private final TOSCAliteModel topology;

    public Engine(TOSCAliteModel topology) {
        this.topology = topology;
        //this.applicationState = applicationState;
    }

    /**
     * create the whole application with all dependencies
     * <p>
     * TODO: send files and commands to VM
     */
    public boolean create() {
        Creator creator = new Creator(topology);
        Queue<Node> nodesForCreation = creator.getSortedNodes();
        Map<String, String> properties=null;

        while (!nodesForCreation.isEmpty()) {
            Node nodeToInstall = nodesForCreation.poll();
            properties = nodeToInstall.getProperties();
            String createProperty = properties.get(ArtifactType.CREATE.getElementName());
            System.out.println("created");
            //TODO: send createProperty to the VM
        }
        return true;
    }

    /**
     * TODO: Description of start() method
     */
    public void start() {
        // TODO: Implementation of start() method
        System.out.println("started");
    }

    /**
     * TODO: Description function of stop() method
     */
    public void stop() {
        // TODO: Implementation of stop() method
        System.out.println("stopped");
    }
}
