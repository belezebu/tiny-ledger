package com.example.tinyledger.ledger.domain;

import com.example.tinyledger.common.exception.InvalidMoneyOperationException;

public record Money(Long amount) {
    private static final long ZERO = 0;

    public Money {
        if (amount == null || amount < 0) {
            throw InvalidMoneyOperationException.amountMustBePositiveNumber(amount);
        }
    }

    public static Money zero() {
        return new Money(ZERO);
    }

    public Money add(Money other) {
        return new Money(this.amount + other.amount);
    }

    public Money subtract(Money other) {
        if (this.amount < other.amount) {
            throw InvalidMoneyOperationException.couldNotSubtractGreaterNumber(this, other);
        }
        return new Money(this.amount - other.amount);
    }

    public boolean isLessThan(Money withdrawAmount) {
        return this.amount < withdrawAmount.amount;
    }
}
