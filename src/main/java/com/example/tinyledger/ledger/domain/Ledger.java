package com.example.tinyledger.ledger.domain;

import com.example.tinyledger.common.exception.InvalidMoneyOperationException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class Ledger {
    private final UUID id;
    private final UUID userId;
    private final String name;
    private Money balance;
    private final List<Transaction> transactions;

    private Ledger(UUID id, UUID userId, String name, Money money, List<Transaction> transactions) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.balance = money;
        this.transactions = transactions;
    }

    public Ledger(String name, UUID userId) {
        this(UUID.randomUUID(), userId, name, Money.zero(), new ArrayList<>());
    }

    public synchronized Transaction deposit(Money depositAmount) {
        this.balance = this.balance.add(depositAmount);
        var transaction =
                new Transaction(UUID.randomUUID(), this.getId(), TransactionType.DEPOSIT, depositAmount, Instant.now());
        this.transactions.add(transaction);
        return transaction;
    }

    public synchronized Transaction withdraw(Money withdrawAmount) {
        if (this.getBalance().isLessThan(withdrawAmount)) {
            throw InvalidMoneyOperationException.insufficientFunds(this.getBalance(), withdrawAmount);
        }
        this.balance = this.balance.subtract(withdrawAmount);
        var transaction = new Transaction(
                UUID.randomUUID(), this.getId(), TransactionType.WITHDRAW, withdrawAmount, Instant.now());
        this.transactions.add(transaction);
        return transaction;
    }

    public synchronized List<Transaction> getTransactions() {
        return transactions.stream()
                .sorted(Comparator.comparing(Transaction::occurredAt))
                .toList();
    }

    public synchronized Money getBalance() {
        return this.balance;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public UUID getUserId() {
        return userId;
    }
}
