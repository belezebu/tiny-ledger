package com.example.tinyledger.ledger.domain;

import static org.junit.jupiter.api.Assertions.*;

import com.example.tinyledger.common.exception.InvalidMoneyOperationException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LedgerTest {

    @Test
    @DisplayName("Should create ledger with zero initial balance")
    void shouldCreateLedgerWithZeroInitialBalance() {
        UUID userId = UUID.randomUUID();
        Ledger ledger = new Ledger("Test Ledger", userId);

        assertEquals("Test Ledger", ledger.getName());
        assertEquals(userId, ledger.getUserId());
        assertEquals(0L, ledger.getBalance().amount());
        assertTrue(ledger.getTransactions().isEmpty());
    }

    @Test
    @DisplayName("Should deposit money and increase balance")
    void shouldDepositMoneyAndIncreaseBalance() {
        Ledger ledger = new Ledger("Test Ledger", UUID.randomUUID());

        Transaction transaction = ledger.deposit(new Money(100L));

        assertEquals(100L, ledger.getBalance().amount());
        assertNotNull(transaction);
        assertEquals(TransactionType.DEPOSIT, transaction.type());
        assertEquals(100L, transaction.amount().amount());
    }

    @Test
    @DisplayName("Should deposit multiple times and accumulate balance")
    void shouldDepositMultipleTimesAndAccumulateBalance() {
        Ledger ledger = new Ledger("Test Ledger", UUID.randomUUID());

        ledger.deposit(new Money(100L));
        ledger.deposit(new Money(50L));
        ledger.deposit(new Money(25L));

        assertEquals(175L, ledger.getBalance().amount());
    }

    @Test
    @DisplayName("Should withdraw money and decrease balance")
    void shouldWithdrawMoneyAndDecreaseBalance() {
        Ledger ledger = new Ledger("Test Ledger", UUID.randomUUID());
        ledger.deposit(new Money(100L));

        Transaction transaction = ledger.withdraw(new Money(50L));

        assertEquals(50L, ledger.getBalance().amount());
        assertNotNull(transaction);
        assertEquals(TransactionType.WITHDRAW, transaction.type());
        assertEquals(50L, transaction.amount().amount());
    }

    @Test
    @DisplayName("Should withdraw exact balance to reach zero")
    void shouldWithdrawExactBalanceToReachZero() {
        Ledger ledger = new Ledger("Test Ledger", UUID.randomUUID());
        ledger.deposit(new Money(100L));

        ledger.withdraw(new Money(100L));

        assertEquals(0L, ledger.getBalance().amount());
    }

    @Test
    @DisplayName("Should throw exception when withdrawing more than balance")
    void shouldThrowExceptionWhenWithdrawingMoreThanBalance() {
        Ledger ledger = new Ledger("Test Ledger", UUID.randomUUID());
        ledger.deposit(new Money(50L));

        assertThrows(InvalidMoneyOperationException.class, () -> ledger.withdraw(new Money(100L)));
    }

    @Test
    @DisplayName("Should throw exception when withdrawing from zero balance")
    void shouldThrowExceptionWhenWithdrawingFromZeroBalance() {
        Ledger ledger = new Ledger("Test Ledger", UUID.randomUUID());

        assertThrows(InvalidMoneyOperationException.class, () -> ledger.withdraw(new Money(1L)));
    }

    @Test
    @DisplayName("Should record transaction history")
    void shouldRecordTransactionHistory() {
        Ledger ledger = new Ledger("Test Ledger", UUID.randomUUID());

        ledger.deposit(new Money(100L));
        ledger.withdraw(new Money(30L));
        ledger.deposit(new Money(50L));

        List<Transaction> transactions = ledger.getTransactions();
        assertEquals(3, transactions.size());
        assertEquals(TransactionType.DEPOSIT, transactions.get(0).type());
        assertEquals(TransactionType.WITHDRAW, transactions.get(1).type());
        assertEquals(TransactionType.DEPOSIT, transactions.get(2).type());
    }

    @Test
    @DisplayName("Should handle multiple deposits and withdrawals correctly")
    void shouldHandleMultipleDepositsAndWithdrawalsCorrectly() {
        Ledger ledger = new Ledger("Test Ledger", UUID.randomUUID());

        ledger.deposit(new Money(1000L)); // Balance: 1000
        ledger.withdraw(new Money(300L)); // Balance: 700
        ledger.deposit(new Money(500L)); // Balance: 1200
        ledger.withdraw(new Money(200L)); // Balance: 1000

        assertEquals(1000L, ledger.getBalance().amount());
        assertEquals(4, ledger.getTransactions().size());
    }

    @Test
    @DisplayName("Should link transactions to correct ledger")
    void shouldLinkTransactionsToCorrectLedger() {
        Ledger ledger = new Ledger("Test Ledger", UUID.randomUUID());

        Transaction transaction = ledger.deposit(new Money(100L));

        assertEquals(ledger.getId(), transaction.ledgerId());
    }

    @Test
    @DisplayName("Should generate unique transaction IDs")
    void shouldGenerateUniqueTransactionIds() {
        Ledger ledger = new Ledger("Test Ledger", UUID.randomUUID());

        Transaction t1 = ledger.deposit(new Money(100L));
        Transaction t2 = ledger.deposit(new Money(200L));

        assertNotEquals(t1.id(), t2.id());
    }

    @Test
    @DisplayName("Should handle concurrent deposits correctly")
    void shouldHandleConcurrentDepositsCorrectly() throws InterruptedException {
        Ledger ledger = new Ledger("Test Ledger", UUID.randomUUID());
        int threadCount = 10;
        int depositsPerThread = 100;
        long depositAmount = 10L;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < depositsPerThread; j++) {
                        ledger.deposit(new Money(depositAmount));
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean completed = latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        assertTrue(completed, "All threads should complete within timeout");

        long expectedBalance = threadCount * depositsPerThread * depositAmount;
        assertEquals(expectedBalance, ledger.getBalance().amount());
        assertEquals(threadCount * depositsPerThread, ledger.getTransactions().size());
    }

    @Test
    @DisplayName("Should handle concurrent withdrawals correctly")
    void shouldHandleConcurrentWithdrawalsCorrectly() throws InterruptedException {
        Ledger ledger = new Ledger("Test Ledger", UUID.randomUUID());
        long initialDeposit = 10000L;
        ledger.deposit(new Money(initialDeposit));

        int threadCount = 10;
        int withdrawalsPerThread = 10;
        long withdrawAmount = 10L;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < withdrawalsPerThread; j++) {
                        ledger.withdraw(new Money(withdrawAmount));
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean completed = latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        assertTrue(completed, "All threads should complete within timeout");

        long totalWithdrawn = threadCount * withdrawalsPerThread * withdrawAmount;
        long expectedBalance = initialDeposit - totalWithdrawn;
        assertEquals(expectedBalance, ledger.getBalance().amount());
    }

    @Test
    @DisplayName("Should handle mixed concurrent deposits and withdrawals")
    void shouldHandleMixedConcurrentDepositsAndWithdrawals() throws InterruptedException {
        Ledger ledger = new Ledger("Test Ledger", UUID.randomUUID());
        ledger.deposit(new Money(5000L)); // Initial balance for withdrawals

        int threadCount = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // Half threads deposit, half withdraw
        for (int i = 0; i < threadCount / 2; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < 50; j++) {
                        ledger.deposit(new Money(10L));
                    }
                } finally {
                    latch.countDown();
                }
            });

            executor.submit(() -> {
                try {
                    for (int j = 0; j < 50; j++) {
                        try {
                            ledger.withdraw(new Money(5L));
                        } catch (InvalidMoneyOperationException e) {
                            // Expected when balance is insufficient
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean completed = latch.await(15, TimeUnit.SECONDS);
        executor.shutdown();

        assertTrue(completed, "All threads should complete within timeout");

        // Balance should be consistent (initial + deposits - successful withdrawals)
        assertTrue(ledger.getBalance().amount() >= 0L, "Balance should never be negative");
    }
}