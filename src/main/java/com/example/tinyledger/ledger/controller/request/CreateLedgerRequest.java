package com.example.tinyledger.ledger.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class CreateLedgerRequest {
    @NotBlank(message = "Ledger name is required") String name;

    @NotNull UUID userId;

    public CreateLedgerRequest(String name, UUID userId) {
        this.name = name;
        this.userId = userId;
    }

    public CreateLedgerRequest() {}

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
