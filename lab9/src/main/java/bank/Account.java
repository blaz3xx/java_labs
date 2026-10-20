package bank;

public class Account {

    private final int id;
    private int balance;

    public Account(int id, int balance) {
        if (balance < 0) {
            throw new IllegalArgumentException("The balance cannot be negative");
        }
        this.id = id;
        this.balance = balance;
    }

    public int getId() {
        return id;
    }

    public synchronized int getBalance() {
        return balance;
    }

    public synchronized void deposit(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("The amount must be positive");
        }
        balance += amount;
    }

    public synchronized void withdraw(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("The amount must be positive");
        }
        if (amount > balance) {
            throw new IllegalStateException("Not enough money on account " + id);
        }
        balance -= amount;
    }

    @Override
    public String toString() {
        return "Account " + id + ": " + getBalance();
    }
}
