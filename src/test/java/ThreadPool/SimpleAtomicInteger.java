package ThreadPool;

public class SimpleAtomicInteger {
    private int value;

    public SimpleAtomicInteger(int initialValue) {
        this.value = initialValue;
    }

    public synchronized int get() {
        return value;
    }

    public synchronized int incrementAndGet() {
        return ++value;
    }
}
