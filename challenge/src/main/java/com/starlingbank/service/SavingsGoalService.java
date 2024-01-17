package com.starlingbank.service;

import com.starlingbank.api.StarlingClient;
import com.starlingbank.model.SavingGoal;
import com.starlingbank.model.Amount;
import com.starlingbank.exceptions.ApiException;
import com.starlingbank.exceptions.ServiceException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for managing savings goals.
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
        } catch (IOException | ApiException e) {
            throw new ServiceException("Error fetching savings goals", e);
        }
    }

    /**
     * Creates a new savings goal for a given account.
     * @param accountUid Unique identifier for the account
     * @param name Name of the savings goal
     * @param currency Currency of the savings goal
     * @param targetMinorUnits Target amount for the savings goal in minor units
     * @return SavingGoal object representing the newly created savings goal
     * @throws ServiceException if there is an error creating the savings goal
     */
    public SavingGoal createSavingsGoal(String accountUid, String name, String currency, int targetMinorUnits) {
        try {
            String response = starlingClient.createSavingsGoal(accountUid, name, currency, targetMinorUnits);
            JSONObject goalJson = new JSONObject(response);
            String savingsGoalUid = goalJson.getString("savingsGoalUid");
            Amount target = new Amount(targetMinorUnits, currency);
            return new SavingGoal(savingsGoalUid, name, target);
        } catch (IOException | ApiException e) {
            throw new ServiceException("Error creating savings goal", e);
        }
    }

    /**
     * Adds money to a savings goal.
     * @param accountUid Unique identifier for the account
     * @param savingsGoalUid Unique identifier for the savings goal
     * @param amount Amount to add to the savings goal in minor units
     * @param currency Currency of the savings goal
     * @throws ServiceException if there is an error adding money to the savings goal
     */
    public void addMoneyToSavingsGoal(String accountUid, String savingsGoalUid, int amount, String currency) {
        try {
            starlingClient.addMoneyToSavingsGoal(accountUid, savingsGoalUid, amount, currency);
        } catch (IOException | ApiException e) {
            throw new ServiceException("Error adding money to savings goal", e);
        }
    }
}
