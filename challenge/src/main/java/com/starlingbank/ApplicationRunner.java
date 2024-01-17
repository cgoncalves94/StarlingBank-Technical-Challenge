package com.starlingbank;

import com.starlingbank.service.AccountService;
import com.starlingbank.service.TransactionService;
import com.starlingbank.service.SavingsGoalService;
import com.starlingbank.util.RoundUpCalculator;
import com.starlingbank.util.UserInputHandler;
import com.starlingbank.exceptions.ApiException;
import com.starlingbank.model.Account;
import com.starlingbank.model.Amount;
import com.starlingbank.model.Transaction;
import com.starlingbank.model.SavingGoal;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

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
     * Fetches account details, gets start and end dates from the user, retrieves transactions between specific timestamps,
     * calculates the total round-up amount, and manages savings goals.
     * @throws IOException if an I/O error occurs.
     * @throws ApiException if an API error occurs.
     */
    public void runApplication() throws IOException, ApiException {
        // Fetch account details
        Account account = accountService.getAccountDetails();

        // Get the start and end dates from the user
        LocalDate startDate = userInputHandler.readDate("Enter the start date (YYYY-MM-DD): ");
        LocalDate endDate = userInputHandler.readDate("Enter the end date (YYYY-MM-DD): ");

        // Validate that the start date is before the end date
        if (!startDate.isBefore(endDate)) {
            System.out.println("Invalid date range. The start date must be before the end date.");
            return; // Or loop back to ask for the dates again
        }

        // Convert LocalDate to ZonedDateTime at the start of the day in UTC
        ZonedDateTime startDateTime = startDate.atStartOfDay(ZoneOffset.UTC);
        ZonedDateTime endDateTime = endDate.atStartOfDay(ZoneOffset.UTC);

        // Format to the desired string representation
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String formattedStartDate = startDateTime.format(formatter);
        String formattedEndDate = endDateTime.format(formatter);

        // Get transactions between specific timestamps
        List<Transaction> transactions = transactionService.getTransactions(account.getAccountUid(), account.getCategoryUid(), formattedStartDate, formattedEndDate);

        // Calculate the total round-up amount
        int totalRoundUpMinorUnits = calculator.calculateTotalRoundUp(transactions);

        // Manage savings goals
        manageSavingsGoals(account, totalRoundUpMinorUnits);
    }


    /**
     * Manages savings goals.
     * If there are no savings goals, it prompts the user to create one.
     * If there is a round-up amount, it adds it to the savings goal.
     * @param account The account object.
     * @param totalRoundUpMinorUnits The total round-up amount in minor units.
     */
    private void manageSavingsGoals(Account account, int totalRoundUpMinorUnits) {
        List<SavingGoal> savingsGoals = savingsGoalService.getSavingsGoals(account.getAccountUid());
        SavingGoal targetSavingGoal;

        if (savingsGoals.isEmpty()) {
            String goalName = userInputHandler.readString("Enter a name for your savings goal: ");
            double targetAmountPounds = userInputHandler.readDouble("Enter your target amount in pounds: ");
            Amount targetAmount = new Amount((int) (targetAmountPounds * 100), "GBP"); // Assuming the currency is always GBP

            targetSavingGoal = savingsGoalService.createSavingsGoal(account, goalName, targetAmount);
        } else {
            targetSavingGoal = savingsGoals.getFirst(); // Assuming you want to use the first savings goal
        }

        if (totalRoundUpMinorUnits > 0) {
            Amount roundUpAmount = new Amount(totalRoundUpMinorUnits, "GBP");
            System.out.println("Total round-up amount to transfer: " + roundUpAmount.format(Locale.UK));
            savingsGoalService.addMoneyToSavingsGoal(account, targetSavingGoal, roundUpAmount);
        } else {
            System.out.println("No round-up amount to transfer.");
        }
    }
}
