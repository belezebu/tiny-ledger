package com.example.tinyledger.ledger.domain;

import java.util.UUID;

public record Balance(UUID ledgerId, Money balance) {}
