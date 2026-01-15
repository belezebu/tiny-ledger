package com.example.tinyledger.ledger.controller.request;

import com.example.tinyledger.ledger.domain.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CreateTransactionRequest {

    @NotNull TransactionType transactionType;

    @Positive @NotNull long amount;

    public CreateTransactionRequest() {}

    public CreateTransactionRequest(TransactionType transactionType, long amount) {
        this.transactionType = transactionType;
        this.amount = amount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public long getAmount() {
        return amount;
    }
}
