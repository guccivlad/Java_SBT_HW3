package ThreadPool;

import java.util.ArrayDeque;
import java.util.Queue;

public class FixedThreadPool implements ThreadPool{
    private final Worker[] workers;
    private final BlockingQueue blockingQueue;
    private final int nThreads;

    public FixedThreadPool(int nThreads) {
        this.workers = new Worker[nThreads];
        this.blockingQueue = new BlockingQueue();
        this.nThreads = nThreads;
    }

    @Override
    public void start() {
        for(int i = 0; i < nThreads; ++i) {
            workers[i] = new Worker();
            workers[i].start();
        }
    }

    @Override
    public void execute(Runnable runnable) {
        blockingQueue.push(runnable);
    }

    @Override
    public void shutDown() throws InterruptedException {
        for(Worker worker: workers) {
            worker.interrupt();
        }
        for(Worker worker: workers) {
            worker.join();
        }
    }

    private class Worker extends Thread {

        @Override
        public void run() {
            Runnable task;

            while(!isInterrupted()) {
                try {
                    task = blockingQueue.get();
                    task.run();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private class BlockingQueue {
        private Queue<Runnable> tasks = new ArrayDeque<>();

        public synchronized void push(Runnable task) {
            tasks.add(task);
            notifyAll();
        }

        public synchronized Runnable get() throws InterruptedException {
            while(tasks.isEmpty()) {
                wait();
            }
            Runnable task = tasks.poll();
            notifyAll();
            return task;
        }
    }
}
