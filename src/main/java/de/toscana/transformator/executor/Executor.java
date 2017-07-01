package de.toscana.transformator.executor;

import java.io.File;

/**
 * Created by oliver on 22.06.2017.
 */
public interface Executor {

    String sendCommand(String command);
    boolean uploadFile(File file, String targetPath);
}
