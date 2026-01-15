package com.example.tinyledger.ledger.controller.response;

import java.util.UUID;

public record BalanceResponse(UUID ledgerId, long balance) {
    public static BalanceResponse from(com.example.tinyledger.ledger.domain.Balance currentBalance) {
        return new BalanceResponse(
                currentBalance.ledgerId(), currentBalance.balance().amount());
    }
}
