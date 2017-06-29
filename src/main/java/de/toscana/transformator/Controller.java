package de.toscana.transformator;

import jline.console.ConsoleReader;
import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;


import java.io.IOException;
import java.io.PrintWriter;
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

    private static void printHelp() {
        // TODO: 22.06.17 better description
        System.out.println("create");
        System.out.println("start");
        System.out.println("stop");
    }

    public void setListener(ControllerListener listener) {
        this.listener = listener;
    }

    public void createReader() {
        try {
            reader = new ConsoleReader();
            reader.setPrompt(ANSI_CYAN + "tosca2vsphere\u001B[0m> ");
            setUpCompletors(reader);

            String line;
            PrintWriter out = new PrintWriter(reader.getOutput());

            while ((line = reader.readLine()) != null) {
                if ("help".equals(line)) {
                    printHelp();
                    break;
                } else if (actionsForEngine.contains(line)) {
                    listener.action(line);
                }
                out.flush();

                if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) {
                    listener.action("exit");
                    break;
                }
                if (line.equalsIgnoreCase("cls")) {
                    reader.clearScreen();
                }
            }

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void stopReader() {
        try {
            reader.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
        reader = null;
    }

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
