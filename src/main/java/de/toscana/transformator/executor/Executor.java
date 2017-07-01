package de.toscana.transformator.executor;

import java.io.File;

/**
 * Created by oliver on 22.06.2017.
 */
public interface Executor {

    String sendCommand(String command);
    String sendCommand(String nodename, String command);
    boolean uploadFile(File file, String targetPath);
}
