package com.goncalves;

import java.io.IOException;
import java.util.Scanner;
import org.json.JSONObject;
import org.json.JSONArray;
import com.goncalves.api.StarlingClient;
import com.goncalves.service.RoundUpCalculator;

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
    */
    public static void main(String[] args) throws IOException {
        // Create a scanner to read user input
        Scanner scanner = new Scanner(System.in);

        // Create a Starling client with the access token
        StarlingClient client = new StarlingClient("eyJhbGciOiJQUzI1NiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAA_21TQZKbMBD8SorzzhYWsqG45ZYP5AEjzWCrFiRKEt5spfL3CATGuPZGd8_09Ejib2FCKNoCRwPEg3sPEX1v7FWh_XjXbijeijCpVKFEVZ3quoaGKwlSKgGKawHYnGXJVYkVUSrmP2PRnuryLGXTVOKtMBgzUV2EnAnU2k02_nI9sf9taPUua6mgO5UE8iySt2jOIFSJqiq1uNSYvKP7YLt3cFMR8KVsUseFIQVgUPp8Yi06gWpOk9b6qTWHsHdhxwpYNiVIwamrrhmoqZNbc-mqk5gX1m7k-VByUrgtUcHiwK1npB8vQvwaXwRDbKPpDPsj35sQD8wKiHwK2TKZ-ABZiRH1beBH5RRvzpuQbgiMJXM3NGGfNYU9Wr0m0egJtLPRuz77zsyqOdsZP2A0zoLroJssrfP0FKIbttg8oFm7B7SEkVviniM_4FI2cMSEsNUJzuKGl84Rv5g3KYPVJIO9CMyA19Uza_snRI82oJ4zP2jonU7b796ZADcfwyu7dnnXmX4blWcfqKXKs2YzxgMIR-nTm-Qe8J6uIsDV7TkO3LrqgVt8npm8XJeO_RuLXfzGaxezqb4xTT0TpLX3VxM4xrTgNK5wxO2ZpN89vaL0mJynp_FHdpt7ZL_pB_dpH3zkOQDocH-lRuoy9Xyny1W8XnLx7z8wwjWXoQQAAA.g25IU0a39Sj2Dta9kQHpwzR8Iuszm1D1AAInKdc10i1A48HkG9Jj8YwEtwpbnhfdZfJubIzk187_6r6eq6xr8FzjvILmpDJTpznzM2Unnny3VbQiQn-1gQyqmnzX47dq50tCwd4XRUistlriJYQp8sYE57QvNQRmDk-2BhqkhoNc4UgPnQnstjSDqk3x67irmem4y0GYYwCv2vq4CHdbU10IRr8dPwpom9GK00PA1OhueyuffnLsiDLtXe3AoRIL8bikp5MXFTnwrl-Z3yAzrbHkGbW2rh0xIzq3gZWUzrv4bVemEhcqUPaZVezZhf7fuJ_2pIGsuKTnx86feHsDVECipl4Zvd5txXAAiUzc_HczQ3VuXzN51QVGz7XSvrWZc28MiBb9Dj6h2U1mzBWHoejJVfVHUDGea7LqToPTOF-koD2nOfSNBsbqyWhTUj5zlstg3dDzgzJIhN4luX-vHHIaxhXxr5YbAINApNOoDZ73fBl61iv0I3El0GEvIt45piThJvkz3mSy2C4byn6ey2CHqG9r0l1RNlKFv_ChD6zwQ3NYa9ksDMDrFHDDdirpAx_yqxHrEuk-y51CwIJVuniMRgvWzQ6eZIQhH_O_QF3QzsJu2tOKSCkx6hyQumCECCOJ8yhGeb24IvHKJ3SA-xFOamK-fI0h4KI3Zp-hbsY");

        // Get account details
        String accountDetailsJson = client.getAccountDetails();
        JSONObject accountDetails = new JSONObject(accountDetailsJson);
        JSONArray accounts = accountDetails.getJSONArray("accounts");
        String accountUid = accounts.getJSONObject(0).getString("accountUid");
        String categoryUid = accounts.getJSONObject(0).getString("defaultCategory");

        // Get transactions between specific timestamps
        System.out.println("Enter the start date (YYYY-MM-DD): ");
        String startDate = scanner.nextLine() + "T00:00:00Z";
        System.out.println("Enter the end date (YYYY-MM-DD): ");
        String endDate = scanner.nextLine() + "T00:00:00Z";
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
