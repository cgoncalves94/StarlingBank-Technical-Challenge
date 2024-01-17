package com.starlingbank.service;

import com.starlingbank.api.StarlingClient;
import com.starlingbank.model.Account;
import com.starlingbank.exceptions.ApiException;
import com.starlingbank.exceptions.ServiceException;

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
     * Fetches account details from the Starling Bank API.
     * @return Account object containing account details
     * @throws ServiceException if there is an error fetching account details
     */
    public Account getAccountDetails() {
        try {
            String response = starlingClient.getAccountDetails();
            JSONObject accountJson = new JSONObject(response).getJSONArray("accounts").getJSONObject(0);
            return new Account(accountJson.getString("accountUid"), accountJson.getString("defaultCategory"));
        } catch (ApiException | IOException e) {
            throw new ServiceException("Error fetching account details", e);
        }
    }
}
