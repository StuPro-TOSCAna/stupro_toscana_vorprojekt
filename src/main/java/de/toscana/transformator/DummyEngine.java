package de.toscana.transformator;

/**
 * Created by nick on 22.06.17.
 */
public class DummyEngine extends Thread {
    public DummyEngine() {
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
        System.out.println("My thread is in running state.");
        //keeps the thread alive
        while (!Thread.currentThread().isInterrupted()) {
        }
    }

}

