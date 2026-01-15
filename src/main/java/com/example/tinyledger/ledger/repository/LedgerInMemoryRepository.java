package com.example.tinyledger.ledger.repository;

import com.example.tinyledger.common.exception.EntityAlreadyExistsException;
import com.example.tinyledger.ledger.domain.Ledger;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class LedgerInMemoryRepository implements LedgerRepository {
    private final Map<UUID, Ledger> ledgers;

    public LedgerInMemoryRepository() {
        this.ledgers = new ConcurrentHashMap<>();
    }

    public synchronized Ledger save(Ledger ledger) {
        if (ledgers.containsKey(ledger.getId())) {
            throw EntityAlreadyExistsException.ledgerAlreadyExists(ledger.getId());
        }
        ledgers.put(ledger.getId(), ledger);
        return ledger;
    }

    public Optional<Ledger> get(UUID id) {
        return Optional.ofNullable(ledgers.get(id));
    }

    public List<Ledger> getLedgers() {
        return ledgers.values().stream().toList();
    }

    @Override
    public List<Ledger> getLedgersByUserId(UUID uuid) {
        return this.getLedgers().stream()
                .filter(ledger -> ledger.getUserId().equals(uuid))
                .toList();
    }
}
