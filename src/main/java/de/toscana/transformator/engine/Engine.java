package de.toscana.transformator.engine;

import com.jcraft.jsch.JSchException;
import de.toscana.transformator.executor.Executor;
import de.toscana.transformator.executor.SSHConnection;
import de.toscana.transformator.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

/**
 * The engine class contains methods to deploy the application and manage it at runtime.
 *
 * @author Marvin Munoz Baron
 * @author Jens Mueller
 *
 */
public class Engine {

    private static final Logger LOG = LoggerFactory.getLogger(Engine.class);

    private final Creator creator;
    private final ArrayList<Queue> lstMachineQueues;
    private final ArrayList<Queue> copyLstMachineQueues;
    private Executor ssh;
    private final File zip;
    private final List<Relationship> lstRelations;

    /**
     * Constructor of the engine class
     * for each command you need a new instance of Constructor TODO: What does this mean?
     * @param topology The TOSCAlite model containing the complete application topology
     * @param inputZip The zip file containing all artifacts and models
     */
    public Engine(TOSCAliteModel topology, File inputZip) {
        creator=new Creator(topology);
        lstMachineQueues = creator.getAllQueues();
        copyLstMachineQueues = lstMachineQueues;
        ssh = null;
        zip = inputZip;
        lstRelations=topology.getRelationships();
    }

    /**
     * Creates the whole application with all dependencies.
     * return true if successful, false otherwise
     */
    public boolean create() {
        try {
            helpCreateStart(ArtifactType.CREATE);
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
        for(Queue<Node> nodesForCreation : lstMachineQueues){
            MachineNode mNode = (MachineNode) nodesForCreation.peek();
            makeSSHConnection(mNode);
            //get stop-artifact of nodes in descending order
            while (!nodesForCreation.isEmpty()) {
                Iterator<Node> iterator= nodesForCreation.iterator();
                ServiceNode currentNode=null;
                while(iterator.hasNext()){
                    currentNode = (ServiceNode) iterator.next();
                }
                nodesForCreation.remove(currentNode);
                String path=currentNode.getImplementationArtifact(ArtifactType.STOP).getAbsolutePath();
                //TODO: possibly change path to proper command? Does "path" work as a command?
                try {
                    ssh.executeScript(path);
                } catch (JSchException e) {
                    LOG.error("Failed to stop instance.", e);
                    return false;
                }
            }
            //close ssh-connection
            ssh.close();
        }
        return true;
    }

    /**
     * Orchestrates ZIP upload and sends commands through SSH.
     * Depending on the artifact type the method will use create or start scripts.
     * @param type The artifact type which determines the nature of the method
     */
    private void helpCreateStart(ArtifactType type) throws JSchException {
        for(Queue<Node> nodesForCreation : lstMachineQueues){
            Node mNode = nodesForCreation.poll();
            if (mNode instanceof MachineNode){
               makeSSHConnection((MachineNode) mNode);
                if (type == ArtifactType.CREATE){
                    ssh.uploadAndUnzipZip(zip);
                }
            }

            while (!nodesForCreation.isEmpty()) {
                Node nodeToInstall = nodesForCreation.poll();
                String pathCreate="";
                String pathStart="";

                //instance of ssh Connection
                if (nodeToInstall instanceof ServiceNode){
                    ArtifactPath startArti = ((ServiceNode) nodeToInstall).getImplementationArtifact(ArtifactType.START);
                    ArtifactPath createArti = ((ServiceNode) nodeToInstall).getImplementationArtifact(ArtifactType.CREATE);
                    if(startArti!=null){
                        pathStart=startArti.getAbsolutePath();
                    }

                    if(type == ArtifactType.CREATE){
                        if(createArti!=null){
                            pathCreate=createArti.getAbsolutePath();
                            ssh.executeScript(pathCreate);
                        }

                        if(startArti!=null){
                            ssh.executeScript(pathStart);
                        }

                    } else if(type == ArtifactType.START){
                        if(startArti!=null){
                            ssh.executeScript(pathStart);
                        }
                    }
                }
            }
            ssh.close();
        }
        if(type == ArtifactType.CREATE){
            try {
                executeConnects();
            } catch (JSchException e){
                LOG.error("Failed to execute connects-to Relationship", e);
            }
        }
    }

    /**
     *
     * executes all connects to relationships
     * @throws JSchException
     */
    private void executeConnects() throws JSchException {
        for(Relationship rel : lstRelations){
            if(rel instanceof ConnectsToRelationship){
                Node node=rel.getSource();
                for(Queue qu : copyLstMachineQueues){
                    if(qu.contains(node)){
                        MachineNode mNode=(MachineNode) qu.peek();
                        makeSSHConnection(mNode);
                        ArtifactPath relArti = ((ConnectsToRelationship) rel).getImplementationArtifact();
                        if(relArti!=null){
                            String relPath = relArti.getAbsolutePath();
                            ssh.executeScript(relPath);
                        }
                        ssh.close();
                    }
                }
            }
        }
    }

    /**
     * create connection to a VM
     * @param mNode
     */
    private void makeSSHConnection(MachineNode mNode){
        String ip = ((MachineNode) mNode).getIpAdress();
        String user = ((MachineNode) mNode).getUsername();
        String pw = ((MachineNode) mNode).getPassword();
        ssh = new SSHConnection(user, pw, ip);
        ssh.connect();
    }
}
