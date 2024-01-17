package com.starlingbank.service;

import com.starlingbank.api.StarlingClient;
import com.starlingbank.model.Account;
import com.starlingbank.exceptions.ApiException;
import com.starlingbank.exceptions.ServiceException;

import org.json.JSONObject;

import java.io.IOException;

public class AccountService {
    private final StarlingClient starlingClient;

    public AccountService(StarlingClient starlingClient) {
        this.starlingClient = starlingClient;
    }

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
