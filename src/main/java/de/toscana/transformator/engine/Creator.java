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
    private Queue<Node> sortedNodes = new LinkedList<Node>();
    private ArrayList<Node> lookedNodes = new ArrayList<>();

    //this array list should contain all queues
    //each machine has a own queue with their nodes
    private ArrayList<Queue> allQueues = new ArrayList<>();


    private Queue<Node> tmpNodes= new LinkedList<>();
    private Node tmpMachine;
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
        addSortedNodes();
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
    private void addSortedNodes(){
        for (Map.Entry<String, Node> entry : allNodes.entrySet()) {
            if (!(entry.getValue() instanceof MachineNode) && !(lookedNodes.contains(entry.getValue()))){
                tmpMachine=null;
                tmpNodes=null;
                getAllChildren(entry.getValue());

                //add the nodes which was found on the way to the last child of the current node
                //to the machine queue where it belongs to
                for(Queue queue : allQueues){
                    if(queue.contains(tmpMachine)){
                        while(!tmpNodes.isEmpty()){
                            Node currentNode = tmpNodes.peek();
                            if(!queue.contains(currentNode)){
                                queue.add(tmpNodes.poll());
                            }

                        }
                    }
                }
            }
        }
    }

    /**
     * recursive method
     * walk recursively to the last child of a node
     * save all nodes on the way and remember the machineNode their belongs to
     *
     * @param n the current node
     */
    private void getAllChildren(Node n){
        lookedNodes.add(n);
        while(!(n.getChildren().isEmpty())){
            for (Node child : n.getChildren()){
                getAllChildren(child);
            }
        }
        if(n instanceof MachineNode){
            tmpMachine=n;
        } else{
            tmpNodes.add(n);
        }

    }

}
