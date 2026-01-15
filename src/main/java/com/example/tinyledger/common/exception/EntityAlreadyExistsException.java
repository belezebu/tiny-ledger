package com.example.tinyledger.common.exception;

import java.util.UUID;

public class EntityAlreadyExistsException extends RuntimeException {
    public EntityAlreadyExistsException(String message) {
        super(message);
    }

    public static EntityAlreadyExistsException ledgerAlreadyExists(UUID ledgerId) {
        return  new EntityAlreadyExistsException("Ledger already exists with id: #%s".formatted(ledgerId));
    }

    public static EntityAlreadyExistsException userAlreadyExists(UUID userId) {
        return  new EntityAlreadyExistsException("User already exists with id: #%s".formatted(userId));
    }
}
