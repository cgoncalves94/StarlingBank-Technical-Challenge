
package com.goncalves.api;


import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
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

    public StarlingClient(String accessToken) {
        this.accessToken = accessToken;
        this.httpClient = HttpClients.createDefault();
    }

    private String sendRequest(HttpUriRequest request) throws IOException {
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        request.setHeader(HttpHeaders.ACCEPT, "application/json"); //

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());

            if (statusCode != 200) {
                // Log the response body here to understand more about the failure
                System.err.println("Response body: " + responseBody);
                throw new RuntimeException("Failed: HTTP error code: " + statusCode);
            }
            return responseBody;
        }
    }

    // Endpoints

    // Method to get account details
    public String getAccountDetails() throws IOException {
        HttpGet request = new HttpGet(baseUrl + "/api/v2/accounts");
        return sendRequest(request);
    }

    // Method to get transactions between specific timestamps
    public String getTransactionsBetween(String accountUid, String categoryUid, String minTransactionTimestamp, String maxTransactionTimestamp) throws IOException {
        String url = baseUrl + "/api/v2/feed/account/" + accountUid + "/category/" + categoryUid
                    + "/transactions-between?minTransactionTimestamp=" + minTransactionTimestamp
                    + "&maxTransactionTimestamp=" + maxTransactionTimestamp;

        HttpGet request = new HttpGet(url);
        return sendRequest(request);
    }

    // Method to check if a savings goal already exists
    public String getSavingsGoals(String accountUid) throws IOException {
        HttpGet request = new HttpGet(baseUrl + "/api/v2/account/" + accountUid + "/savings-goals");
        return sendRequest(request);
    }
    // Method to create a savings goal
    public JSONObject createSavingsGoal(String accountUid, String name, String currency, int targetMinorUnits) throws IOException {
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
    // Method to add money to a savings goal
    public void addMoneyToSavingsGoal(String accountUid, String savingsGoalUid, int amount, String currency) throws IOException {
        UUID transferUid = UUID.randomUUID(); // Generate a unique transfer ID
        HttpPut request = new HttpPut(baseUrl + "/api/v2/account/" + accountUid + "/savings-goals/" + savingsGoalUid + "/add-money/" + transferUid);
        String json = "{\"amount\":{\"currency\":\"" + currency + "\",\"minorUnits\":" + amount + "}}";
        request.setEntity(new StringEntity(json));
        request.setHeader("Content-type", "application/json");
        sendRequest(request);
    }
}
