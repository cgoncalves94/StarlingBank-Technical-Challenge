package com.starlingbank.service;

import com.starlingbank.api.StarlingClient;
import com.starlingbank.model.Account;
import com.starlingbank.exceptions.ApiException;
import com.starlingbank.exceptions.ServiceException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * This class provides services related to Account.
 * It uses StarlingClient to communicate with the Starling Bank API.
 */
public class AccountService {
    // StarlingClient instance for API communication
    private final StarlingClient starlingClient;

    /**
     * Constructor for AccountService.
     * @param starlingClient StarlingClient instance for API communication
     */
    public AccountService(StarlingClient starlingClient) {
        this.starlingClient = starlingClient;
    }


    /**
     * Retrieves the account details from the Starling Bank API.
     * 
     * @return The account details.
     * @throws ServiceException If there is an error communicating with the API, reading the response, or parsing the response.
     */
    public Account getAccountDetails() {
        try {
            String response = starlingClient.getAccountDetails();
            JSONArray accountsArray = new JSONObject(response).getJSONArray("accounts");
            
            if (accountsArray.isEmpty()) {
                throw new ServiceException("No accounts found");
            }
            
            JSONObject accountJson = accountsArray.getJSONObject(0);
            return new Account(accountJson.getString("accountUid"), accountJson.getString("defaultCategory"));
        } catch (ApiException e) {
            throw new ServiceException("Error communicating with the API", e);
        } catch (IOException e) {
            throw new ServiceException("Error reading the response from the API", e);
        } catch (JSONException e) {
            throw new ServiceException("Error parsing the response from the API", e);
        }
    }
}
