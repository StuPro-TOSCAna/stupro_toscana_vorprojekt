package de.toscana.transformator;

import de.toscana.transformator.engine.Engine;
import de.toscana.transformator.model.ParsingException;
import de.toscana.transformator.model.TOSCAliteModel;
import de.toscana.transformator.util.ConsoleColors;

import java.io.*;


class Main {
    private static Engine engine;

    public static void main(String[] args) {
        printInfo();
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
        controller.setListener(s -> {
            if (engine != null) {
                if ("start".equals(s)) engine.start();
                else if ("stop".equals(s)) engine.stop();
            } else {
                System.out.println(ConsoleColors.getErrorString("Something went wrong!"));
                //noinspection UnnecessaryReturnStatement
                return;
            }
        });
        controller.createReader();
    }

    private static String getModelXmlFileAsString(String arg) {
        System.out.println("Inputfile: " + arg);
        String string;
        File inputFile = new File(arg);
        ArchiveHandler archiveHandler;
        try {
            archiveHandler = new ArchiveHandler(inputFile);
            string = archiveHandler.getModelXml();
        } catch (ArchiveHandler.ArchiveException e) {
            System.err.println(ConsoleColors.getErrorString(e.getMessage()));
            return null;
        }
        return string;
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