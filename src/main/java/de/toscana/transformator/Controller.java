package de.toscana.transformator;

import jline.console.ConsoleReader;
import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by nick on 22.06.17.
 */
public class Controller {
    public interface ControllerListener{
        void  action(String s);
    }
    private List<String> actionsForEngine;
    ControllerListener listener;
    public Controller(){
        this.actionsForEngine = Arrays.asList("start","stop");
    }
    public void setListener(ControllerListener listener){
        this.listener = listener;
    }
    private static void printHelp() {
        System.out.println("start");
        System.out.println("stop");
    }
    private ConsoleReader reader;
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public void createReader(){
        try {
            reader = new ConsoleReader();
            reader.setPrompt(ANSI_GREEN + "tosca2vsphere\u001B[0m> ");
            setUpCompletors(reader);

            String line;
            PrintWriter out = new PrintWriter(reader.getOutput());

            while ((line = reader.readLine()) != null) {
                if ("help".equals(line)){
                    printHelp();
                    break;
                } else if(actionsForEngine.contains(line)){
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

        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void stopReader(){
        try {
            reader.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
        reader = null;
    }

    private void setUpCompletors(ConsoleReader reader) {
        List<Completer> completors = new LinkedList<Completer>();
        completors.add(new StringsCompleter("create","start","stop","exit","help"));
        for (Completer c : completors) {
            reader.addCompleter(c);
        }
    }
}
