package ThreadPool;

import java.util.ArrayDeque;
import java.util.Queue;

public class ScalableThreadPool implements ThreadPool {
    private final int maxThreadsCount;
    private final int minThreadsCount;
    private int currentWorkingThreadsCount;
    private final BlockingQueue blockingQueue;
    private final Worker[] workers;

    public ScalableThreadPool(int minThreadsCount, int maxThreadsCount) {
        this.minThreadsCount = minThreadsCount;
        this.maxThreadsCount = maxThreadsCount;
        this.blockingQueue = new BlockingQueue();
        this.workers = new Worker[maxThreadsCount];
        this.currentWorkingThreadsCount = minThreadsCount;
    }

    @Override
    public void start() throws InterruptedException {
        for(int i = 0; i < minThreadsCount; ++i) {
            workers[i] = new Worker();
            workers[i].start();
        }
    }

    @Override
    public void execute(Runnable runnable) throws InterruptedException {
        blockingQueue.push(runnable);

        if(currentWorkingThreadsCount < maxThreadsCount &&
                blockingQueue.Size() > currentWorkingThreadsCount) {
            startNewWorker();
        }
    }

    @Override
    public void shutDown() throws InterruptedException {
        for(int i = 0; i < currentWorkingThreadsCount; ++i) {
            workers[i].interrupt();
        }
    }

    @Override
    public void Wait() throws InterruptedException {
        Thread.currentThread().sleep(5_000); // :)
        deleteUnnecessaryThread();
    }

    private void startNewWorker() {
        workers[currentWorkingThreadsCount] = new Worker();
        workers[currentWorkingThreadsCount].start();
        ++currentWorkingThreadsCount;
    }

    private void deleteUnnecessaryThread() {
        if(blockingQueue.Size() == 0) {
            for(int i = minThreadsCount; i < currentWorkingThreadsCount; ++i) {
                workers[i].interrupt();
            }
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

        public int Size() {
            return tasks.size();
        }
    }
}
