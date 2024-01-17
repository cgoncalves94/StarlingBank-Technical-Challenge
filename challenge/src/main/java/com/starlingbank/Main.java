package com.starlingbank;



import com.starlingbank.api.StarlingClient;
import com.starlingbank.config.ConfigManager;
import com.starlingbank.exceptions.ApiException;
import com.starlingbank.service.AccountService;
import com.starlingbank.service.SavingsGoalService;
import com.starlingbank.service.TransactionService;
import com.starlingbank.util.RoundUpCalculator;
import com.starlingbank.util.UserInputHandler;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            ApplicationRunner appRunner = setupServices();
            appRunner.runApplication();
        } catch (IOException | ApiException e) {
            logger.error("An error occurred: ", e);
        }
    }

    private static ApplicationRunner setupServices() throws IOException, ApiException {
        // Initialize configuration manager and get access token
        ConfigManager configManager = new ConfigManager();
        String accessToken = configManager.getAccessToken();

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
        return new ApplicationRunner(accountService, transactionService, savingsGoalService, calculator, userInputHandler);
    }
}
