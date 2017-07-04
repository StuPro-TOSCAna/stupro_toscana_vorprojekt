package de.toscana.transformator.engine;

import de.toscana.transformator.model.MachineNode;
import de.toscana.transformator.model.Node;
import de.toscana.transformator.model.TOSCAliteModel;

import java.util.*;

/**
 * The creator class contains methods to get the correct order for creation, start and stop of services.
 *
 * @author Jens Mueller
 *
 */
public class Creator {

    private Map<String,Node> allNodes;
    private ArrayList<ArrayList<Node>> allBranches = new ArrayList<>();


    /**
     * Constructor of the creator class
     *
     * @param topology The TOSCAlite model containing the complete application topology
     */
    public Creator(TOSCAliteModel topology) {
        allNodes = topology.getNodes();
    }


    /**
     * Gets the array with the nodes in ascending order ready for creation.
     * For each machine node there is an own array with all nodes which belong to the machine.
     * The first element has to be installed at first.
     *
     * @return a array of array with nodes
     */
    protected ArrayList<ArrayList<Node>> getAllBranches() {
        findMachines();
        addChildren();
        return allBranches;
    }

    /**
     * Finds machine nodes and creates an own array for each machinenode.
     */
    private void findMachines() {
        for (Map.Entry<String, Node> entry : allNodes.entrySet()) {
            if(entry.getValue() instanceof MachineNode){
                allBranches.add(new ArrayList<>(Arrays.asList(entry.getValue())));
            }
        }
    }


    /**
     * Adds the nodes to the array by ascending order.
     */
    private void addChildren(){
        for(ArrayList<Node> currentBranch : allBranches){
            getAllChildren(currentBranch.get(0), currentBranch);
        }
    }

    /**
     * Walks recursively to the last child of a node and adds each node to the array.
     *
     * @param n The current node
     * @param branch current branch list
     */
    private void getAllChildren(Node n, ArrayList<Node> branch){

        if(!branch.contains(n)){
            branch.add(n);
        }

        for(Node child : n.getChildren()){
            getAllChildren(child, branch);
        }

    }


}
