import java.io.Serializable;
public class CurrentAccount extends MiniBank.Account implements Serializable {
    private static final long serialVersionUID = 1L;
    long overdraftLimit;
    
    public CurrentAccount(String ownerName, long openingBalance, long overdraftLimit) {
        super(ownerName, openingBalance);
        this.overdraftLimit = overdraftLimit;
    }
    
    public CurrentAccount(String ownerName, long overdraftLimit) {
        super(ownerName, 0);
        this.overdraftLimit = overdraftLimit;
    }
    
    @Override
    public double interestRate() {
        return 0.0;
    }
    
    @Override
    public boolean canWithdraw(long amount) {
        if (getBalance() - amount >= -overdraftLimit) {
            return true;
        }
        return false;
    }
}
