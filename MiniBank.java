import java.util.Scanner;
import java.io.Serializable;
import java.util.ArrayList;
import exception.AccountNotFoundException;
import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;

public class MiniBank {
    
    private static final long serialVersionUID = 1L;
    
    public static <N extends Number> double average(N[] values) {
        if (values == null || values.length == 0) {
            return 0.0;
        }
        double sum = 0.0;
        for (N value : values) {
            if (value != null) {
                sum += value.doubleValue();
            }
        }
        return sum / values.length;
    }
    
    public record BankInfo(String name, String branch) {}
    
    public enum MenuOption {
        OPEN_ACCOUNT,
        DEPOSIT,
        WITHDRAW,
        TRANSFER,
        LIST_ACCOUNTS,
        GENERATE_REPORT,
        EXIT
    }
    
    public static class Customer implements Cloneable, Serializable {
        private static final long serialVersionUID = 1L;
        private String name;
        private String email;
        private String mobile;
        private final String customerId;
        private static int customerCounter = 101;
        private Address address;
        
        public static class Address implements Serializable {
            private static final long serialVersionUID = 1L;
            private String line;
            private String city;
            private String pincode;
            
            public Address(String line, String city, String pincode) {
                this.line = line;
                this.city = city;
                this.pincode = pincode;
            }
            
            public String getLine() { return line; }
            public String getCity() { return city; }
            public String getPincode() { return pincode; }
        }
        
        private static String generateCustomerId() {
            return "CUST" + customerCounter++;
        }
        
        public Customer(String name, String email, String mobile) {
            this.name = name;
            this.email = email;
            this.mobile = mobile;
            this.customerId = generateCustomerId();
        }
        
        public Customer(String name, String email, String mobile, Address address) {
            this.name = name;
            this.email = email;
            this.mobile = mobile;
            this.customerId = generateCustomerId();
            this.address = address;
        }
        
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getMobile() { return mobile; }
        public String getCustomerId() { return customerId; }
        public Address getAddress() { return address; }
        
        @Override
        public Customer clone() {
            try {
                Customer cloned = (Customer) super.clone();
                if (this.address != null) {
                    cloned.address = new Address(this.address.line, this.address.city, this.address.pincode);
                }
                return cloned;
            } catch (CloneNotSupportedException e) {
                return null;
            }
        }
    }
    
    public static abstract class Account implements Serializable, Comparable<Account> {
        private static final long serialVersionUID = 1L;
        protected ArrayList<String> transactionHistory = new ArrayList<String>();
        @model.annotation.Id
        private final String accountNumber;
        private String ownerName;
        @model.annotation.Positive
        private long balance;
        private boolean active;
        private static int counter = 1;
        
        private static String generateAccountNumber() {
            return String.format("AC%04d", counter++);
        }
        
        public Account(String ownerName, long openingBalance) {
            this.accountNumber = generateAccountNumber();
            this.ownerName = ownerName;
            this.balance = openingBalance;
            this.active = true;
        }
        
        public Account(String ownerName) {
            this(ownerName, 0);
        }
        
        public synchronized void deposit(long amount) {
            if (amount <= 0) {
                throw new IllegalArgumentException("Deposit amount must be greater than 0");
            }
            transactionHistory.add("DEPOSIT: " + amount);
            this.balance += amount;
        }
        
        public abstract double interestRate();
        public abstract boolean canWithdraw(long amount);
        
        public synchronized boolean withdraw(long amount) {
            transactionHistory.add("WITHDRAW_ATTEMPT: " + amount);
            if (amount <= 0 || !canWithdraw(amount)) {
                return false;
            }
            this.balance -= amount;
            transactionHistory.add("WITHDRAW_OK: " + amount);
            return true;
        }

        public String getAccountNumber() { return accountNumber; }
        public String getOwnerName() { return ownerName; }
        public long getBalance() { return balance; }
        public boolean isActive() { return active; }
        public ArrayList<String> getTransactionHistory() { return transactionHistory; }
        
        @Override
        public int compareTo(Account other) {
            if (this.accountNumber == null && other.accountNumber == null) return 0;
            if (this.accountNumber == null) return -1;
            if (other.accountNumber == null) return 1;
            return this.accountNumber.compareTo(other.accountNumber);
        }
        
