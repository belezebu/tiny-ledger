package com.example.tinyledger.ledger.controller;

import com.example.tinyledger.common.controller.response.CreateEntityResponse;
import com.example.tinyledger.ledger.controller.request.CreateLedgerRequest;
import com.example.tinyledger.ledger.controller.request.CreateTransactionRequest;
import com.example.tinyledger.ledger.controller.response.BalanceResponse;
import com.example.tinyledger.ledger.controller.response.LedgerResponse;
import com.example.tinyledger.ledger.controller.response.TransactionResponse;
import com.example.tinyledger.ledger.service.LedgerService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ledgers")
public class LedgerController {

    private final LedgerService ledgerService;

    public LedgerController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @GetMapping
    public List<LedgerResponse> getLedgers(@RequestParam(required = false) UUID userId) {
        return this.ledgerService.getLedgers(userId).stream()
                .map(LedgerResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public LedgerResponse getLedger(@PathVariable("id") UUID ledgerId) {
        var ledger = this.ledgerService.getLedger(ledgerId);
        return LedgerResponse.from(ledger);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateEntityResponse createLedger(@Valid @RequestBody CreateLedgerRequest createLedgerRequest) {
        var ledger = this.ledgerService.createLedger(createLedgerRequest.getName(), createLedgerRequest.getUserId());
        return CreateEntityResponse.from(ledger);
    }

    @PostMapping("/{id}/transactions")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateEntityResponse createTransaction(
            @PathVariable("id") UUID ledgerId, @Valid @RequestBody CreateTransactionRequest transaction) {
        var transactionCreated = this.ledgerService.createTransaction(ledgerId, transaction.getAmount(), transaction.getTransactionType());
        return new CreateEntityResponse(transactionCreated.id());
    }

    @GetMapping("/{id}/transactions")
    public List<TransactionResponse> getTransactions(@PathVariable("id") UUID ledgerId) {
        return this.ledgerService.getTransactions(ledgerId).stream()
                .map(TransactionResponse::from)
                .toList();
    }

    @GetMapping("/{id}/balance")
    public BalanceResponse getBalance(@PathVariable("id") UUID ledgerId) {
        var currentBalance = this.ledgerService.getBalance(ledgerId);
        return BalanceResponse.from(currentBalance);
    }
}
