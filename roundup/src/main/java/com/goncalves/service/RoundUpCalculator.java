package com.goncalves.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The RoundUpCalculator class provides methods to calculate the round-up amount for transactions
 * and the total round-up amount for a list of transactions.
 */

public class RoundUpCalculator {

    /**
     * Calculates the round-up amount for a transaction.
     *
     * @param transaction The JSON object representing a transaction.
     * @return The round-up amount in minorUnits.
     */
    public int calculateRoundUpForTransaction(JSONObject transaction) {
        JSONObject amount = transaction.getJSONObject("amount");
        int minorUnits = amount.getInt("minorUnits");
        int pence = minorUnits % 100;
        return pence > 0 ? 100 - pence : 0;
    }

    /**
     * Calculates the total round-up amount for a list of transactions.
     *
     * @param transactionsJson The JSON string representing a list of transactions.
     * @return The total round-up amount in minorUnits.
     */
    public int calculateTotalRoundUp(String transactionsJson) {
        int totalRoundUp = 0;
        try {
            JSONObject feedItems = new JSONObject(transactionsJson);
            JSONArray transactions = feedItems.getJSONArray("feedItems");

            for (int i = 0; i < transactions.length(); i++) {
                JSONObject transaction = transactions.getJSONObject(i);
                // Calculate the round-up only for transactions with source "FASTER_PAYMENTS_OUT"
                if ("FASTER_PAYMENTS_OUT".equals(transaction.getString("source"))) {
                    totalRoundUp += calculateRoundUpForTransaction(transaction);
                }
            }
        } catch (JSONException e) {
            System.out.println("Invalid JSON array: " + e.getMessage());
        }
        return totalRoundUp;
    }
}
