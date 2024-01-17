package com.starlingbank.model;

public class Amount {
    private final int minorUnits;
    private final String currency;

    public Amount(int minorUnits, String currency) {
        this.minorUnits = minorUnits;
        this.currency = currency;
    }

    public int getMinorUnits() {
        return minorUnits;
    }

    public String getCurrency() {
        return currency;
    }
}
