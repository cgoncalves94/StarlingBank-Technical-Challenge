package com.starlingbank.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.starlingbank.api.StarlingClient;
import com.starlingbank.exceptions.ApiException;
import com.starlingbank.exceptions.ServiceException;
import com.starlingbank.model.Transaction;

/**
 * This service class is responsible for handling transaction related operations.
 * @author Cesar Goncalves
 */
public class TransactionService {
    // Instance of StarlingClient for API communication
    private final StarlingClient starlingClient;

    /**
     * Constructor for TransactionService.
     * @param starlingClient Instance of StarlingClient for API communication
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
     * @throws ServiceException if there is an error while fetching transactions
     */
    public List<Transaction> getTransactions(String accountUid, String categoryUid,
        String minTransactionTimestamp, String maxTransactionTimestamp) {
        validateParameters(accountUid, categoryUid, minTransactionTimestamp, maxTransactionTimestamp);
        try {
            String response = starlingClient.getTransactions(accountUid, categoryUid,
                minTransactionTimestamp, maxTransactionTimestamp);
            return parseTransactions(response);
        } catch (IOException e) {
            throw new ServiceException("Error occurred while communicating with the API", e);
        } catch (ApiException e) {
            throw new ServiceException("Received an error response from the API", e);
        } catch (JSONException e) {
            throw new ServiceException("Error occurred while parsing the response from the API", e);
        }
    }

    /**
     * Validates the input parameters.
     * @param params Input parameters to validate
     * @throws IllegalArgumentException if any input parameter is null or empty
     */
    private void validateParameters(String... params) {
        for (String param : params) {
            if (param == null || param.isEmpty()) {
                throw new IllegalArgumentException("Input parameters cannot be null or empty");
            }
        }
    }

    /**
     * Parses the transactions from the response.
     * @param response Response string to parse
     * @return List of Transaction objects
     * @throws JSONException if there is an error while parsing the response
     */
    private List<Transaction> parseTransactions(String response) throws JSONException {
        JSONArray transactionsJson = new JSONObject(response).getJSONArray("feedItems");
        List<Transaction> transactions = new ArrayList<>();
        for (int i = 0; i < transactionsJson.length(); i++) {
            JSONObject transactionJson = transactionsJson.getJSONObject(i);
            int minorUnits = transactionJson.getJSONObject("amount").getInt("minorUnits");
            String source = transactionJson.getString("source");
            transactions.add(new Transaction(minorUnits, source));
        }
        return transactions;
    }
}
