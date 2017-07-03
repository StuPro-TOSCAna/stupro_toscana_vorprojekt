package de.toscana.transformator;

import de.toscana.transformator.engine.Engine;
import de.toscana.transformator.model.ParsingException;
import de.toscana.transformator.model.TOSCAliteModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Main entry point for the program
 *
 * @Author Nick Fode
 */
class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private static Engine engine;

    /**
     *
     * @param args the archive file path
     */
    public static void main(String[] args) {
        printTitle();
        if (args.length == 0) {
            System.err.println("File-argument missing.");
            return;
        } else {
            File archive = new File(args[0]);
            String model = getModelXmlFileAsString(archive);
            if (model == null) return;
            TOSCAliteModel toscaLiteModel;
            try {
                toscaLiteModel = new TOSCAliteModel(model);
            } catch (ParsingException e) {
                return;
            }
            engine = new Engine(toscaLiteModel, archive);
        }

        setUpController();
    }

    private static Controller controller;
    private static void setUpController() {
        controller = new Controller();

        // set listener to react on cli input
        controller.setListener(s -> {
            if (engine != null) {
                controlEngine(s);
            } else {
                LOG.error("Something went wrong!");
                return;
            }
        });
        try {
            controller.createReader();
        } catch (IOException e) {
            return;
        }
    }

    private static void controlEngine(String s) {
        boolean success = true;
        switch (s) {
            case "create":
                success = engine.create();
                controller.changeStatusInPrompt("created");
                break;
            case "start":
                success = engine.start();
                controller.changeStatusInPrompt("started");
                break;
            case "stop":
                success = engine.stop();
                controller.changeStatusInPrompt("stopped");
                break;
            case "exit":
                LOG.info("shutdown");
                System.exit(0);
                break;
        }
        if (!success){
            LOG.error("Unsuccessfully executed command '{}', aborting", s);
            System.exit(1);
        }
    }

    /**
     * parses the model.xml out of the archive file
     * @param file the archive file
     * @return the model.xml as one line string
     */
    private static String getModelXmlFileAsString(File file) {
        File archive = file;
        System.out.println("Inputfile: " + archive.getName());
        String string;
        ArchiveHandler archiveHandler;
        try {
            archiveHandler = new ArchiveHandler(archive);
            string = archiveHandler.getModelXmlFromZip();
        } catch (ArchiveHandler.ArchiveException e) {
            LOG.error("Parsing the archive failed",e);
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
