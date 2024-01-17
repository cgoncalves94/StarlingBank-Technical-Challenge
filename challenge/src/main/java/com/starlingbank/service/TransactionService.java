package com.starlingbank.service;

import com.starlingbank.api.StarlingClient;
import com.starlingbank.model.Transaction;
import com.starlingbank.exceptions.ApiException;
import com.starlingbank.exceptions.ServiceException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class to handle transaction related operations.
 */
public class TransactionService {
    // StarlingClient instance for API communication
    private final StarlingClient starlingClient;

    /**
     * Constructor for TransactionService.
     * @param starlingClient StarlingClient instance for API communication
     */
    public TransactionService(StarlingClient starlingClient) {
        this.starlingClient = starlingClient;
    }

    /**
     * Fetches a list of transactions for a given account and category within a specified time range.
     * @param accountUid Unique identifier for the account
     * @param categoryUid Unique identifier for the category
     * @param minTransactionTimestamp Minimum timestamp for the transaction
     * @param maxTransactionTimestamp Maximum timestamp for the transaction
     * @return List of Transaction objects
     * @throws ServiceException if there is an error fetching transactions
     */
    public List<Transaction> getTransactionsBetween(String accountUid, String categoryUid, String minTransactionTimestamp, String maxTransactionTimestamp) {
        try {
            String response = starlingClient.getTransactions(accountUid, categoryUid, minTransactionTimestamp, maxTransactionTimestamp);
            JSONArray transactionsJson = new JSONObject(response).getJSONArray("feedItems");
            List<Transaction> transactions = new ArrayList<>();
            for (int i = 0; i < transactionsJson.length(); i++) {
                JSONObject transactionJson = transactionsJson.getJSONObject(i);
                int minorUnits = transactionJson.getJSONObject("amount").getInt("minorUnits");
                String source = transactionJson.getString("source");
                transactions.add(new Transaction(minorUnits, source));
            }
            return transactions;
        } catch (IOException | ApiException e) {
            throw new ServiceException("Error fetching transactions", e);
        }
    }
}