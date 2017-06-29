package de.toscana.transformator.engine;

import de.toscana.transformator.model.Node;
import de.toscana.transformator.model.TOSCAliteModel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * @author Jens Mueller
 *         <p>
 *         Class to get the correct order for a creation
 *         <p>
 *         TODO: test and check all new methods, create dummy classes for node/topology, Logging, Exceptions
 */
public class Creator {


    private Map<String,Node> allNodes;
    private Queue<Node> sortedNodes = new LinkedList<Node>();

    /**
     * constructor of class Creator
     *
     * @param topology
     */
    public Creator(TOSCAliteModel topology) {
        allNodes = topology.getNodes();
    }

    /**
     * for each node the recursive method sortNodes will be done
     */
    private void sortNodes() {
        for (Map.Entry<String, Node> entry : allNodes.entrySet()) {
            if (!sortedNodes.contains(entry.getValue())) {
                sortNodes(entry.getValue());
            }
        }
    }

    /**
     * recursive method to add the nodes to the queue by ascending order
     *
     * @param children
     */
    private void sortNodes(Node children) {
        while (!children.getChildren().isEmpty()) {
            for (Node n : children.getChildren()) {
                sortNodes(n);
            }
        }
        if (!sortedNodes.contains(children)) {
            sortedNodes.add(children);
        }
    }


    /**
     * get the nodes in ascending order ready for creation
     * the first element has to be installed at first
     *
     * @return a queue with nodes
     */
    protected Queue<Node> getSortedNodes() {
        sortNodes();
        return sortedNodes;
    }

}
