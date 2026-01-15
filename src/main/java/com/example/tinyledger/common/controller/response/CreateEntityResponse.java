package com.example.tinyledger.common.controller.response;

import com.example.tinyledger.ledger.domain.Ledger;
import java.util.UUID;

public record CreateEntityResponse(UUID id) {
    public static CreateEntityResponse from(Ledger ledger) {
        return new CreateEntityResponse(ledger.getId());
    }
}
