package com.goncalves;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;
import org.json.JSONObject;
import org.json.JSONArray;
import com.goncalves.api.StarlingClient;
import com.goncalves.exceptions.ApiException;
import com.goncalves.service.RoundUpCalculator;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * This class represents the RoundUpApplication which is responsible for calculating the total round-up amount from
 * transactions, creating a savings goal, and transferring the round-up amount to the savings goal.
 */


public class RoundUpApplication {

    /**
    * Main method to run the RoundUpApplication.
    *
    * @param args command line arguments
    * @throws IOException if an I/O error occurs
     * @throws ApiException 
    */
    public static void main(String[] args) throws IOException, ApiException {
        // Load the properties file containing the access token
        Properties prop = new Properties();
        String accessToken = "";

        try (InputStream input = new FileInputStream("roundup/resources/config.properties")) {
            // Load the properties file
            prop.load(input);
            // Retrieve the access token
            accessToken = prop.getProperty("ACCESS_TOKEN");
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        if (accessToken == null || accessToken.trim().isEmpty()) {
            System.out.println("Access token is not set or invalid in the properties file.");
            return; // Exit the application if the token is not provided
        }

        // Create a scanner to read user input
        Scanner scanner = new Scanner(System.in);

        // Create a Starling client with the access token
        StarlingClient client = new StarlingClient(accessToken);

        // Get account details
        String accountDetailsJson = client.getAccountDetails();
        JSONObject accountDetails = new JSONObject(accountDetailsJson);
        JSONArray accounts = accountDetails.getJSONArray("accounts");
        String accountUid = accounts.getJSONObject(0).getString("accountUid");
        String categoryUid = accounts.getJSONObject(0).getString("defaultCategory");


        // Get the start and end dates from the user
        System.out.println("Enter the start date (YYYY-MM-DD): ");
        String startDateInput = scanner.nextLine();
        System.out.println("Enter the end date (YYYY-MM-DD): ");
        String endDateInput = scanner.nextLine();

        // Validate the date format
        try {
            LocalDate.parse(startDateInput);
            LocalDate.parse(endDateInput);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please enter dates in the format YYYY-MM-DD.");
            scanner.close();
            return; // Exit the application if the dates are invalid
        }
        String startDate = startDateInput + "T00:00:00Z";
        String endDate = endDateInput + "T00:00:00Z";

        // Get transactions between specific timestamps
        String transactionsJson = client.getTransactionsBetween(accountUid, categoryUid, startDate, endDate);

        

        // Calculate the total round-up amount for the transactions
        RoundUpCalculator calculator = new RoundUpCalculator();
        int totalRoundUpMinorUnits = calculator.calculateTotalRoundUp(transactionsJson);
        double totalRoundUpPounds = totalRoundUpMinorUnits / 100.0;

        // Check if there are any savings goals
        // If there are savings goals, transfer the round-up amount to the first savings goal
        // Otherwise create a savings goal and transfer the round-up amount to it
        String savingsGoalsJson = client.getSavingsGoals(accountUid);
        JSONObject savingsGoals = new JSONObject(savingsGoalsJson);
        JSONArray savingsGoalList = savingsGoals.getJSONArray("savingsGoalList");
        String savingsGoalUid;
        if (savingsGoalList.isEmpty()) {
            System.out.println("Enter a name for your savings goal: ");
            String goalName = scanner.nextLine();
            System.out.println("Enter your target amount in pounds: ");
            double targetAmountPounds = Double.parseDouble(scanner.nextLine());
            int targetAmountMinorUnits = (int) (targetAmountPounds * 100);
            System.out.println("Enter the currency for your savings goal (e.g., GBP): ");
            String currency = scanner.nextLine();

            JSONObject createSavingsGoalResponse = client.createSavingsGoal(accountUid, goalName, currency, targetAmountMinorUnits);
            savingsGoalUid = createSavingsGoalResponse.getString("savingsGoalUid");
        } else {
            savingsGoalUid = savingsGoalList.getJSONObject(0).getString("savingsGoalUid");
        }
        if (totalRoundUpMinorUnits > 0) {
            client.addMoneyToSavingsGoal(accountUid, savingsGoalUid, totalRoundUpMinorUnits, "GBP");
            System.out.printf("Total round-up amount transferred to savings goal: £%.2f%n", totalRoundUpPounds);
        } else {
            System.out.println("No round-up amount to transfer.");
        }

        // Close the scanner
        scanner.close();
    }
}
