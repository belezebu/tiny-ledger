package com.example.tinyledger.common.exception;

import com.example.tinyledger.ledger.domain.Money;

public class InvalidMoneyOperationException extends RuntimeException {
    public InvalidMoneyOperationException(String message) {
        super(message);
    }

    public static InvalidMoneyOperationException amountMustBePositiveNumber(Long amount) {
        return new InvalidMoneyOperationException("Amount must be a positive number: %d".formatted(amount));
    }

    public static InvalidMoneyOperationException couldNotSubtractGreaterNumber(Money firstAmount, Money secondAmount) {
        return new InvalidMoneyOperationException("Amount to subtract must not be greater than amount: %d - %d"
                .formatted(firstAmount.amount(), secondAmount.amount()));
    }

    public static InvalidMoneyOperationException insufficientFunds(Money balance, Money withdrawAmount) {
        return new InvalidMoneyOperationException("Insufficient funds. Balance: %d. Withdraw amount: %d"
                .formatted(balance.amount(), withdrawAmount.amount()));
    }
}
