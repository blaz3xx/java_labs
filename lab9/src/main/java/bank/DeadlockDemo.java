package bank;

import java.lang.management.ManagementFactory;

public class DeadlockDemo {

    private static void naiveTransfer(Account from, Account to, int amount) {
        synchronized (from) {
            pause(200);
            synchronized (to) {
                from.withdraw(amount);
                to.deposit(amount);
            }
        }
    }

    private static void pause(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Account a = new Account(1, 100);
        Account b = new Account(2, 100);

        Thread aToB = new Thread(() -> naiveTransfer(a, b, 10), "A-to-B");
        Thread bToA = new Thread(() -> naiveTransfer(b, a, 10), "B-to-A");
        aToB.setDaemon(true);
        bToA.setDaemon(true);
        aToB.start();
        bToA.start();

        Thread.sleep(1000);
        long[] stuck = ManagementFactory.getThreadMXBean().findDeadlockedThreads();
        System.out.println(stuck == null
                ? "No deadlock this time."
                : "DEADLOCK: " + stuck.length + " threads wait for each other forever.");
    }
}
