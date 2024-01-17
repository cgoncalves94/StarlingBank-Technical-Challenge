package com.starlingbank.util;

import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * The UserInputHandler class is responsible for handling user inputs.
 * It provides methods to read different types of inputs from the user.
 */
public class UserInputHandler {
    // Scanner object to read user input
    private final Scanner scanner;

    /**
     * Constructor for UserInputHandler.
     * Initializes the scanner object.
     */
    public UserInputHandler() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Reads a date input from the user.
     * Prompts the user until a valid date is entered.
     * @param prompt The prompt to display to the user.
     * @return The date entered by the user.
     */
    public LocalDate readDate(String prompt) {
        while (true) {
            System.out.println(prompt);
            String input = scanner.nextLine();
            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please enter dates in the format YYYY-MM-DD.");
            }
        }
    }

    /**
     * Reads a string input from the user.
     * @param prompt The prompt to display to the user.
     * @return The string entered by the user.
     */
    public String readString(String prompt) {
        System.out.println(prompt);
        return scanner.nextLine();
    }

    /**
     * Reads a double input from the user.
     * Prompts the user until a valid double is entered.
     * @param prompt The prompt to display to the user.
     * @return The double entered by the user.
     */
    public double readDouble(String prompt) {
        System.out.println(prompt);
        while (!scanner.hasNextDouble()) {
            System.out.println("Invalid input. Please enter a numeric value.");
            scanner.next(); // Clear the invalid input
        }
        return scanner.nextDouble();
    }

}
