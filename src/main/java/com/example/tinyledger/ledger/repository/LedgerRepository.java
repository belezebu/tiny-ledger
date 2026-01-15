package com.example.tinyledger.ledger.repository;

import com.example.tinyledger.ledger.domain.Ledger;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LedgerRepository {
    Ledger save(Ledger ledger);

    Optional<Ledger> get(UUID id);

    List<Ledger> getLedgers();

    List<Ledger> getLedgersByUserId(UUID uuid);
}
