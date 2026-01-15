package com.example.tinyledger.ledger.domain;

import java.time.Instant;
import java.util.UUID;

public record Transaction(UUID id, UUID ledgerId, TransactionType type, Money amount, Instant occurredAt) {}
