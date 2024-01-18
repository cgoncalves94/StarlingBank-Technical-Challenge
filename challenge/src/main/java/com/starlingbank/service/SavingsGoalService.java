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
import com.starlingbank.model.Account;
import com.starlingbank.model.Amount;
import com.starlingbank.model.SavingGoal;

/**
 * Service class for managing savings goals.
 * @author Cesar Goncalves
 */
public class SavingsGoalService {
    // StarlingClient instance for API communication
    private final StarlingClient starlingClient;

    /**
     * Constructor for SavingsGoalService.
     * @param starlingClient StarlingClient instance for API communication
     */
    public SavingsGoalService(StarlingClient starlingClient) {
        this.starlingClient = starlingClient;
    }

    /**
     * Fetches a list of savings goals for a given account.
     * @param accountUid Unique identifier for the account
     * @return List of SavingGoal objects
     * @throws ServiceException if there is an error fetching savings goals
     */
    public List<SavingGoal> getSavingsGoals(String accountUid) {
        // Validate input
        if (accountUid == null) {
            throw new IllegalArgumentException("accountUid cannot be null");
        }
        try {
            String response = starlingClient.getSavingsGoals(accountUid);
            JSONArray savingsGoalsJson = new JSONObject(response).getJSONArray("savingsGoalList");
            List<SavingGoal> savingsGoals = new ArrayList<>();
            for (int i = 0; i < savingsGoalsJson.length(); i++) {
                JSONObject goalJson = savingsGoalsJson.getJSONObject(i);
                String savingsGoalUid = goalJson.getString("savingsGoalUid");
                String name = goalJson.getString("name");
                JSONObject targetJson = goalJson.getJSONObject("target");
                int minorUnits = targetJson.getInt("minorUnits");
                String currency = targetJson.getString("currency");
                Amount target = new Amount(minorUnits, currency);
                savingsGoals.add(new SavingGoal(savingsGoalUid, name, target));
            }
            return savingsGoals;
        } catch (ApiException e) {
            throw new ServiceException("Error communicating with the API", e);
        } catch (IOException e) {
            throw new ServiceException("Error reading the response from the API", e);
        } catch (JSONException e) {
            throw new ServiceException("Error parsing the response from the API", e);
        }
    }

    /**
     * This method creates a new savings goal for a given account.
     *
     * @param account Account object having the unique identifier
     * for the account for which savings goal is being created
     * @param goalName Name of the savings goal
     * @param targetAmount Amount object which includes the currency code and minor units,
     * representing the target amount for the savings goal
     * @return A SavingGoal object that represents the newly created savings goal.
     * It includes the savings goal's unique identifier, name, and target amount
     * @throws ServiceException  If there's an error while creating the savings goal due to an IOException or
     * ApiException. It contains an error message and the original exception.
     */
    public SavingGoal createSavingsGoal(Account account, String goalName, Amount targetAmount) {
        try {
            String response = starlingClient.createSavingsGoal(account.getAccountUid(),
                goalName, targetAmount.getCurrencyCode(), targetAmount.getMinorUnits());
            JSONObject goalJson = new JSONObject(response);
            String savingsGoalUid = goalJson.getString("savingsGoalUid");
            return new SavingGoal(savingsGoalUid, goalName, targetAmount);
        } catch (IOException | ApiException e) {
            throw new ServiceException("Error creating savings goal: " + goalName, e);
        }
    }

    /**
     * Adds money to the specified savings goal for the given account.
     *
     * @param account The account object containing the unique identifier for the account.
     * @param savingGoal The saving goal object containing the unique identifier for the savings goal.
     * @param amount The amount object containing the currency and the amount in minor units to add to the savings goal.
     * @throws ServiceException if there is an error adding money to the savings goal.
     */
    public void addMoneyToSavingsGoal(Account account, SavingGoal savingGoal, Amount amount) {
        try {
            String currency = amount.getCurrencyCode();
            int minorUnits = amount.getMinorUnits();
            starlingClient.addMoneyToSavingsGoal(account.getAccountUid(),
                savingGoal.getSavingsGoalUid(), minorUnits, currency);
        } catch (IOException | ApiException e) {
            throw new ServiceException("Error adding money to savings goal: " + savingGoal.getName(), e);
        }
    }
}
