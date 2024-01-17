package com.starlingbank.model;



public class Transaction {
    private final int minorUnits;
    private final String source;

    public Transaction(int minorUnits, String source) {
        this.minorUnits = minorUnits;
        this.source = source;
    }

    public int getMinorUnits() {
        return minorUnits;
    }

    public String getSource() {
        return source;
    }


}
