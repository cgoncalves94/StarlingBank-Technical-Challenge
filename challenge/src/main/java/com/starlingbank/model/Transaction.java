package com.starlingbank.model;

/**
 * Represents a Transaction in the Starling Bank system.
 */
public class Transaction {
    // The amount in minor units (e.g., pences for GBP)
    private final int minorUnits;
    // The source of the transaction
    private final String source;

    /**
     * Constructs a Transaction with the specified minor units and source.
     *
     * @param minorUnits the amount in minor units
     * @param source the source of the transaction
     */
    public Transaction(int minorUnits, String source) {
        this.minorUnits = minorUnits;
        this.source = source;
    }

    /**
     * Returns the amount in minor units.
     *
     * @return the amount in minor units
     */
    public int getMinorUnits() {
        return minorUnits;
    }

    /**
     * Returns the source of the transaction.
     *
     * @return the source of the transaction
     */
    public String getSource() {
        return source;
    }
}
