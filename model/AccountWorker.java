package model;

public class AccountWorker implements Runnable {
    private Object account;
    private int times;
    private long amount;
    
    public AccountWorker(Object account, int times, long amount) {
        this.account = account;
        this.times = times;
        this.amount = amount;
    }
    
    @Override
    public void run() {
        for (int i = 0; i < times; i++) {
            try {
                java.lang.reflect.Method depositMethod = account.getClass().getMethod("deposit", long.class);
                depositMethod.invoke(account, amount);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " deposited " + amount);
        }
    }
}
