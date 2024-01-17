package com.starlingbank;

// Importing necessary classes and packages
import com.starlingbank.api.StarlingClient;
import com.starlingbank.config.ConfigManager;
import com.starlingbank.exceptions.ApiException;
import com.starlingbank.service.AccountService;
import com.starlingbank.service.SavingsGoalService;
import com.starlingbank.service.TransactionService;
import com.starlingbank.util.RoundUpCalculator;
import com.starlingbank.util.UserInputHandler;

import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;

// Main class of the application
public class Main {

    // Logger for logging any errors or exceptions
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    // Main method of the application
    public static void main(String[] args) {
        try {
            // Setting up services and running the application
            ApplicationRunner appRunner = setupServices();
            appRunner.runApplication();
        } catch (IOException | ApiException e) {
            // Logging any errors or exceptions that occur
            LOGGER.log(Level.SEVERE, "An error occurred: ", e);
        }
    }

    // Method to setup services for the application
    private static ApplicationRunner setupServices() {
        // Initialize configuration manager and get access token
        ConfigManager configManager = new ConfigManager();
        String accessToken = configManager.getAccessToken();

        // Check if access token is valid
        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new IllegalArgumentException("Access token is not set or invalid in the properties file.");
        }

        // Initialize the API client and service classes
        StarlingClient client = new StarlingClient(accessToken);
        AccountService accountService = new AccountService(client);
        TransactionService transactionService = new TransactionService(client);
        SavingsGoalService savingsGoalService = new SavingsGoalService(client);

        // Initialize the round-up calculator and user input handler
        RoundUpCalculator calculator = new RoundUpCalculator();
        UserInputHandler userInputHandler = new UserInputHandler();
        // Return a new instance of ApplicationRunner with the initialized services
        return new ApplicationRunner(accountService, transactionService, savingsGoalService, calculator, userInputHandler);
    }
}
