package de.toscana.transformator.executor;

import java.io.File;

/**
 * Created by oliver on 22.06.2017.
 */
public interface Executor {

    /**
     * sending a simple command
     * @param command
     * @return
     */
    String sendCommand(String command);

    /**
     * executes the script
     *
     * @param script expects nodename/scriptname
     * @return
     */
    String executeScript(String script);

    /**
     * Uploads the file to the machine and unzips it
     * @param file expects a .zip file
     * @return
     */
    String uploadAndUnzipZip(File file);
}
