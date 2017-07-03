package de.toscana.transformator.engine;

import com.jcraft.jsch.JSchException;
import de.toscana.transformator.executor.Executor;
import de.toscana.transformator.executor.SSHConnection;
import de.toscana.transformator.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The engine class contains methods to deploy the application and manage it at runtime.
 *
 * @author Marvin Munoz Baron
 * @author Jens Mueller
 */
public class Engine {

    private static final Logger LOG = LoggerFactory.getLogger(Engine.class);

    private ArrayList<ArrayList<Node>> allBranches;
    private Executor ssh;
    private final File zip;
    private final List<Relationship> lstRelations;
    private final Map<String,String> environmentMap;

    /**
     * Constructor of the engine class
     *
     * @param topology The TOSCAlite model containing the complete application topology
     * @param inputZip The zip file containing all artifacts and models
     */
    public Engine(TOSCAliteModel topology, File inputZip) {
        Creator creator=new Creator(topology);
        allBranches = creator.getAllBranches();
        zip = inputZip;
        lstRelations = topology.getRelationships();
        environmentMap = new Environment(topology).getEnvironmentMap();
    }

    /**
     * Creates the whole application with all dependencies.
     * return true if successful, false otherwise
     */
    public boolean create() {
        try {
            helpCreateStart(ArtifactType.CREATE);
            exeConnectsToRelations();
            stop();
            return true;
        } catch (JSchException e) {
            LOG.error("Failed to create instance.", e);
            return false;
        }
    }

    /**
     * Starts all services in the application topology.
     */
    public boolean start() {
        try {
            helpCreateStart(ArtifactType.START);
            return true;
        } catch (JSchException e) {
            LOG.error("Failed to start instance.", e);
            return false;

        }
    }

    /**
     * Stops all services in the application topology.
     * return true if successful, false otherwise
     */
    public boolean stop() {

        for(ArrayList<Node> currentBranch : allBranches){
            MachineNode mNode = (MachineNode) currentBranch.get(0);
            try {
                makeSSHConnection(mNode);
            } catch (JSchException e) {
                LOG.error("Failed to stop instace", e);
                return false;
            }

            for(int i=currentBranch.size()-1;i>0;i--){
                ServiceNode sNode = (ServiceNode) currentBranch.get(i);
                ArtifactPath stopPath = sNode.getImplementationArtifact(ArtifactType.STOP);
                if(stopPath!=null){
                    String stopExe = stopPath.getAbsolutePath();

                    try {
                        ssh.executeScript(stopExe);
                    } catch (JSchException e) {
                        LOG.error("Failed to stop instance.", e);
                        return false;
                    }
                }
            }
            ssh.close();
        }
        return true;

    }

    /**
     * Orchestrates ZIP upload and sends commands through SSH.
     * Depending on the artifact type the method will use create or start scripts.
     *
     * @param type The artifact type which determines the nature of the method
     */
    private void helpCreateStart(ArtifactType type) throws JSchException {

        for(ArrayList<Node> currentBranch : allBranches){
            MachineNode mNode = (MachineNode) currentBranch.get(0);
            makeSSHConnection(mNode);
            if (type == ArtifactType.CREATE){
                ssh.uploadAndUnzipZip(zip);
            }

            for(int i=1; i<currentBranch.size();i++){
                ServiceNode currentNode = (ServiceNode) currentBranch.get(i);
                ArtifactPath createPath = currentNode.getImplementationArtifact(ArtifactType.CREATE);
                ArtifactPath startPath = currentNode.getImplementationArtifact(ArtifactType.START);

                if(type==ArtifactType.CREATE){
                    executeScript(createPath, currentNode.getProperties());
                    executeScript(startPath, currentNode.getProperties());
                } else {
                    executeScript(startPath, currentNode.getProperties());
                }
            }
            ssh.close();
        }
    }

    /**
     *
     * executes all connects-to-relationships
     * @throws JSchException
     */
    private void exeConnectsToRelations() throws JSchException {
        for(Relationship currentRelation : lstRelations){
            if(currentRelation instanceof ConnectsToRelationship){
                Node sourceNode=currentRelation.getSource();

                //find branch in which the source of the realtion belongs to and connects to the vm
                //to execute the relationImplementation
                for(ArrayList<Node> currentBranch : allBranches){
                    if(currentBranch.contains(sourceNode)){
                        MachineNode mNode = (MachineNode) currentBranch.get(0);
                        makeSSHConnection(mNode);
                        ArtifactPath relationPath = ((ConnectsToRelationship) currentRelation).getImplementationArtifact();
                        executeScript(relationPath, sourceNode.getProperties());
                        ssh.close();
                    }
                }
            }

        }
    }

    /**
     * create connection to a VM
     *
     * @param mNode
     */
    private void makeSSHConnection(MachineNode mNode) throws JSchException {
        String ip = mNode.getIpAdress();
        String user = mNode.getUsername();
        String pw = mNode.getPassword();
        ssh = new SSHConnection(user, pw, ip, environmentMap);
        ssh.connect();
    }

    /**
     * test path and if not null execute
     * @param pathToExe
     * @throws JSchException
     */
    private void executeScript(ArtifactPath pathToExe, Map<String,String> environment) throws JSchException {
        if(pathToExe!=null){
            ssh.executeScript(pathToExe.getAbsolutePath());
        }
    }
}
