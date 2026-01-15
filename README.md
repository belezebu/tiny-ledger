# Tiny Ledger

A simple ledger application that allows you to record money movements (deposits and withdrawals), view current balance, and view transaction history.

## Prerequisites

- Java 21 or higher
- Gradle (included via Gradle Wrapper)

## Running the Application

### Using Gradle Wrapper

```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

## Testing the application

```bash
./gradlew test
```

## API Endpoints

There are two different resources in the project, users and ledgers. 
A user can have multiple ledgers and a ledger is required to be associated with a user.

The user endpoints are prefixed with `/users`
### 1. Create a User

Create a user with firstName, lastName and emailAddress.

**Request:**
```
POST /users
Content-Type: application/json
{
    "firstName": "Obi-Wan",
    "lastName": "Kenobi",
    "emailAddress": "obi-wan@kenobi.com"
}
```

**Response:** `201 Created`
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000"
}
```

The ledger endpoints are prefixed with `/ledgers`

### 1. Create a Ledger

Create a new ledger with a name.

**Request:**

```
POST /ledgers
Content-Type: application/json

{
  "name": "My Savings Account",
  "userId": "{userId}"
}
```

**Response:** `201 Created`
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000"
}
```

### 2. Get All Ledgers

Retrieve a list of all ledgers.

**Request:**
```
GET /ledgers
```

**Response:** `200 OK`
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "userId": "550e8400-e29b-41d4-a716-446655440001",
    "name": "My Savings Account"
  }
]
```

### 3. Get a Specific Ledger

Retrieve details of a specific ledger by ID.

**Request:**
```
GET /ledgers/{id}
```

**Response:** `200 OK`
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "userId": "550e8400-e29b-41d4-a716-446655440001",
  "name": "My Savings Account"
}
```

### 4. Create a Transaction

Record a deposit or withdrawal for a ledger.

**Request:**
```
POST /ledgers/{id}/transactions
Content-Type: application/json

{
  "transactionType": "DEPOSIT",
  "amount": 1000
}
```

**Response:** `201 Created`
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440001"
}
```

**Transaction Types:**
- `DEPOSIT` - Add money to the ledger
- `WITHDRAW` - Remove money from the ledger (must not exceed balance)

### 5. Get Transaction History

Retrieve all transactions for a specific ledger.

**Request:**
```
GET /ledgers/{id}/transactions
```

**Response:** `200 OK`
```json
[
  {
    "id": "660e8400-e29b-41d4-a716-446655440001",
    "ledgerId": "550e8400-e29b-41d4-a716-446655440000",
    "type": "DEPOSIT",
    "amount": 1000,
    "occurredAt": "2024-01-15T10:30:00Z"
  },
  {
    "id": "770e8400-e29b-41d4-a716-446655440002",
    "ledgerId": "550e8400-e29b-41d4-a716-446655440000",
    "type": "WITHDRAW",
    "amount": 250,
    "occurredAt": "2024-01-15T11:45:00Z"
  }
]
```

### 6. Get Current Balance

Retrieve the current balance of a ledger.

**Request:**
```
GET /ledgers/{id}/balance
```

**Response:** `200 OK`
```json
{
  "ledgerId": "550e8400-e29b-41d4-a716-446655440000",
  "balance": 750
}
```

## Example Usage

### Complete Workflow Example

```bash
# 1. Create a user
curl -X POST http://localhost:8080/users \
  -H 'Content-Type: application/json' \
  -d '{ "firstName": "Obi-Wan", "lastName": "Kenobi", "emailAddress": "obi-wan@kenobi.com"}'

# 2. Create a ledger
curl -X POST http://localhost:8080/ledgers \
  -H "Content-Type: application/json" \
  -d '{"name": "Vacation Fund", "userId": "{userId}"}'

# 3. Make a deposit (amount in cents: 5000 = $50.00)
curl -X POST http://localhost:8080/ledgers/{ledgerId}/transactions \
  -H "Content-Type: application/json" \
  -d '{"transactionType": "DEPOSIT", "amount": 5000}'

# 4. Make another deposit (amount in cents: 300 = $3.00)
curl -X POST http://localhost:8080/ledgers/{ledgerId}/transactions \
  -H "Content-Type: application/json" \
  -d '{"transactionType": "DEPOSIT", "amount": 300}'

# 5. Make a withdrawal (amount in cents: 200 = $2.00)
curl -X POST http://localhost:8080/ledgers/{ledgerId}/transactions \
  -H "Content-Type: application/json" \
  -d '{"transactionType": "WITHDRAW", "amount": 200}'

# 6. Check balance (balance in cents: 5100 = $51.00)
curl http://localhost:8080/ledgers/{ledgerId}/balance

# 7. View transaction history
curl http://localhost:8080/ledgers/{ledgerId}/transactions
```

## Error Responses

### 404 Not Found
When a ledger is not found:
```json
{
  "code": "ENTITY_NOT_FOUND",
  "message": "Ledger not found with id: 550e8400-e29b-41d4-a716-446655440000",
  "details": []
}
```

### 400 Bad Request
When validation fails or business rules are violated:
```json
{
  "code": "ENTITY_NOT_FOUND",
  "message": "User not found with id: 8c2a9600-6025-4e58-85dc-1f85bf993d7e",
  "details": []
}
```

Or for validation errors:
```json
{
  "code": "BAD_REQUEST",
  "message": "Request body is invalid",
  "details": [
    {
      "field": "name",
      "messages": [
        "Ledger name is required"
      ]
    }
  ]
}
```

## Assumptions

1. **Currency**: All amounts are received and returned in cents (the smallest currency unit). For example:
   - 1000 cents = $10.00
   - 500 cents = $5.00
   - 75 cents = $0.75
   
   Amounts are stored as integers. For simplicity, no currency symbol or decimal places are used in the API.

2. **Data Persistence**: Data is stored in-memory and will be lost when the application restarts. This is intentional for simplicity as per the requirements.

3. **Concurrency**: The application uses synchronized methods to handle concurrent access, but for production use, a proper database with transactions would be required.

4. **Validation**: 
   - Amounts must be positive (greater than 0)
   - Withdrawals cannot exceed the current balance
   - Ledger names cannot be blank

## Technology Stack

- **Java 21**
- **Spring Boot 4.0.1**

## Project Structure

```
src/main/java/com/example/tinyledger/
├── ledger/
│   ├── controller/          # REST API endpoints
│   ├── domain/              # Domain models (Ledger, Transaction, Money)
│   ├── service/             # Business logic
│   ├── repository/          # In-memory data storage
│   └── exception/           # Exception handling
├── user/
│   ├── controller/          # REST API endpoints
│   ├── domain/              # Domain models (User)
│   ├── service/             # Business logic
│   ├── repository/          # In-memory data storage
│   └── exception/           # Exception handling
└── TinyLedgerApplication.java
```
