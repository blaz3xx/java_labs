package bank;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class BankApp {

    public static final int ACCOUNTS = 200;
    public static final int THREADS = 5000;
    public static final int MAX_START_BALANCE = 1000;
    public static final int MAX_TRANSFER = 500;

    public record Report(long moneyBefore, long moneyAfter, int completed, int rejected, int smallestBalance) {
    }

    public static Report runTest(int accountCount, int threadCount) throws InterruptedException {
        Bank bank = new Bank();
        Random random = new Random();
        for (int i = 0; i < accountCount; i++) {
            bank.openAccount(random.nextInt(MAX_START_BALANCE + 1));
        }
        List<Account> accounts = bank.getAccounts();
        long moneyBefore = bank.getTotalMoney();

        AtomicInteger completed = new AtomicInteger();
        AtomicInteger rejected = new AtomicInteger();
        CountDownLatch startSignal = new CountDownLatch(1);

        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                ThreadLocalRandom local = ThreadLocalRandom.current();
                Account from = accounts.get(local.nextInt(accounts.size()));
                Account to = accounts.get(local.nextInt(accounts.size()));
                int amount = local.nextInt(MAX_TRANSFER) + 1;
                try {
                    startSignal.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                if (bank.transfer(from, to, amount)) {
                    completed.incrementAndGet();
                } else {
                    rejected.incrementAndGet();
                }
            });
            threads[i].start();
        }

        startSignal.countDown();
        for (Thread thread : threads) {
            thread.join();
        }

        int smallest = accounts.stream().mapToInt(Account::getBalance).min().orElse(0);
        return new Report(moneyBefore, bank.getTotalMoney(), completed.get(), rejected.get(), smallest);
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Accounts: " + ACCOUNTS + ", threads: " + THREADS);
        Report report = runTest(ACCOUNTS, THREADS);

        System.out.println("Money in the bank before: " + report.moneyBefore());
        System.out.println("Money in the bank after:  " + report.moneyAfter());
        System.out.println("Transfers done: " + report.completed() + ", rejected (not enough money or same account): "
                + report.rejected());
        System.out.println("Smallest balance: " + report.smallestBalance());
        System.out.println(report.moneyBefore() == report.moneyAfter() && report.smallestBalance() >= 0
                ? "OK: the total is the same and no balance is negative."
                : "ERROR: money was lost or a balance is negative!");
    }
}
