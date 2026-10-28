import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TransactionProcessor {
    private ExecutorService executorService;
    
    public TransactionProcessor() {
        executorService = Executors.newFixedThreadPool(4);
    }
    
    public void submit(Runnable task) {
        executorService.execute(task);
    }
    
    public void stop() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
