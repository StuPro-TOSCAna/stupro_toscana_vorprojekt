package de.toscana.transformator;

import jline.console.ConsoleReader;
import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;


import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static de.toscana.transformator.util.ConsoleColors.ANSI_CYAN;

/**
 * Created by nick on 22.06.17.
 */
class Controller {
    private final List<String> actionsForEngine;
    private ControllerListener listener;
    private ConsoleReader reader;

    public Controller() {
        this.actionsForEngine = Arrays.asList("create","start", "stop");
    }

    /**
     * prints the help text
     */
    private static void printHelp() {
        // TODO: 22.06.17 better description
        System.out.println("create - create the model");
        System.out.println("start - start the model");
        System.out.println("stop - stop the model");
    }

    public void setListener(ControllerListener listener) {
        this.listener = listener;
    }

    /**
     * creates the cli and listens for input
     */
    public void createReader() {
        try {
            reader = new ConsoleReader();
            reader.setPrompt(ANSI_CYAN + "tosca2vsphere\u001B[0m> ");
            setUpCompletors(reader);
            String line;
            while ((line = reader.readLine()) != null) {
                if (handleInput(line)) break;
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * handles what the user enters into the cli
     * @param line the text the user enters
     *
     * @return false if the user wants to exit
     * @throws IOException
     */
    private boolean handleInput(String line) throws IOException {
        if ("help".equals(line)) {
            printHelp();
        }
        // checks if input is one of the valid commands for the engine
        if (actionsForEngine.contains(line)) {
            listener.action(line);
        }

        if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) {
            listener.action("exit");
            return true;
        }

        //clears the screen for the current reader
        if (line.equalsIgnoreCase("cls")) {
            reader.clearScreen();
        }
        return false;
    }

    public void stopReader() {
        try {
            reader.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
        reader = null;
    }

    /**
     * sets up the completors that a user can use with tab
     * @param reader  the current reader
     */
    private void setUpCompletors(ConsoleReader reader) {
        List<Completer> completors = new LinkedList<>();
        completors.add(new StringsCompleter("create", "start", "stop", "exit", "help"));
        for (Completer c : completors) {
            reader.addCompleter(c);
        }
    }

    public interface ControllerListener {
        void action(String s);
    }
}
