package com.starlingbank.api;

// Importing necessary libraries for handling exceptions
import com.starlingbank.exceptions.ApiException;
import java.io.IOException;
import java.util.UUID;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

/**
 * This class is responsible for handling all the API calls to the Starling Bank.
 * It includes methods to get account details, transactions
 * savings goals, and also to create savings goals and add money to them.
 */
public class StarlingClient {
    private static final String APPLICATION_JSON = "application/json";
    private static final int HTTP_STATUS_OK = 200;

    private final String baseUrl;
    private final CloseableHttpClient httpClient;
    private final String accessToken;

    /**
     * Constructor for the StarlingClient class.
     * @param accessToken The access token to authenticate the API calls.
     */
    public StarlingClient(String accessToken) {
        this.baseUrl = "https://api-sandbox.starlingbank.com";
        this.accessToken = accessToken;
        this.httpClient = HttpClients.createDefault();
    }

    /**
     * This method sends the HTTP request and returns the response.
     * @param request The HTTP request to be sent.
     * @return The response body as a string.
     * @throws IOException If an input or output exception occurred.
     * @throws ApiException If an API exception occurred.
     */

    private String sendRequest(HttpUriRequest request) throws IOException, ApiException {
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        request.setHeader(HttpHeaders.ACCEPT, APPLICATION_JSON);

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());
            if (statusCode == HTTP_STATUS_OK) {
                return responseBody;
            } else {
                handleErrorResponse(responseBody, statusCode);
                return null; // unreachable
            }
        }
    }

    /**
     * This method handles the error response from the API.
     * @param responseBody The body of the response.
     * @param statusCode The status code of the response.
     * @throws ApiException If an API exception occurred.
     */
    private void handleErrorResponse(String responseBody, int statusCode) throws ApiException {
        throw new ApiException(statusCode, "API Error", responseBody);
    }

    /**
     * This method gets the account details.
     * @return The account details as a string.
     * @throws IOException If an input or output exception occurred.
     * @throws ApiException If an API exception occurred.
     */
    public String getAccountDetails() throws IOException, ApiException {
        HttpGet request = new HttpGet(baseUrl + "/api/v2/accounts");
        return sendRequest(request);
    }

    /**
     * This method gets the transactions between two timestamps.
     * @param accountUid The unique identifier of the account.
     * @param categoryUid The unique identifier of the category.
     * @param minTransactionTimestamp The minimum transaction timestamp.
     * @param maxTransactionTimestamp The maximum transaction timestamp.
     * @return The transactions as a string.
     * @throws IOException If an input or output exception occurred.
     * @throws ApiException If an API exception occurred.
     */
    public String getTransactions(String accountUid, String categoryUid,
                                String minTransactionTimestamp, String maxTransactionTimestamp)
                                throws IOException, ApiException {
        String url = baseUrl + "/api/v2/feed/account/" + accountUid + "/category/" + categoryUid
                + "/transactions-between?minTransactionTimestamp=" + minTransactionTimestamp
                + "&maxTransactionTimestamp=" + maxTransactionTimestamp;
        HttpGet request = new HttpGet(url);
        return sendRequest(request);
    }

    /**
     * This method gets the savings goals of an account.
     * @param accountUid The unique identifier of the account.
     * @return The savings goals as a string.
     * @throws IOException If an input or output exception occurred.
     * @throws ApiException If an API exception occurred.
     */
    public String getSavingsGoals(String accountUid) throws IOException, ApiException {
        HttpGet request = new HttpGet(baseUrl + "/api/v2/account/" + accountUid + "/savings-goals");
        return sendRequest(request);
    }

    /**
     * This method creates a savings goal.
     * @param accountUid The unique identifier of the account.
     * @param name The name of the savings goal.
     * @param currency The currency of the savings goal.
     * @param targetMinorUnits The target minor units of the savings goal.
     * @return The response as a string.
     * @throws IOException If an input or output exception occurred.
     * @throws ApiException If an API exception occurred.
     */
    public String createSavingsGoal(String accountUid, String name,
                                    String currency, int targetMinorUnits) throws IOException, ApiException {
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

    /**
     * This method adds money to a savings goal.
     * @param accountUid The unique identifier of the account.
     * @param savingsGoalUid The unique identifier of the savings goal.
     * @param amount The amount to be added.
     * @param currency The currency of the amount.
     * @throws IOException If an input or output exception occurred.
     * @throws ApiException If an API exception occurred.
     */
    public void addMoneyToSavingsGoal(String accountUid, String savingsGoalUid,
                                        int amount, String currency) throws IOException, ApiException {
        UUID transferUid = UUID.randomUUID();

        String url = baseUrl + "/api/v2/account/" + accountUid + "/savings-goals/"
                    + savingsGoalUid + "/add-money/" + transferUid;

        HttpPut request = new HttpPut(url);
        JSONObject amountJson = new JSONObject();
        amountJson.put("currency", currency);
        amountJson.put("minorUnits", amount);

        JSONObject addMoneyRequest = new JSONObject();
        addMoneyRequest.put("amount", amountJson);

        StringEntity entity = new StringEntity(addMoneyRequest.toString());
        request.setEntity(entity);
        request.setHeader("Content-Type", APPLICATION_JSON);

        sendRequest(request);
    }
}
