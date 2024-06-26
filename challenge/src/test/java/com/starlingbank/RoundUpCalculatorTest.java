
package com.starlingbank;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.starlingbank.model.Transaction;
import com.starlingbank.util.RoundUpCalculator;

/**
 * This class is used to test the RoundUpCalculator class.
 * @author Cesar Goncalves
 */
class RoundUpCalculatorTest {

    private static final int AMOUNT1 = 123;
    private static final int AMOUNT2 = 456;

    // Instance of RoundUpCalculator to be tested
    private RoundUpCalculator calculator = new RoundUpCalculator();

    // Test case for successful calculation of total round up from a list of transactions
    @Test
    void calculateTotalRoundUp() {
        // Arrange
        // Creating two transactions with "FASTER_PAYMENTS_OUT" source

        final int expectedRoundUp = 121;
        Transaction t1 = new Transaction(AMOUNT1, "FASTER_PAYMENTS_OUT");
        Transaction t2 = new Transaction(AMOUNT2, "FASTER_PAYMENTS_OUT");
        List<Transaction> transactions = Arrays.asList(t1, t2);

        // Act
        // Calculating the total round up from the list of transactions
        int result = calculator.calculateTotalRoundUp(transactions);

        // Assert
        // The expected result is 121 (77 pence for t1 and 44 pence for t2)
        assertThat(result).isEqualTo(expectedRoundUp);
    }

    // Test case for calculating total round up from an empty list of transactions
    @Test
    void calculateTotalRoundUp_NoTransactions() {
        // Arrange
        // Creating an empty list of transactions
        List<Transaction> transactions = Arrays.asList();

        // Act
        // Calculating the total round up from the empty list of transactions
        int result = calculator.calculateTotalRoundUp(transactions);

        // Assert
        // The expected result is 0 as there are no transactions
        assertThat(result).isZero();
    }

    // Test case for calculating total round up from a list of transactions with "FASTER_PAYMENTS_IN" source
    @Test
    void calculateTotalRoundUp_NotFasterPaymentOut() {
        // Arrange
        // Creating two transactions with "FASTER_PAYMENTS_IN" source
        Transaction t1 = new Transaction(AMOUNT1, "FASTER_PAYMENTS_IN");
        Transaction t2 = new Transaction(AMOUNT2, "FASTER_PAYMENTS_IN");
        List<Transaction> transactions = Arrays.asList(t1, t2);

        // Act
        // Calculating the total round up from the list of transactions
        int result = calculator.calculateTotalRoundUp(transactions);

        // Assert
        // The expected result is 0 as the transactions are not of "FASTER_PAYMENTS_OUT" source
        assertThat(result).isZero();
    }
}
