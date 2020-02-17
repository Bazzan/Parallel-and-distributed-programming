package Inlup3ProducerConsumerMyExecutor;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MyExecutor {

    static int size;
    static int currentSize;
    static BlockingQueue<Runnable> blockingQueue;
    Execute execute;

    public static boolean shutDown;
    public static boolean isRunning;

    public MyExecutor() {
        this.size = Runtime.getRuntime().availableProcessors() - 1;
        currentSize = 0;
        blockingQueue = new LinkedBlockingQueue<Runnable>();
        execute = new Execute();
        shutDown = false;

    }

    public MyExecutor(int size) {
        this.size = size;
        currentSize = 0;
        blockingQueue = new LinkedBlockingQueue<Runnable>();
        execute = new Execute();
        shutDown = false;

    }

    public void execute(Runnable runnable) {
        if (shutDown == false) {

            blockingQueue.add(runnable);
            execute.executeTask();

        }
    }

    public void shutDown() {
        shutDown = true;
    }

    public boolean awaitShutDown() throws InterruptedException {

        if (shutDown) {

            for (Thread thread : execute.threadList) {
                if (thread.isAlive()) {
                    return true;
                }
            }

        }
        return false;
    }

}

class Execute implements Runnable {
    LinkedList<Thread> threadList = new LinkedList<Thread>();

    public void executeTask() {
        if (MyExecutor.currentSize < MyExecutor.size) {
            MyExecutor.currentSize++;
            Thread thread = new Thread(new Execute());
            threadList.add(thread);

            thread.start();
        }
    }

    @Override
    public void run() {

        while (true) {
            if (MyExecutor.blockingQueue.size() != 0) {
                MyExecutor.isRunning = true;
                try {
                    MyExecutor.blockingQueue.take().run();
                } catch (InterruptedException e) {
                    System.out.println(e);
                    e.printStackTrace();
                }

            } else {
                return;
            }
        }
    }

}