package ThreadPool;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class FixedThreadPool implements ThreadPool{
    private final List<Thread> workers;
    private final Queue<Runnable> blockingQueue;
    private final int nThreads;
    private boolean isShutDown;

    public FixedThreadPool(int nThreads) {
        this.workers = new ArrayList<>(nThreads);
        this.blockingQueue = new ArrayDeque<>();
        this.nThreads = nThreads;
        this.isShutDown = false;
    }

    @Override
    public void start() throws InterruptedException {
        for(int i = 0; i < nThreads; ++i) {
            workers.add(new Thread(new workerRoutine()));
        }

        for(Thread worker: workers) {
            worker.start();
        }
    }

    @Override
    public void execute(Runnable runnable) {
        synchronized (this) {
            blockingQueue.add(runnable);
            notifyAll();
        }
    }

    @Override
    public synchronized void shutDown() throws InterruptedException {
    }

    private class workerRoutine implements Runnable {

        @Override
        public void run() {
            while(!isShutDown) {
                Runnable task;
                synchronized (FixedThreadPool.this) {
                    while(blockingQueue.isEmpty()) {
                        try {
                            FixedThreadPool.this.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    task = blockingQueue.poll();
                    task.run();
                }
            }
        }
    }
}
