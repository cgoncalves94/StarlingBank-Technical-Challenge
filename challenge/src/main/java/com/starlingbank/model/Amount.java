package com.starlingbank.model;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

/**
 * Represents an Amount in the Starling Bank system.
 * @author Cesar Goncalves
 */
public class Amount {
    private final int minorUnits;
    private final String currencyCode;

    public Amount(int minorUnits, String currencyCode) {
        this.minorUnits = minorUnits;
        this.currencyCode = currencyCode;
    }

    public int getMinorUnits() {
        return minorUnits;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    // Converts minor units to major units (e.g., pence to pounds)
    public BigDecimal toMajorUnits() {
        Currency currency = Currency.getInstance(currencyCode);
        return BigDecimal.valueOf(minorUnits, currency.getDefaultFractionDigits());
    }

    // Formats the amount for display, e.g., "£10.00" or "€10.00"
    /**
     * Formats the amount for display.
     *
     * @param locale the locale to use for formatting.
     * @return the formatted amount.
     */
    public String format(Locale locale) {
        BigDecimal amount = toMajorUnits();
        Currency currency = Currency.getInstance(currencyCode);
        NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        format.setCurrency(currency);
        return format.format(amount);
    }
}
