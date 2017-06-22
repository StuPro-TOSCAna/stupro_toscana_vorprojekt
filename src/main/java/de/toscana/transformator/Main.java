package de.toscana.transformator;

import de.toscana.transformator.util.ConsoleColors;

import java.io.File;

public class Main {
    private static DummyEngine engine;

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println(ConsoleColors.getColorizedString(ConsoleColors.ANSI_RED, "File-argument missing."));
            return;
        } else {
            File file = handleInputFile(args[0]);
            if (file != null) parseFile(file);
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

    private static void parseFile(File file) {
        System.out.println("Parsing file!");
    }

    private static File handleInputFile(String arg) {
        System.out.println("Inputfile: " + arg);
        File inputFile = new File(arg);
        String fileName = inputFile.getName();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        if (validateFileExistence(inputFile)) return null;
        if (validateExtension(extension)) return null;
        return inputFile;
    }

    private static boolean validateFileExistence(File inputFile) {
        if (!inputFile.exists()) {
            System.err.println(ConsoleColors.getColorizedString(ConsoleColors.ANSI_RED, "This file does not exist!"));
            return true;
        }
        return false;
    }

    private static boolean validateExtension(String extension) {
        if (!"zip".equalsIgnoreCase(extension)) {
            System.err.println(ConsoleColors.getColorizedString(ConsoleColors.ANSI_RED, "Wrong file extension!"));
            return true;
        }
        return false;
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