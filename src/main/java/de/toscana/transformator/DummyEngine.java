package de.toscana.transformator;

import java.io.File;

/**
 * Created by nick on 22.06.17.
 */
public class DummyEngine extends Thread {

    public void startEngine(){
        start();
    }

    public void stopThread() {
        interrupt();
    }

    public void startModel() {
        System.out.println("start");
    }

    public void stopModel() {
        System.out.println("stop");
    }

    public void run() {
        System.out.println("Engine is ready!");
        //keeps the thread alive
        while (!Thread.currentThread().isInterrupted()) {
        }
    }

}

