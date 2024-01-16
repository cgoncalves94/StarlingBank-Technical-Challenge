
package com.goncalves.api;


import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.goncalves.exceptions.ApiException;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;
import java.util.UUID;

/**
 * The `StarlingClient` class is a client for interacting with the Starling Bank API. It provides methods to perform various operations, such as retrieving account details, getting
 * transactions between specific timestamps, checking savings goals, creating savings goals, and adding money to savings goals.
 * This class requires an access token to authenticate with the Starling Bank API. The access token should be provided when constructing an instance of `StarlingClient`.
 */

public class StarlingClient {

    private final String baseUrl = "https://api-sandbox.starlingbank.com";
    private final CloseableHttpClient httpClient;
    private final String accessToken;

    /**
     * Constructs a new StarlingClient object with the specified access token.
     *
     * @param accessToken the access token used for authentication
     */
    public StarlingClient(String accessToken) {
        this.accessToken = accessToken;
        this.httpClient = HttpClients.createDefault();
    }

    /**
     * Sends an HTTP request with the specified request object and returns the response body as a string.
     * Sets the necessary headers for authentication and content type.
     * Throws a RuntimeException if the response status code is not 200.
     *
     * @param request the HTTP request object
     * @return the response body as a string
     * @throws IOException if an I/O error occurs while sending the request
     */
    private String sendRequest(HttpUriRequest request) throws IOException, ApiException {
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        request.setHeader(HttpHeaders.ACCEPT, "application/json");

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());

            if (statusCode == 200) {
                return responseBody;
            } else {
                // Parse the error response based on the expected JSON structure
                if (responseBody.contains("\"errors\"")) {
                    // Handle the JSON structure with an "errors" array
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    JSONArray errors = jsonResponse.optJSONArray("errors");
                    if (errors != null && errors.length() > 0) {
                        JSONObject firstError = errors.getJSONObject(0);
                        String message = firstError.optString("message", "No message provided");
                        throw new ApiException(statusCode, "Error response received", message);
                    } else {
                        throw new ApiException(statusCode, "Unknown error", "No error information provided");
                    }
                } else {
                    // Handle the regular JSON structure with "error" and "error_description"
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    String error = jsonResponse.optString("error", "Unknown error");
                    String errorDescription = jsonResponse.optString("error_description", "No description provided");
                    throw new ApiException(statusCode, error, errorDescription);
                }
            }
        }
    }

    // Endpoints

    /**
     * Retrieves the account details for the authenticated user.
     *
     * @return the account details as a string
     * @throws IOException if an I/O error occurs while sending the request
     * @throws ApiException 
     */
    public String getAccountDetails() throws IOException, ApiException {
        HttpGet request = new HttpGet(baseUrl + "/api/v2/accounts");
        return sendRequest(request);
    }

    /**
     * Retrieves the transactions between the specified timestamps for the given account and category.
     *
     * @param accountUid              the account UID
     * @param categoryUid             the category UID
     * @param minTransactionTimestamp the minimum transaction timestamp
     * @param maxTransactionTimestamp the maximum transaction timestamp
     * @return the transactions as a string
     * @throws IOException if an I/O error occurs while sending the request
     * @throws ApiException 
     */
    public String getTransactionsBetween(String accountUid, String categoryUid, String minTransactionTimestamp, String maxTransactionTimestamp) throws IOException, ApiException {
        String url = baseUrl + "/api/v2/feed/account/" + accountUid + "/category/" + categoryUid
                + "/transactions-between?minTransactionTimestamp=" + minTransactionTimestamp
                + "&maxTransactionTimestamp=" + maxTransactionTimestamp;

        HttpGet request = new HttpGet(url);
        return sendRequest(request);
    }

    /**
     * Retrieves the savings goals for the specified account.
     *
     * @param accountUid the account UID
     * @return the savings goals as a string
     * @throws IOException if an I/O error occurs while sending the request
     * @throws ApiException 
     */
    public String getSavingsGoals(String accountUid) throws IOException, ApiException {
        HttpGet request = new HttpGet(baseUrl + "/api/v2/account/" + accountUid + "/savings-goals");
        return sendRequest(request);
    }

    /**
     * Creates a new savings goal for the specified account with the given name, currency, and target amount.
     *
     * @param accountUid        the account UID
     * @param name              the name of the savings goal
     * @param currency          the currency of the savings goal
     * @param targetMinorUnits  the target amount in minor units
     * @return the created savings goal as a JSONObject
     * @throws IOException if an I/O error occurs while sending the request
     * @throws ApiException 
     */
    public JSONObject createSavingsGoal(String accountUid, String name, String currency, int targetMinorUnits) throws IOException, ApiException {
        HttpPut request = new HttpPut(baseUrl + "/api/v2/account/" + accountUid + "/savings-goals");
        JSONObject target = new JSONObject();
        target.put("currency", currency);
        target.put("minorUnits", targetMinorUnits);

        JSONObject savingsGoalRequest = new JSONObject();
        savingsGoalRequest.put("name", name);
        savingsGoalRequest.put("currency", currency);
        savingsGoalRequest.put("target", target);

        StringEntity entity = new StringEntity(savingsGoalRequest.toString());
        request.setEntity(entity);
        request.setHeader("Content-Type", "application/json");

        String response = sendRequest(request);
        return new JSONObject(response);
    }

    /**
     * Adds money to the specified savings goal for the given account.
     *
     * @param accountUid       the account UID
     * @param savingsGoalUid   the savings goal UID
     * @param amount           the amount to add in minor units
     * @param currency         the currency of the amount
     * @throws IOException if an I/O error occurs while sending the request
     * @throws ApiException 
     */
    public void addMoneyToSavingsGoal(String accountUid, String savingsGoalUid, int amount, String currency) throws IOException, ApiException {
        UUID transferUid = UUID.randomUUID(); // Generate a unique transfer ID
        HttpPut request = new HttpPut(baseUrl + "/api/v2/account/" + accountUid + "/savings-goals/" + savingsGoalUid + "/add-money/" + transferUid);
        String json = "{\"amount\":{\"currency\":\"" + currency + "\",\"minorUnits\":" + amount + "}}";
        request.setEntity(new StringEntity(json));
        request.setHeader("Content-type", "application/json");
        sendRequest(request);
    }
}
