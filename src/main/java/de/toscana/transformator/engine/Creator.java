package de.toscana.transformator.engine;

import de.toscana.transformator.model.MachineNode;
import de.toscana.transformator.model.Node;
import de.toscana.transformator.model.TOSCAliteModel;

import java.util.*;

/**
 * @author Jens Mueller
 *         <p>
 *         Class to get the correct order for a creation
 *         <p>
 *         TODO: test and check all new methods, create dummy classes for node/topology, Logging, Exceptions
 */
public class Creator {


    private Map<String,Node> allNodes;

    //this array list should contain all queues
    //each machine has a own queue with their nodes
    private ArrayList<Queue> allQueues = new ArrayList<>();


    /**
     * constructor of class Creator
     *
     * @param topology
     */
    public Creator(TOSCAliteModel topology) {
        allNodes = topology.getNodes();
    }


    /**
     * get the queues with the nodes in ascending order ready for creation
     * for each machine node there is a own queue with all nodes which belongs to the machine
     * the first element has to be installed at first
     *
     * @return a queue with nodes
     */
    protected ArrayList<Queue> getAllQueues() {
        findMachines();
        addChildren();
        return allQueues;
    }

    /**
     * find machinenodes and create for each node an own queue
     */
    private void findMachines(){
        for (Map.Entry<String, Node> entry : allNodes.entrySet()) {
            if(entry.getValue() instanceof MachineNode){
                allQueues.add(new LinkedList<Node>(Arrays.asList(entry.getValue())));
            }
        }
    }


    /**
     * add the nodes to the queue by ascending order
     */
    private void addChildren(){
        for(Queue<Node> qu : allQueues){
            getAllChildren(qu.peek(), qu);
        }
    }

    /**
     * recursive method
     * walk recursively to the last child of a node and add each node to the queue
     *
     * @param n the current node
     * @param qu the current queue
     */
    private void getAllChildren(Node n, Queue<Node> qu){
        if(!qu.contains(n)){
            qu.add(n);
        }

        while(!n.getChildren().isEmpty()){
            for (Node child : n.getChildren()){
                getAllChildren(child,qu);
            }
        }
    }


}
