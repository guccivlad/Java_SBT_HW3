package ThreadPool;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TestThreadPool {

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
