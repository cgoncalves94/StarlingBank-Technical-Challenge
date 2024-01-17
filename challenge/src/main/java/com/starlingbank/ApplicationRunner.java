package com.starlingbank;

import com.starlingbank.service.AccountService;
import com.starlingbank.service.TransactionService;
import com.starlingbank.service.SavingsGoalService;
import com.starlingbank.util.RoundUpCalculator;
import com.starlingbank.util.UserInputHandler;
import com.starlingbank.exceptions.ApiException;
import com.starlingbank.model.Account;
import com.starlingbank.model.Transaction;
import com.starlingbank.model.SavingGoal;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * The ApplicationRunner class is responsible for running the application.
 * It fetches account details, gets start and end dates from the user, gets transactions between specific timestamps,
 * calculates the total round-up amount, and manages savings goals.
 */
public class ApplicationRunner {
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final SavingsGoalService savingsGoalService;
    private final RoundUpCalculator calculator;
    private final UserInputHandler userInputHandler;

    /**
     * Constructor for ApplicationRunner.
     * Initializes the services, calculator, and user input handler.
     */
    public ApplicationRunner(AccountService accountService, TransactionService transactionService, SavingsGoalService savingsGoalService, RoundUpCalculator calculator, UserInputHandler userInputHandler) {
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.savingsGoalService = savingsGoalService;
        this.calculator = calculator;
        this.userInputHandler = userInputHandler;
    }

    /**
     * Runs the application.
     * Fetches account details, gets start and end dates from the user, gets transactions between specific timestamps,
     * calculates the total round-up amount, and manages savings goals.
     * @throws IOException if an I/O error occurs.
     * @throws ApiException if an API error occurs.
     */
    public void runApplication() throws IOException, ApiException {
        // Fetch account details
        Account account = accountService.getAccountDetails();
        String accountUid = account.getAccountUid();
        String categoryUid = account.getCategoryUid();

        // Get the start and end dates from the user
        LocalDate startDate = userInputHandler.readDate("Enter the start date (YYYY-MM-DD): ");
        LocalDate endDate = userInputHandler.readDate("Enter the end date (YYYY-MM-DD): ");

        // Convert LocalDate to ZonedDateTime at the start of the day in UTC
        ZonedDateTime startDateTime = startDate.atStartOfDay(ZoneOffset.UTC);
        ZonedDateTime endDateTime = endDate.atStartOfDay(ZoneOffset.UTC);

        // Format to the desired string representation
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String formattedStartDate = startDateTime.format(formatter);
        String formattedEndDate = endDateTime.format(formatter);

        // Get transactions between specific timestamps
        List<Transaction> transactions = transactionService.getTransactionsBetween(accountUid, categoryUid, formattedStartDate, formattedEndDate);

        // Calculate the total round-up amount
        int totalRoundUpMinorUnits = calculator.calculateTotalRoundUp(transactions);
        double totalRoundUpPounds = totalRoundUpMinorUnits / 100.0;

        // Manage savings goals
        manageSavingsGoals(accountUid, totalRoundUpMinorUnits, totalRoundUpPounds);
    }

    /**
     * Manages savings goals.
     * If there are no savings goals, it prompts the user to create one.
     * If there is a round-up amount, it adds it to the savings goal.
     * @param accountUid The account UID.
     * @param totalRoundUpMinorUnits The total round-up amount in minor units.
     * @param totalRoundUpPounds The total round-up amount in pounds.
     */
    private void manageSavingsGoals(String accountUid, int totalRoundUpMinorUnits, double totalRoundUpPounds) {
        List<SavingGoal> savingsGoals = savingsGoalService.getSavingsGoals(accountUid);
        String savingsGoalUid;

        if (savingsGoals.isEmpty()) {
            String goalName = userInputHandler.readString("Enter a name for your savings goal: ");
            double targetAmountPounds = userInputHandler.readDouble("Enter your target amount in pounds: ");
            int targetAmountMinorUnits = (int) (targetAmountPounds * 100);

            SavingGoal createdGoal = savingsGoalService.createSavingsGoal(accountUid, goalName, "GBP", targetAmountMinorUnits);
            savingsGoalUid = createdGoal.getSavingsGoalUid();
        } else {
            savingsGoalUid = savingsGoals.getFirst().getSavingsGoalUid();
        }

        if (totalRoundUpMinorUnits > 0) {
            savingsGoalService.addMoneyToSavingsGoal(accountUid, savingsGoalUid, totalRoundUpMinorUnits, "GBP");
            System.out.printf("Total round-up amount transferred to savings goal: £%.2f%n", totalRoundUpPounds);
        } else {
            System.out.println("No round-up amount to transfer.");
        }
    }
}
