package com.example.tinyledger.ledger.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.tinyledger.common.exception.InvalidMoneyOperationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MoneyTest {

    @Test
    @DisplayName("Should create money with positive amount")
    void shouldCreateMoneyWithPositiveAmount() {
        Money money = new Money(100L);
        assertEquals(100L, money.amount());
    }

    @Test
    @DisplayName("Should create money with zero amount")
    void shouldCreateMoneyWithZeroAmount() {
        Money money = Money.zero();
        assertEquals(0L, money.amount());
    }

    @Test
    @DisplayName("Should throw exception for negative amount")
    void shouldThrowExceptionForNegativeAmount() {
        assertThrows(InvalidMoneyOperationException.class, () -> new Money(-100L));
    }

    @Test
    @DisplayName("Should throw exception for null amount")
    void shouldThrowExceptionForNullAmount() {
        assertThrows(InvalidMoneyOperationException.class, () -> new Money(null));
    }

    @Test
    @DisplayName("Should add money correctly")
    void shouldAddMoneyCorrectly() {
        Money first = new Money(100L);
        Money second = new Money(50L);

        Money result = first.add(second);

        assertEquals(150L, result.amount());
    }

    @Test
    @DisplayName("Should add zero to money")
    void shouldAddZeroToMoney() {
        Money first = new Money(100L);
        Money zero = Money.zero();

        Money result = first.add(zero);

        assertEquals(100L, result.amount());
    }

    @Test
    @DisplayName("Should subtract money correctly")
    void shouldSubtractMoneyCorrectly() {
        Money first = new Money(100L);
        Money second = new Money(50L);

        Money result = first.subtract(second);

        assertEquals(50L, result.amount());
    }

    @Test
    @DisplayName("Should subtract equal amounts to get zero")
    void shouldSubtractEqualAmountsToGetZero() {
        Money first = new Money(100L);
        Money second = new Money(100L);

        Money result = first.subtract(second);

        assertEquals(0L, result.amount());
    }

    @Test
    @DisplayName("Should throw exception when subtracting greater amount")
    void shouldThrowExceptionWhenSubtractingGreaterAmount() {
        Money first = new Money(50L);
        Money second = new Money(100L);

        assertThrows(InvalidMoneyOperationException.class, () -> first.subtract(second));
    }

    @Test
    @DisplayName("Should compare amounts correctly with isLessThan")
    void shouldCompareAmountsCorrectly() {
        Money smaller = new Money(50L);
        Money larger = new Money(100L);

        assertTrue(smaller.isLessThan(larger));
        assertFalse(larger.isLessThan(smaller));
    }

    @Test
    @DisplayName("Should return false when comparing equal amounts")
    void shouldReturnFalseWhenComparingEqualAmounts() {
        Money first = new Money(100L);
        Money second = new Money(100L);

        assertFalse(first.isLessThan(second));
    }

    @Test
    @DisplayName("Should handle large amounts")
    void shouldHandleLargeAmounts() {
        Money large = new Money(Long.MAX_VALUE - 1);
        Money small = new Money(1L);

        Money result = large.add(small);

        assertEquals(Long.MAX_VALUE, result.amount());
    }
}
