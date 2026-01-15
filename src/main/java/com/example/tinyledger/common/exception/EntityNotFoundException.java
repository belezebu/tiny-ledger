package com.example.tinyledger.common.exception;

import java.util.UUID;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }

    public static EntityNotFoundException ledgerNotFound(UUID id) {
        return new EntityNotFoundException("Ledger not found with id: " + id);
    }

    public static EntityNotFoundException userNotFound(UUID id) {
        return new EntityNotFoundException("User not found with id: " + id);
    }
}
