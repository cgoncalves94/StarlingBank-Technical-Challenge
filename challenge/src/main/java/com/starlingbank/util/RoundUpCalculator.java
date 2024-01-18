package com.starlingbank.util;

import com.starlingbank.model.Transaction;
import java.util.List;

/**
 * The RoundUpCalculator class provides methods to calculate the round-up amount for transactions
 * and the total round-up amount for a list of transactions.
 */

public class RoundUpCalculator {

    private static final int MAX_PENCE = 100;

    /**
     * Calculates the round-up amount for a transaction.
     *
     * @param transaction The transaction object.
     * @return The round-up amount in minorUnits.
     */
    public int calculateRoundUpForTransaction(Transaction transaction) {
        int minorUnits = transaction.getMinorUnits();
        int pence = minorUnits % MAX_PENCE;
        return pence > 0 ? MAX_PENCE - pence : 0;
    }

    /**
     * Calculates the total round-up amount for a list of transactions.
     *
     * @param transactions The list of transaction objects.
     * @return The total round-up amount in minorUnits.
     */
    public int calculateTotalRoundUp(List<Transaction> transactions) {
        int totalRoundUp = 0;
        for (Transaction transaction : transactions) {
            // Only include transactions that are outbound payments.
            if ("FASTER_PAYMENTS_OUT".equals(transaction.getSource())) {
                totalRoundUp += calculateRoundUpForTransaction(transaction);
            }
        }
        return totalRoundUp;
    }
}
