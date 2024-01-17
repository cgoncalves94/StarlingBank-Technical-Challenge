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

public class ApplicationRunner {
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final SavingsGoalService savingsGoalService;
    private final RoundUpCalculator calculator;
    private final UserInputHandler userInputHandler;

    public ApplicationRunner(AccountService accountService, TransactionService transactionService, SavingsGoalService savingsGoalService, RoundUpCalculator calculator, UserInputHandler userInputHandler) {
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.savingsGoalService = savingsGoalService;
        this.calculator = calculator;
        this.userInputHandler = userInputHandler;
    }

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
