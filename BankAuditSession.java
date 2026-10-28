public class BankAuditSession implements AutoCloseable {
    
    public BankAuditSession() {
        System.out.println("Bank Audit Session Started");
    }
    
    public void audit() {
        System.out.println("Performing audit...");
    }
    
    @Override
    public void close() {
        System.out.println("Bank Audit Session Closed");
    }
}
