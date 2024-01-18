package com.starlingbank;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.starlingbank.api.StarlingClient;
import com.starlingbank.config.ConfigManager;
import com.starlingbank.exceptions.ApiException;
import com.starlingbank.service.AccountService;
import com.starlingbank.service.SavingsGoalService;
import com.starlingbank.service.TransactionService;
import com.starlingbank.util.RoundUpCalculator;
import com.starlingbank.util.UserInputHandler;

/**
 * Main class of the application.
 */
public final class Main {

    // Logger for logging any errors or exceptions
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    private Main() {
        // Private constructor to prevent instantiation
    }

    /**
     * Main method of the application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // Attempt to set up the application runner with the access token from the config
            ApplicationRunner appRunner = setupServices();

            if (appRunner == null) {
                LOGGER.log(Level.SEVERE, "Please update the configuration file with a valid access token.");
                return; // Exit the application
            }

            // Run the application
            appRunner.runApplication();
        } catch (IOException | ApiException e) {
            LOGGER.log(Level.SEVERE, "An error occurred: ", e);
        }
    }

    // Method to set up services for the application
    private static ApplicationRunner setupServices() {
        ConfigManager configManager = new ConfigManager();
        String accessToken = configManager.getAccessToken();

        if (accessToken == null || accessToken.trim().isEmpty()) {
            LOGGER.log(Level.SEVERE, "Access token is not set or invalid in the properties file.");
            return null; // Token is not set or is empty
        }

        try {
            StarlingClient client = new StarlingClient(accessToken);
            client.getAccountDetails(); // Validate the token by attempting an API call

            // If the token is valid, set up the rest of the services
            AccountService accountService = new AccountService(client);
            TransactionService transactionService = new TransactionService(client);
            SavingsGoalService savingsGoalService = new SavingsGoalService(client);
            RoundUpCalculator calculator = new RoundUpCalculator();
            UserInputHandler userInputHandler = new UserInputHandler();

            return new ApplicationRunner(accountService, transactionService,
                savingsGoalService, calculator, userInputHandler);

        } catch (IOException | ApiException e) {
            LOGGER.log(Level.SEVERE, "The provided access token is not valid: {0}", e.getMessage());
            return null; // Token validation failed
        }
    }
}
