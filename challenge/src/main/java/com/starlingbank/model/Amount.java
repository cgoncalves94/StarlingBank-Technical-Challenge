package com.starlingbank.model;

/**
 * Represents an Amount in the Starling Bank system.
 */
public class Amount {
    // The amount in minor units (e.g., pences for GBP)
    private final int minorUnits;
    // The currency of the amount (e.g., GBP, EUR)
    private final String currency;

    /**
     * Constructs an Amount with the specified minor units and currency.
     *
     * @param minorUnits the amount in minor units
     * @param currency the currency of the amount
     */
    public Amount(int minorUnits, String currency) {
        this.minorUnits = minorUnits;
        this.currency = currency;
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
     * Returns the currency of the amount.
     *
     * @return the currency of the amount
     */
    public String getCurrency() {
        return currency;
    }
}