        @Override
        public String toString() {
            return "Account{accountNumber='" + accountNumber + "', ownerName='" + ownerName + "', balance=" + balance + "}";
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Account)) return false;
            Account account = (Account) o;
            return accountNumber.equals(account.accountNumber);
        }
        
        @Override
        public int hashCode() {
            return accountNumber.hashCode();
        }
    }
    
    private static Account findOrThrow(service.Repository<Account> repo, String accNum) throws AccountNotFoundException {
        Account acc = repo.findById(accNum);
        if (acc == null) {
            throw new AccountNotFoundException("Account not found: " + accNum);
        }
        return acc;
    }

    public static void main(String[] args) 
    {
        Scanner scanner = new Scanner(System.in);
        BankInfo bankInfo = new BankInfo("MiniBank", "Main Branch");
        System.out.println(bankInfo);
        
        Account[] accounts = new Account[3];
        accounts[0] = new SavingsAccount("Alice", 1000, 500);
        accounts[1] = new CurrentAccount("Bob", 500, 1000);
        accounts[2] = new FixedDepositAccount("Charlie", 2000);
        
        accounts[0].deposit(500);
        accounts[1].deposit(200);
        accounts[2].deposit(100);
        
        System.out.println("\n=== Repository Demo ===");
        service.Repository<Account> repo = new service.Repository<Account>();
        repo.saveAll(accounts[0], accounts[1], accounts[2]);
        Account fetched = repo.findById(accounts[0].getAccountNumber());
        System.out.println("Fetched: " + fetched);
        Double[] nums = {1.5, 2.5, 3.5};
        System.out.println("Average: " + average(nums));
        
        boolean running = true;

        while (running) {
            System.out.println("\n1. Open Account\n2. Deposit\n3. Withdraw\n4. Transfer\n5. List Accounts\n6. Generate Report\n7. Exit");
            System.out.print("Choice: ");
            String input = scanner.nextLine();

            MenuOption option = switch (input) {
                case "1" -> MenuOption.OPEN_ACCOUNT;
                case "2" -> MenuOption.DEPOSIT;
                case "3" -> MenuOption.WITHDRAW;
                case "4" -> MenuOption.TRANSFER;
                case "5" -> MenuOption.LIST_ACCOUNTS;
                case "6" -> MenuOption.GENERATE_REPORT;
                case "7" -> MenuOption.EXIT;
                default -> null;
            };

            if (option == null) {
                System.out.println("Invalid option.");
                continue;
            }

            try {
                switch (option) {
                    case OPEN_ACCOUNT -> {
                        System.out.print("Enter name: ");
                        String name = scanner.nextLine().trim();
                        if (name.isEmpty()) {
                            throw new InvalidAmountException("Name cannot be empty.");
                        }
                        System.out.print("Enter Initial Deposit: ");
                        long bal = Long.parseLong(scanner.nextLine().trim());
                        if (bal < 0) {
                            throw new InvalidAmountException("Initial deposit cannot be negative.");
                        }

                        // 1000 is the minimum balance requirement
                        Account newAcc = new SavingsAccount(name, bal, 1000);
                        repo.save(newAcc);

                        service.TransactionLog.append(java.nio.file.Paths.get("transactions.log"), "DEPOSIT " + newAcc.getAccountNumber() + " " + bal);
                        System.out.println("Opened: " + newAcc.getAccountNumber());
                    }
                    case DEPOSIT -> {
                        System.out.print("Enter Account Number: ");
                        String accNum = scanner.nextLine().trim().toUpperCase();
                        Account acc = findOrThrow(repo, accNum);

                        System.out.print("Amount: ");
                        long amt = Long.parseLong(scanner.nextLine().trim());
                        if (amt <= 0) {
                            throw new InvalidAmountException("Amount must be greater than 0.");
                        }
                        acc.deposit(amt);

                        service.TransactionLog.append(java.nio.file.Paths.get("transactions.log"), "DEPOSIT " + accNum + " " + amt);
                        System.out.println("Deposited. New balance: " + acc.getBalance());
                    }
                    case WITHDRAW -> {
                        System.out.print("Enter Account Number: ");
                        String accNum = scanner.nextLine().trim().toUpperCase();
                        Account acc = findOrThrow(repo, accNum);

                        System.out.print("Amount: ");
                        long amt = Long.parseLong(scanner.nextLine().trim());
                        if (amt <= 0) {
                            throw new InvalidAmountException("Amount must be greater than 0.");
                        }
                        if (!acc.withdraw(amt)) {
                            throw new InsufficientFundsException(
                                "Withdrawal of " + amt + " not allowed for " + accNum
                                    + " (balance: " + acc.getBalance() + ")",
                                Math.max(0, amt - acc.getBalance()));
                        }

                        service.TransactionLog.append(java.nio.file.Paths.get("transactions.log"), "WITHDRAW " + accNum + " " + amt);
                        System.out.println("Withdrawn. New balance: " + acc.getBalance());
                    }
                    case TRANSFER -> {
                        System.out.print("From Account Number: ");
                        String fromNum = scanner.nextLine().trim().toUpperCase();
                        Account from = findOrThrow(repo, fromNum);
                        System.out.print("To Account Number: ");
                        String toNum = scanner.nextLine().trim().toUpperCase();
                        Account to = findOrThrow(repo, toNum);

                        System.out.print("Amount: ");
                        long amt = Long.parseLong(scanner.nextLine().trim());

                        DeadlockSafeTransfer.transfer(from, to, amt);

                        service.TransactionLog.append(java.nio.file.Paths.get("transactions.log"), "WITHDRAW " + fromNum + " " + amt);
                        service.TransactionLog.append(java.nio.file.Paths.get("transactions.log"), "DEPOSIT " + toNum + " " + amt);
                        System.out.println("Transferred " + amt + " from " + fromNum + " to " + toNum);
                    }
                    case LIST_ACCOUNTS -> {
                        ArrayList<Account> list = repo.findAll();
                        list.sort(new java.util.Comparator<Account>() {
                            public int compare(Account a1, Account a2) {
                                return Long.compare(a2.getBalance(), a1.getBalance());
                            }
                        });
                        for (Account a : list) {
                            System.out.println(a);
                        }
                    }
                    case GENERATE_REPORT -> {
                        service.ReportGenerator.generateReport(
                            java.nio.file.Paths.get("."), java.nio.file.Paths.get("report.txt"));
                        System.out.println("--- End of Day Audit Report written to report.txt ---");
                    }
                    case EXIT -> {
                        System.out.println("Testing Bounded Generic: Average of [10.5, 20.0] is " + average(new Double[]{10.5, 20.0}));

                        ArrayList<Account> allAccounts = repo.findAll();
                        service.StatePersister.save(allAccounts.toArray(new Account[0]), java.nio.file.Paths.get("accounts.dat"));

                        System.out.println("Data saved to disk. System shutting down.");
                        running = false;
                    }
                }
            } catch (BankException e) {
                System.out.println("Transaction Error: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Input Error: Please enter a valid number.");
            } catch (Exception e) {
                System.out.println("System Error: " + e.getMessage());
            }
        }

        scanner.close();
    }
}
