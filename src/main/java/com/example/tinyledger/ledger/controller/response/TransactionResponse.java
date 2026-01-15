package com.example.tinyledger.ledger.controller.response;

import com.example.tinyledger.ledger.domain.TransactionType;
import java.time.Instant;
import java.util.UUID;

public record TransactionResponse(UUID id, UUID ledgerId, TransactionType type, Long amount, Instant occurredAt) {
    public static TransactionResponse from(com.example.tinyledger.ledger.domain.Transaction transaction) {
        return new TransactionResponse(
                transaction.id(),
                transaction.ledgerId(),
                transaction.type(),
                transaction.amount().amount(),
                transaction.occurredAt());
    }
}
