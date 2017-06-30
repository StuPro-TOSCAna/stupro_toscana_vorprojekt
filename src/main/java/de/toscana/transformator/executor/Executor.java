package de.toscana.transformator.executor;

/**
 * Created by oliver on 22.06.2017.
 */
public interface Executor {

    String sendCommand(String command);
    boolean uploadFile(String filename, String targetDirectory);
}
