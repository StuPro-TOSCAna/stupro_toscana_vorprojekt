package de.toscana.transformator.engine;

import de.toscana.transformator.model.MachineNode;
import de.toscana.transformator.model.Node;
import de.toscana.transformator.model.TOSCAliteModel;

import java.util.*;

/**
 * The creator class contains methods to get the correct order for creation, start and stop of services.
 *
 * @author Jens Mueller
 *         <p>
 *         TODO: Logging, Exceptions
 */
public class Creator {

    private Map<String, Node> allNodes;

    //each machine has a own queue with their nodes
    private ArrayList<Queue> allQueues = new ArrayList<>();


    /**
     * Constructor of the creator class
     *
     * @param topology The TOSCAlite model containing the complete application topology
     */
    public Creator(TOSCAliteModel topology) {
        allNodes = topology.getNodes();
    }


    /**
     * Gets the queues with the nodes in ascending order ready for creation.
     * For each machine node there is an own queue with all nodes which belong to the machine.
     * The first element has to be installed at first.
     *
     * @return a queue with nodes
     */
    protected ArrayList<Queue> getAllQueues() {
        findMachines();
        addChildren();
        return allQueues;
    }

    /**
     * Finds machine nodes and creates an own queue for each node.
     */
    private void findMachines() {
        for (Map.Entry<String, Node> entry : allNodes.entrySet()) {
            if (entry.getValue() instanceof MachineNode) {
                allQueues.add(new LinkedList<>(Arrays.asList(entry.getValue())));
            }
        }
    }


    /**
     * Adds the nodes to the queue by ascending order.
     */
    private void addChildren() {
        for (Queue<Node> nodeQueue : allQueues) {
            getAllChildren(nodeQueue.peek(), nodeQueue);

        }
    }

    /**
     * Walks recursively to the last child of a node and adds each node to the queue.
     *
     * @param node      The current node
     * @param nodeQueue The current queue
     */
    private void getAllChildren(Node node, Queue<Node> nodeQueue) {
        if (!nodeQueue.contains(node)) {
            nodeQueue.add(node);
        }
        for (Node child : node.getChildren()) {
            getAllChildren(child, nodeQueue);
        }

    }


}
