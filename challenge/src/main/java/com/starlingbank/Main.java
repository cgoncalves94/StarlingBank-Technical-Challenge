package com.starlingbank;

import com.starlingbank.api.StarlingClient;
import com.starlingbank.config.ConfigManager;
import com.starlingbank.service.AccountService;
import com.starlingbank.service.TransactionService;
import com.starlingbank.service.SavingsGoalService;
import com.starlingbank.util.RoundUpCalculator;
import com.starlingbank.util.UserInputHandler;
import com.starlingbank.exceptions.ApiException;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            // Initialize configuration manager and get access token
            ConfigManager configManager = new ConfigManager();
            String accessToken = configManager.getAccessToken();
            
            if (accessToken == null || accessToken.trim().isEmpty()) {
                System.out.println("Access token is not set or invalid in the properties file.");
                return;
            }

            // Initialize the API client
            StarlingClient client = new StarlingClient(accessToken);

            // Initialize service classes
            AccountService accountService = new AccountService(client);
            TransactionService transactionService = new TransactionService(client);
            SavingsGoalService savingsGoalService = new SavingsGoalService(client);

            // Initialize the round-up calculator and user input handler
            RoundUpCalculator calculator = new RoundUpCalculator();
            UserInputHandler userInputHandler = new UserInputHandler();

            // Initialize and run the application runner with services
            ApplicationRunner appRunner = new ApplicationRunner(accountService, transactionService, savingsGoalService, calculator, userInputHandler);
            appRunner.runApplication();
            
        } catch (IOException | ApiException e) {
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
