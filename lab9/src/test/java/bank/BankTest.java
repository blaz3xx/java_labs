package bank;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BankTest {

    @Test
    void transferMovesMoney() {
        Bank bank = new Bank();
        Account a = bank.openAccount(100);
        Account b = bank.openAccount(50);

        assertTrue(bank.transfer(a, b, 30));

        assertEquals(70, a.getBalance());
        assertEquals(80, b.getBalance());
    }

    @Test
    void notEnoughMoneyChangesNothing() {
        Bank bank = new Bank();
        Account a = bank.openAccount(20);
        Account b = bank.openAccount(0);

        assertFalse(bank.transfer(a, b, 21));

        assertEquals(20, a.getBalance());
        assertEquals(0, b.getBalance());
    }

    @Test
    void transferToTheSameAccountIsIgnored() {
        Bank bank = new Bank();
        Account a = bank.openAccount(100);

        assertFalse(bank.transfer(a, a, 10));

        assertEquals(100, a.getBalance());
    }

    @Test
    void accountRejectsWrongAmounts() {
        Account a = new Account(0, 10);

        assertThrows(IllegalArgumentException.class, () -> a.deposit(0));
        assertThrows(IllegalArgumentException.class, () -> a.withdraw(-5));
        assertThrows(IllegalStateException.class, () -> a.withdraw(11));
        assertThrows(IllegalArgumentException.class, () -> new Account(1, -1));
    }

    @Test
    @Timeout(30)
    void oppositeTransfersDoNotDeadlock() throws InterruptedException {
        Bank bank = new Bank();
        Account a = bank.openAccount(1_000_000);
        Account b = bank.openAccount(1_000_000);

        Thread aToB = new Thread(() -> {
            for (int i = 0; i < 50_000; i++) {
                bank.transfer(a, b, 1);
            }
        });
        Thread bToA = new Thread(() -> {
            for (int i = 0; i < 50_000; i++) {
                bank.transfer(b, a, 1);
            }
        });
        aToB.start();
        bToA.start();
        aToB.join();
        bToA.join();

        assertEquals(2_000_000, bank.getTotalMoney());
    }

    @Test
    @Timeout(60)
    void manyThreadsKeepTheTotalMoney() throws InterruptedException {
        BankApp.Report report = BankApp.runTest(100, 2000);

        assertEquals(report.moneyBefore(), report.moneyAfter());
        assertTrue(report.smallestBalance() >= 0);
        assertEquals(2000, report.completed() + report.rejected());
    }
}
