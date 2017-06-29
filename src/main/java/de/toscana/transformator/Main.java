package de.toscana.transformator;

import de.toscana.transformator.model.ParsingException;
import de.toscana.transformator.model.TOSCAliteModel;
import de.toscana.transformator.util.ConsoleColors;

import java.io.*;


public class Main {
    private static DummyEngine engine;

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println(ConsoleColors.getColorizedString(ConsoleColors.ANSI_RED, "File-argument missing."));
            return;
        } else {
            String model = getModelXmlAsString(args[0]);
            if (model == null) return;
            try {
                TOSCAliteModel toscAliteModel = new TOSCAliteModel(model);
            } catch (ParsingException e) {
                e.printStackTrace();
            }
        }
        printInfo();
        startEngine();
        setUpController();
    }

    private static void setUpController() {
        Controller controller = new Controller();
        controller.setListener(s -> {
            if (engine.isAlive()) {
                if ("start".equals(s)) engine.startModel();
                else if ("stop".equals(s)) engine.stopModel();
                else engine.stopThread();
            } else {
                System.out.println(ConsoleColors.getColorizedString(ConsoleColors.ANSI_RED, "Something went wrong!"));
                return;
            }
        });
        controller.createReader();
    }

    private static String getModelXmlAsString(String arg) {
        System.out.println("Inputfile: " + arg);
        String string;
        File inputFile = new File(arg);
        ArchiveHandler archiveHandler;
        try {
            archiveHandler = new ArchiveHandler(inputFile);
            string = archiveHandler.parseModelXml();
        } catch (ArchiveHandler.ArchiveException e) {
            System.err.println(ConsoleColors.getColorizedString(ConsoleColors.ANSI_RED, e.getMessage()));
            return null;
        }

        return string;
    }


    private static void startEngine() {
        engine = new DummyEngine();
        engine.startEngine();
    }

    private static void printInfo() {
        System.out.println("████████╗ ██████╗ ███████╗ ██████╗ █████╗ ██████╗ ██╗   ██╗███████╗██████╗ ██╗  ██╗███████╗██████╗ ███████╗");
        System.out.println("╚══██╔══╝██╔═══██╗██╔════╝██╔════╝██╔══██╗╚════██╗██║   ██║██╔════╝██╔══██╗██║  ██║██╔════╝██╔══██╗██╔════╝");
        System.out.println("   ██║   ██║   ██║███████╗██║     ███████║ █████╔╝██║   ██║███████╗██████╔╝███████║█████╗  ██████╔╝█████╗");
        System.out.println("   ██║   ██║   ██║╚════██║██║     ██╔══██║██╔═══╝ ╚██╗ ██╔╝╚════██║██╔═══╝ ██╔══██║██╔══╝  ██╔══██╗██╔══╝");
        System.out.println("   ██║   ╚██████╔╝███████║╚██████╗██║  ██║███████╗ ╚████╔╝ ███████║██║     ██║  ██║███████╗██║  ██║███████╗");
        System.out.println("   ╚═╝    ╚═════╝ ╚══════╝ ╚═════╝╚═╝  ╚═╝╚══════╝  ╚═══╝  ╚══════╝╚═╝     ╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝╚══════╝");
    }
}