public class ProducerConsumerDemo {
    private String[] buffer = new String[3];
    private int count = 0;
    
    public synchronized void produce(String transaction) throws InterruptedException {
        while (count == buffer.length) {
            wait();
        }
        buffer[count++] = transaction;
        System.out.println("Produced: " + transaction);
        notify();
    }
    
    public synchronized String consume() throws InterruptedException {
        while (count == 0) {
            wait();
        }
        String transaction = buffer[--count];
        System.out.println("Consumed: " + transaction);
        notify();
        return transaction;
    }
}
