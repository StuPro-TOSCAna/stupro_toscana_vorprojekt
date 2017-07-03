package de.toscana.transformator;

import jline.console.ConsoleReader;
import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by nick on 22.06.17.
 */
class Controller {
    private final List<String> actionsForEngine;
    private ControllerListener listener;
    private ConsoleReader reader;
    private static final Logger LOG = LoggerFactory.getLogger(Controller.class);

    public Controller() {
        this.actionsForEngine = Arrays.asList("create","start", "stop");
    }

    /**
     * prints the help text
     */
    private static void printHelp() {
        // TODO: 22.06.17 better description
        System.out.println("help - show the help");
        System.out.println("create - create the model");
        System.out.println("start - start the model");
        System.out.println("stop - stop the model");
        System.out.println("cls - clean the screen");
        System.out.println("exit - leave the CLI");
        System.out.println("quit - leave the CLI");
    }

    public void setListener(ControllerListener listener) {
        this.listener = listener;
    }



    /**
     * creates the cli and listens for input
     */
    public void createReader() throws IOException {
        try {
            reader = new ConsoleReader();
            changeStatusInPrompt("parsed");
            setUpCompletors(reader);
            String line;
            while ((line = reader.readLine()) != null) {
                if (handleInput(line)) break;
            }
        } catch (IOException ex) {
            LOG.error("Setting up the CLI failed.",ex);
            throw ex;
        }
    }

    public void changeStatusInPrompt(String s) {
        reader.setPrompt("tosca2vsphere("+s+")> ");
    }

    /**
     * handles what the user enters into the cli
     * @param line the text the user enters
     *
     * @return false if the user wants to exit
     * @throws IOException
     */
    private boolean handleInput(String line) throws IOException {
        line = line.trim();
        if ("help".equals(line)) {
            printHelp();
        }
        // checks if input is one of the valid commands for the engine
        else if (actionsForEngine.contains(line)) {
            listener.action(line);
        }

        else if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) {
            listener.action("exit");
            return true;
        }

        //clears the screen for the current reader
        else if (line.equalsIgnoreCase("cls")) {
            reader.clearScreen();
        }
        else {
                System.out.println("Unknown command. Available commands:");
                printHelp();
        }
        return false;
    }

    public void stopReader() throws IOException {
        try {
            reader.delete();
        } catch (IOException e) {
            LOG.error("Stopping the reader failed.",e);
            throw e;
        }
        reader = null;
    }

    /**
     * sets up the completors that a user can use with tab
     * @param reader  the current reader
     */
    private void setUpCompletors(ConsoleReader reader) {
        List<Completer> completors = new LinkedList<>();
        completors.add(new StringsCompleter("create", "start", "stop", "exit", "help", "cls"));
        for (Completer c : completors) {
            reader.addCompleter(c);
        }
    }

    public interface ControllerListener {
        void action(String s);
    }
}
