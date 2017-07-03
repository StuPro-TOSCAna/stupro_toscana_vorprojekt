package de.toscana.transformator.engine;

import de.toscana.transformator.model.ArtifactPath;
import de.toscana.transformator.model.Node;
import de.toscana.transformator.model.ServiceNode;
import de.toscana.transformator.model.TOSCAliteModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hnicke on 03.07.17.
 * Helper class which provides all properties and deployment artifacts in form of a map.
 */
public class Environment {

    private final TOSCAliteModel topology;
    private final Map<String,String> environmentMap = new HashMap<>();

    Environment(TOSCAliteModel topology){
        this.topology = topology;
        populateEnvironmentMap();
    }

    private void populateEnvironmentMap() {
        Map<String,Node> nodes =topology.getNodes();
        for (String nodeName : nodes.keySet()){
           Node node = nodes.get(nodeName);
           addProperties(node);
           if (node instanceof ServiceNode){
               ServiceNode sNode = (ServiceNode) node;
               addDeploymentArtifacts(sNode);

           }
        }
    }


    private void addProperties(Node node) {
        Map<String,String> properties = node.getProperties();
        for(String key : properties.keySet()){
            String value = properties.get(key);
            addToEnvironment(node, key, value);

        }
    }

    private void addDeploymentArtifacts(ServiceNode node) {
        Map<String, ArtifactPath> artifacts = node.getDeploymentArtifacts();
        for(String key : artifacts.keySet()){
            ArtifactPath artifact = artifacts.get(key);
            String absolutePath = artifact.getAbsolutePath();
            String relativePath = absolutePath.substring(absolutePath.lastIndexOf("/") + 1);
            addToEnvironment(node, key, relativePath);
        }
    }

    private void addToEnvironment(Node node, String key, String value){
        String variableKey = (node.getName() + "_" + key).toUpperCase();
        environmentMap.put(variableKey, value);
    }

    Map<String,String> getEnvironmentMap(){
        return environmentMap;
    }
}
