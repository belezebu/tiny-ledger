package com.example.tinyledger.ledger.service;

import com.example.tinyledger.common.exception.EntityNotFoundException;
import com.example.tinyledger.ledger.controller.request.CreateLedgerRequest;
import com.example.tinyledger.ledger.controller.request.CreateTransactionRequest;
import com.example.tinyledger.ledger.domain.Balance;
import com.example.tinyledger.ledger.domain.Ledger;
import com.example.tinyledger.ledger.domain.Money;
import com.example.tinyledger.ledger.domain.Transaction;
import com.example.tinyledger.ledger.repository.LedgerRepository;
import com.example.tinyledger.user.service.UserService;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class LedgerService {

    private final LedgerRepository ledgerRepository;
    private final UserService userService;

    public LedgerService(LedgerRepository ledgerRepository, UserService userService) {
        this.ledgerRepository = ledgerRepository;
        this.userService = userService;
    }

    public List<Ledger> getLedgers(@Nullable UUID userId) {
        return Optional.ofNullable(userId)
                .map(this.ledgerRepository::getLedgersByUserId)
                .orElseGet(this.ledgerRepository::getLedgers);
    }

    public Ledger getLedger(UUID id) {
        return this.ledgerRepository.get(id).orElseThrow(() -> EntityNotFoundException.ledgerNotFound(id));
    }

    public Transaction createTransaction(UUID ledgerId, CreateTransactionRequest transaction) {
        var ledger = getLedger(ledgerId);
        var amount = new Money(transaction.getAmount());
        return switch (transaction.getTransactionType()) {
                    case DEPOSIT -> ledger.deposit(amount);
                    case WITHDRAW -> ledger.withdraw(amount);
                };
    }

    public List<Transaction> getTransactions(UUID ledgerId) {
        return getLedger(ledgerId).getTransactions();
    }

    public Ledger createLedger(CreateLedgerRequest createLedgerRequest) {
        var user = this.userService.getUser(createLedgerRequest.getUserId());
        var ledger = new Ledger(createLedgerRequest.getName(), user.id());
        return this.ledgerRepository.save(ledger);
    }

    public Balance getBalance(UUID ledgerId) {
        var ledger = this.getLedger(ledgerId);
        return new Balance(ledgerId, ledger.getBalance());
    }
}
