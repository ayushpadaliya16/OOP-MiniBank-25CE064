public class StatementFormatter {
    
    public static String buildStatement(MiniBank.Account account) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Account Statement ===\n");
        sb.append("Account Number: ").append(account.getAccountNumber()).append("\n");
        sb.append("Owner Name: ").append(account.getOwnerName()).append("\n");
        sb.append("Balance: ").append(account.getBalance()).append("\n");
        sb.append("==========================");
        return sb.toString();
    }
}
