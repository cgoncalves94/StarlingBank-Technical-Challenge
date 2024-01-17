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

public class TransactionService {
    private final StarlingClient starlingClient;

    public TransactionService(StarlingClient starlingClient) {
        this.starlingClient = starlingClient;
    }

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
