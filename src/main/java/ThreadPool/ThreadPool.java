package ThreadPool;

public interface ThreadPool {
    // запускает потоки.
    // Потоки бездействуют, до тех пор пока не появится новое задание в очереди (см. execute)
    void start() throws InterruptedException;

    // складывает это задание в очередь.
    // Освободившийся поток должен выполнить это задание. Каждое задание должно быть выполнено ровно 1 раз
    void execute(Runnable runnable) throws InterruptedException;

    // Завершаем потоки(добавил)
    void shutDown() throws InterruptedException;

     // Ждем, пока все задачи завершатся(добавил)
    void Wait() throws InterruptedException;
}
