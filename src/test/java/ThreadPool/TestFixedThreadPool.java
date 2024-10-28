package ThreadPool;

import org.junit.Assert;
import org.junit.Test;

public class TestFixedThreadPool {

    @Test
    public void atomicTest() throws InterruptedException {
        SimpleAtomicInteger simpleAtomicInteger = new SimpleAtomicInteger(0);
        ThreadPool threadPool = new FixedThreadPool(3);

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
}
