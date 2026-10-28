import java.io.Serializable;
public class SavingsAccount extends MiniBank.Account implements Serializable {
    private static final long serialVersionUID = 1L;
    long minBalance;
    
    public SavingsAccount(String ownerName, long openingBalance, long minBalance) {
        super(ownerName, openingBalance);
        this.minBalance = minBalance;
    }
    
    public SavingsAccount(String ownerName, long minBalance) {
        super(ownerName, 0);
        this.minBalance = minBalance;
    }
    
    @Override
    public double interestRate() {
        return 4.0;
    }
    
    @Override
    public boolean canWithdraw(long amount) {
        if (getBalance() - amount >= minBalance) {
            return true;
        }
        return false;
    }
}
