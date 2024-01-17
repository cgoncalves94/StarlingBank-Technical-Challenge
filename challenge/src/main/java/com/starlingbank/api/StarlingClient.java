package com.starlingbank.api;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.starlingbank.exceptions.ApiException;

import java.io.IOException;
import java.util.UUID;

public class StarlingClient {
    private final String baseUrl;
    private final CloseableHttpClient httpClient;
    private final String accessToken;

    public StarlingClient(String accessToken) {
        this.baseUrl = "https://api-sandbox.starlingbank.com";
        this.accessToken = accessToken;
        this.httpClient = HttpClients.createDefault();
    }

    private String sendRequest(HttpUriRequest request) throws IOException, ApiException {
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        request.setHeader(HttpHeaders.ACCEPT, "application/json");

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());
            if (statusCode == 200) {
                return responseBody;
            } else {
                handleErrorResponse(responseBody, statusCode);
                return null; // unreachable
            }
        }
    }

    private void handleErrorResponse(String responseBody, int statusCode) throws ApiException {
        throw new ApiException(statusCode, "API Error", responseBody);
    }

    public String getAccountDetails() throws IOException, ApiException {
        HttpGet request = new HttpGet(baseUrl + "/api/v2/accounts");
        return sendRequest(request);
    }

    public String getTransactions(String accountUid, String categoryUid, String minTransactionTimestamp, String maxTransactionTimestamp) throws IOException, ApiException {
        String url = baseUrl + "/api/v2/feed/account/" + accountUid + "/category/" + categoryUid
                + "/transactions-between?minTransactionTimestamp=" + minTransactionTimestamp
                + "&maxTransactionTimestamp=" + maxTransactionTimestamp;
        HttpGet request = new HttpGet(url);
        return sendRequest(request);
    }

    public String getSavingsGoals(String accountUid) throws IOException, ApiException {
        HttpGet request = new HttpGet(baseUrl + "/api/v2/account/" + accountUid + "/savings-goals");
        return sendRequest(request);
    }

    public String createSavingsGoal(String accountUid, String name, String currency, int targetMinorUnits) throws IOException, ApiException {
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

        return sendRequest(request);
    }

    public void addMoneyToSavingsGoal(String accountUid, String savingsGoalUid, int amount, String currency) throws IOException, ApiException {
        UUID transferUid = UUID.randomUUID();
        String url = baseUrl + "/api/v2/account/" + accountUid + "/savings-goals/" + savingsGoalUid + "/add-money/" + transferUid;
        HttpPut request = new HttpPut(url);
        JSONObject amountJson = new JSONObject();
        amountJson.put("currency", currency);
        amountJson.put("minorUnits", amount);

        JSONObject addMoneyRequest = new JSONObject();
        addMoneyRequest.put("amount", amountJson);

        StringEntity entity = new StringEntity(addMoneyRequest.toString());
        request.setEntity(entity);
        request.setHeader("Content-Type", "application/json");

        sendRequest(request);
    }
}
