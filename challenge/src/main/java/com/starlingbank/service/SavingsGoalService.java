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

public class SavingsGoalService {
    private final StarlingClient starlingClient;

    public SavingsGoalService(StarlingClient starlingClient) {
        this.starlingClient = starlingClient;
    }

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

    public void addMoneyToSavingsGoal(String accountUid, String savingsGoalUid, int amount, String currency) {
        try {
            starlingClient.addMoneyToSavingsGoal(accountUid, savingsGoalUid, amount, currency);
        } catch (IOException | ApiException e) {
            throw new ServiceException("Error adding money to savings goal", e);
        }
    }
}
