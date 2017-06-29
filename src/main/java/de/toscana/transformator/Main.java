package de.toscana.transformator;

import de.toscana.transformator.engine.Engine;
import de.toscana.transformator.model.ParsingException;
import de.toscana.transformator.model.TOSCAliteModel;
import de.toscana.transformator.util.ConsoleColors;

import java.io.*;

/**
 * Main entry point for the program
 */
class Main {
    private static Engine engine;

    /**
     *
     * @param args the archive file path
     */
    public static void main(String[] args) {
        printTitle();
        if (args.length == 0) {
            System.out.println(ConsoleColors.getErrorString("File-argument missing."));
            return;
        } else {
            String model = getModelXmlFileAsString(args[0]);
            if (model == null) return;
            TOSCAliteModel toscaLiteModel;
            try {
                toscaLiteModel = new TOSCAliteModel(model);
            } catch (ParsingException e) {
                System.err.println(e.getMessage());
                return;
            }
            engine = new Engine(toscaLiteModel);
        }

        setUpController();
    }


    private static void setUpController() {
        Controller controller = new Controller();

        // set listener to react on cli input
        controller.setListener(s -> {
            if (engine != null) {
                controlEngine(s);
            } else {
                System.out.println(ConsoleColors.getErrorString("Something went wrong!"));
                return;
            }
        });
        controller.createReader();
    }

    private static void controlEngine(String s) {
        switch (s) {
            case "create":
                engine.create();
                break;
            case "start":
                engine.start();
                break;
            case "stop":
                engine.stop();
        }
    }

    /**
     * parses the model.xml out of the archive file
     * @param arg the archive file path
     * @return the model.xml as one line string
     */
    private static String getModelXmlFileAsString(String arg) {
        System.out.println("Inputfile: " + arg);
        String string;
        File inputFile = new File(arg);
        ArchiveHandler archiveHandler;
        try {
            archiveHandler = new ArchiveHandler(inputFile);
            string = archiveHandler.getModelXmlFromZip();
        } catch (ArchiveHandler.ArchiveException e) {
            System.err.println(ConsoleColors.getErrorString(e.getMessage()));
            return null;
        }
        return string;
    }

    /**
     * super fancy method
     */
    private static void printTitle() {
        System.out.println("████████╗ ██████╗ ███████╗ ██████╗ █████╗ ██████╗ ██╗   ██╗███████╗██████╗ ██╗  ██╗███████╗██████╗ ███████╗");
        System.out.println("╚══██╔══╝██╔═══██╗██╔════╝██╔════╝██╔══██╗╚════██╗██║   ██║██╔════╝██╔══██╗██║  ██║██╔════╝██╔══██╗██╔════╝");
        System.out.println("   ██║   ██║   ██║███████╗██║     ███████║ █████╔╝██║   ██║███████╗██████╔╝███████║█████╗  ██████╔╝█████╗");
        System.out.println("   ██║   ██║   ██║╚════██║██║     ██╔══██║██╔═══╝ ╚██╗ ██╔╝╚════██║██╔═══╝ ██╔══██║██╔══╝  ██╔══██╗██╔══╝");
        System.out.println("   ██║   ╚██████╔╝███████║╚██████╗██║  ██║███████╗ ╚████╔╝ ███████║██║     ██║  ██║███████╗██║  ██║███████╗");
        System.out.println("   ╚═╝    ╚═════╝ ╚══════╝ ╚═════╝╚═╝  ╚═╝╚══════╝  ╚═══╝  ╚══════╝╚═╝     ╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝╚══════╝");
    }
}