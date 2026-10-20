package bank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bank {

    private final List<Account> accounts = new ArrayList<>();

    public Account openAccount(int balance) {
        Account account = new Account(accounts.size(), balance);
        accounts.add(account);
        return account;
    }

    public List<Account> getAccounts() {
        return Collections.unmodifiableList(accounts);
    }

    public boolean transfer(Account from, Account to, int amount) {
        if (from == to) {
            return false;
        }

        Account first = from.getId() < to.getId() ? from : to;
        Account second = first == from ? to : from;

        synchronized (first) {
            synchronized (second) {
                if (from.getBalance() < amount) {
                    return false;
                }
                from.withdraw(amount);
                to.deposit(amount);
                return true;
            }
        }
    }

    public long getTotalMoney() {
        long total = 0;
        for (Account account : accounts) {
            total += account.getBalance();
        }
        return total;
    }
}
