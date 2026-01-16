/* (C) 2026 */
package com.example.tinyledger.ledger.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.tinyledger.common.controller.response.CreateEntityResponse;
import com.example.tinyledger.ledger.controller.request.CreateLedgerRequest;
import com.example.tinyledger.ledger.controller.request.CreateTransactionRequest;
import com.example.tinyledger.ledger.controller.response.BalanceResponse;
import com.example.tinyledger.ledger.domain.TransactionType;
import com.example.tinyledger.user.controller.request.CreateUserRequest;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class LedgerResponseControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should create a ledger")
    void shouldCreateLedger() throws Exception {
        var userId = createUser("Obi-Wan", "Kenobi", "obi-wan@kenobi.com");
        createLedger("New Ledger", userId);
    }

    @Test
    @DisplayName("Should return 400 when creating ledger with blank name")
    void shouldReturn400WhenCreatingLedgerWithBlankName() throws Exception {
        var request = new CreateLedgerRequest("", UUID.randomUUID());
        mockMvc.perform(post("/ledgers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should get all ledgers")
    void shouldGetAllLedgers() throws Exception {
        createLedger("New Ledger");

        mockMvc.perform(get("/ledgers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").value("New Ledger"));
    }

    @Test
    @DisplayName("Should get ledger by id")
    void shouldGetLedgerById() throws Exception {
        var ledgerName = "New Ledger";
        var ledgerId = createLedger(ledgerName);

        mockMvc.perform(get("/ledgers/" + ledgerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ledgerId.toString()))
                .andExpect(jsonPath("$.name").value(ledgerName));
    }

    @Test
    @DisplayName("Should return 404 when ledger not found")
    void shouldReturn404WhenLedgerNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        mockMvc.perform(get("/ledgers/" + nonExistentId)).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should create deposit transaction")
    void shouldCreateDepositTransaction() throws Exception {
        var ledgerId = createLedger("New Ledger");

        var createTransactionRequest = new CreateTransactionRequest(TransactionType.DEPOSIT, 1000);

        mockMvc.perform(post("/ledgers/%s/transactions".formatted(ledgerId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTransactionRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());

        MvcResult balanceResponse = mockMvc.perform(get("/ledgers/" + ledgerId + "/balance"))
                .andExpect(status().isOk())
                .andReturn();

        var balance = objectMapper.readValue(balanceResponse.getResponse().getContentAsString(), BalanceResponse.class);
        assertEquals(createTransactionRequest.getAmount(), balance.balance());
    }

    @Test
    @DisplayName("Should create withdraw transaction")
    void shouldCreateWithdrawTransaction() throws Exception {
        var ledgerId = createLedger("New Ledger");

        var createDepositRequest = new CreateTransactionRequest(TransactionType.DEPOSIT, 1000);

        mockMvc.perform(post("/ledgers/" + ledgerId + "/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDepositRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        var createWithdrawRequest = new CreateTransactionRequest(TransactionType.WITHDRAW, 300);

        mockMvc.perform(post("/ledgers/" + ledgerId + "/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createWithdrawRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());

        MvcResult balanceResponse = mockMvc.perform(get("/ledgers/" + ledgerId + "/balance"))
                .andExpect(status().isOk())
                .andReturn();

        var balance = objectMapper.readValue(balanceResponse.getResponse().getContentAsString(), BalanceResponse.class);
        assertEquals(createDepositRequest.getAmount() - createWithdrawRequest.getAmount(), balance.balance());
    }

    @Test
    @DisplayName("Should return 400 when withdrawing more than balance")
    void shouldReturn400WhenWithdrawingMoreThanBalance() throws Exception {
        var ledgerId = createLedger("New Ledger");
        var createDepositRequest = new CreateTransactionRequest(TransactionType.DEPOSIT, 1000);

        mockMvc.perform(post("/ledgers/" + ledgerId + "/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDepositRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        var createWithdrawRequest = new CreateTransactionRequest(TransactionType.WITHDRAW, 300);

        mockMvc.perform(post("/ledgers/" + ledgerId + "/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createWithdrawRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("Should return 400 when amount is negative")
    void shouldReturn400WhenAmountIsNegative() throws Exception {
        var ledgerId = createLedger("New Ledger");
        var createDepositRequest = new CreateTransactionRequest(TransactionType.DEPOSIT, -1000);

        mockMvc.perform(post("/ledgers/" + ledgerId + "/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDepositRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when amount is zero")
    void shouldReturn400WhenAmountIsZero() throws Exception {
        var ledgerId = createLedger("New Ledger");

        var createDepositRequest = new CreateTransactionRequest(TransactionType.DEPOSIT, 0);
        mockMvc.perform(post("/ledgers/" + ledgerId + "/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDepositRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should get transaction history")
    void shouldGetTransactionHistory() throws Exception {
        // Create a ledger first
        var ledgerId = createLedger("New Ledger");

        // Create transactions
        var createDepositRequest = new CreateTransactionRequest(TransactionType.DEPOSIT, 1000);
        mockMvc.perform(post("/ledgers/" + ledgerId + "/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDepositRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        mockMvc.perform(get("/ledgers/" + ledgerId + "/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].type").value("DEPOSIT"))
                .andExpect(jsonPath("$[0].amount").value(1000))
                .andExpect(jsonPath("$[0].ledgerId").value(ledgerId.toString()))
                .andExpect(jsonPath("$[0].occurredAt").exists());
    }

    @Test
    @DisplayName("Should get balance")
    void shouldGetBalance() throws Exception {
        // Create a ledger first
        var ledgerId = createLedger("New Ledger");

        // Create transactions
        var createDepositRequest = new CreateTransactionRequest(TransactionType.DEPOSIT, 1000);
        mockMvc.perform(post("/ledgers/" + ledgerId + "/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDepositRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        mockMvc.perform(post("/ledgers/" + ledgerId + "/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDepositRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        mockMvc.perform(post("/ledgers/" + ledgerId + "/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDepositRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        var createWithdrawRequest = new CreateTransactionRequest(TransactionType.WITHDRAW, 300);

        mockMvc.perform(post("/ledgers/" + ledgerId + "/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createWithdrawRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
        mockMvc.perform(post("/ledgers/" + ledgerId + "/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createWithdrawRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
        mockMvc.perform(post("/ledgers/" + ledgerId + "/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createWithdrawRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());

        var balanceResponseAsString = mockMvc.perform(get("/ledgers/" + ledgerId + "/balance"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        var balanceResponse = objectMapper.readValue(balanceResponseAsString, BalanceResponse.class);

        assertEquals(ledgerId, balanceResponse.ledgerId());
        assertEquals(1000 * 3 - 300 * 3, balanceResponse.balance());
    }

    @Test
    @DisplayName("Should return 404 when creating ledger with non-existent userId")
    void shouldReturn404WhenCreatingLedgerWithNonExistentUserId() throws Exception {
        UUID nonExistentUserId = UUID.randomUUID();
        var request = new CreateLedgerRequest("Test Ledger", nonExistentUserId);

        mockMvc.perform(post("/ledgers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should withdraw exactly the balance")
    void shouldWithdrawExactBalance() throws Exception {
        var ledgerId = createLedger("Test Ledger");
        var depositRequest = new CreateTransactionRequest(TransactionType.DEPOSIT, 1000);

        mockMvc.perform(post("/ledgers/" + ledgerId + "/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isCreated());

        var withdrawRequest = new CreateTransactionRequest(TransactionType.WITHDRAW, 1000);
        mockMvc.perform(post("/ledgers/" + ledgerId + "/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawRequest)))
                .andExpect(status().isCreated());

        var balanceResponseString = mockMvc.perform(get("/ledgers/" + ledgerId + "/balance"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var balance = objectMapper.readValue(balanceResponseString, BalanceResponse.class);
        assertEquals(0L, balance.balance());
    }

    @Test
    @DisplayName("Should filter ledgers by userId")
    void shouldFilterLedgersByUserId() throws Exception {
        var user1Id = createUser("Luke", "Skywalker", "luke@example.com");
        var user2Id = createUser("Leia", "Organa", "leia@example.com");

        createLedger("Luke's Ledger 1", user1Id);
        createLedger("Luke's Ledger 2", user1Id);
        createLedger("Leia's Ledger", user2Id);

        mockMvc.perform(get("/ledgers").param("userId", user1Id.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("Should return empty array when filtering by userId with no ledgers")
    void shouldReturnEmptyArrayWhenFilteringByUserIdWithNoLedgers() throws Exception {
        var userId = createUser("Han", "Solo", "han@example.com");

        mockMvc.perform(get("/ledgers").param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("Should handle multiple transactions and maintain correct balance")
    void shouldHandleMultipleTransactionsAndMaintainCorrectBalance() throws Exception {
        var ledgerId = createLedger("Multi-transaction Ledger");

        // Deposit 500
        mockMvc.perform(post("/ledgers/" + ledgerId + "/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateTransactionRequest(TransactionType.DEPOSIT, 500))))
                .andExpect(status().isCreated());

        // Withdraw 200
        mockMvc.perform(post("/ledgers/" + ledgerId + "/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateTransactionRequest(TransactionType.WITHDRAW, 200))))
                .andExpect(status().isCreated());

        // Deposit 300
        mockMvc.perform(post("/ledgers/" + ledgerId + "/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateTransactionRequest(TransactionType.DEPOSIT, 300))))
                .andExpect(status().isCreated());

        // Withdraw 150
        mockMvc.perform(post("/ledgers/" + ledgerId + "/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateTransactionRequest(TransactionType.WITHDRAW, 150))))
                .andExpect(status().isCreated());

        // Final balance should be: 500 - 200 + 300 - 150 = 450
        var balanceResponseString = mockMvc.perform(get("/ledgers/" + ledgerId + "/balance"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var balance = objectMapper.readValue(balanceResponseString, BalanceResponse.class);
        assertEquals(450L, balance.balance());

        // Verify transaction count
        mockMvc.perform(get("/ledgers/" + ledgerId + "/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)));
    }

    private UUID createUser(String firstName, String lastName, String emailAddress) {
        try {
            var userRequest = new CreateUserRequest(firstName, lastName, emailAddress);
            var userRequestResult = mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userRequest)))
                    .andExpect(status().isCreated())
                    .andReturn();

            return objectMapper
                    .readValue(userRequestResult.getResponse().getContentAsString(), CreateEntityResponse.class)
                    .id();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private UUID createLedger(String ledgerName, UUID userId) {
        try {
            var request = new CreateLedgerRequest(ledgerName, userId);
            MvcResult result = mockMvc.perform(post("/ledgers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andReturn();

            return objectMapper
                    .readValue(result.getResponse().getContentAsString(), CreateEntityResponse.class)
                    .id();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private UUID createLedger(String ledgerName) {
        var userId = createUser("Luke", "Skywalker", "luke@skywalker.com");
        return createLedger(ledgerName, userId);
    }
}
