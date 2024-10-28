package ThreadPool;

import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

public class TestScalableThreadPool {

    @Test
    public void atomicTest() throws InterruptedException {
        SimpleAtomicInteger simpleAtomicInteger = new SimpleAtomicInteger(0);
        ThreadPool threadPool = new ScalableThreadPool(1, 5);

        threadPool.start();

        for(int i = 0; i < 10_000_000; ++i) {
            threadPool.execute(() -> {
                simpleAtomicInteger.incrementAndGet();
            });
        }

        threadPool.Wait(); // Ждем, пока все таски завершатся
        threadPool.shutDown();

        Assert.assertEquals(10_000_000, simpleAtomicInteger.get());
    }

    @Test
    public void firstThreadCountTest() throws InterruptedException {
        SimpleAtomicInteger simpleAtomicInteger = new SimpleAtomicInteger(0);
        ThreadPool threadPool = new ScalableThreadPool(1, 10);

        threadPool.start();

        // много задач => используем все 10 тредов
        for(int i = 0; i < 10_000_000; ++i) {
            threadPool.execute(() -> {
                simpleAtomicInteger.incrementAndGet();
            });
        }

        int firstTaskGroupThreadCount = 0;
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        for (Thread t : threadSet){
            if(t.getName().matches("Thread-\\d+")) {
                ++firstTaskGroupThreadCount;
            }
        }

        threadPool.Wait();

        // легкая задача => ожидаем 1 - 2 треда
        for(int i = 0; i < 10; ++i) {
            threadPool.execute(() -> {
                simpleAtomicInteger.incrementAndGet();
            });
        }

        int secondTaskGroupThreadCount = 0;
        threadSet = Thread.getAllStackTraces().keySet();
        for (Thread t : threadSet){
            if(t.getName().matches("Thread-\\d+")) {
                ++secondTaskGroupThreadCount;
            }
        }

        threadPool.Wait();
        threadPool.shutDown();

        Assert.assertEquals(10, firstTaskGroupThreadCount);
        Assert.assertTrue(1 <= secondTaskGroupThreadCount && secondTaskGroupThreadCount < 5);
    }
}
