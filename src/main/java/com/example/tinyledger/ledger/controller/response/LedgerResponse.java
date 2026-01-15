package com.example.tinyledger.ledger.controller.response;

import java.util.UUID;

public record LedgerResponse(UUID id, String name) {
    public static LedgerResponse from(com.example.tinyledger.ledger.domain.Ledger ledger) {
        return new LedgerResponse(ledger.getId(), ledger.getName());
    }
}
