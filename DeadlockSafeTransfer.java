import exception.InsufficientFundsException;
import exception.InvalidAmountException;

public class DeadlockSafeTransfer {
    public static void transfer(MiniBank.Account from, MiniBank.Account to, long amount)
            throws InvalidAmountException, InsufficientFundsException {
        if (amount <= 0) {
            throw new InvalidAmountException("Amount must be greater than 0.");
        }
        if (from == to) {
            throw new InvalidAmountException("Cannot transfer to the same account.");
        }

        // Always lock in a fixed order (by account number) to avoid deadlock
        MiniBank.Account lock1 = from;
        MiniBank.Account lock2 = to;
        if (lock1.getAccountNumber().compareTo(lock2.getAccountNumber()) > 0) {
            lock1 = to;
            lock2 = from;
        }

        synchronized (lock1) {
            synchronized (lock2) {
                if (!from.withdraw(amount)) {
                    throw new InsufficientFundsException(
                        "Insufficient funds in " + from.getAccountNumber(),
                        Math.max(0, amount - from.getBalance()));
                }
                to.deposit(amount);
            }
        }
    }
}
