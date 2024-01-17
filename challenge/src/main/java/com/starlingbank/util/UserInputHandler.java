package com.starlingbank.util;

import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class UserInputHandler {
    private Scanner scanner;

    public UserInputHandler() {
        this.scanner = new Scanner(System.in);
    }

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

    public String readString(String prompt) {
        System.out.println(prompt);
        return scanner.nextLine();
    }


    public double readDouble(String prompt) {
        System.out.println(prompt);
        while (!scanner.hasNextDouble()) {
            System.out.println("Invalid input. Please enter a numeric value.");
            scanner.next(); // Clear the invalid input
        }
        return scanner.nextDouble();
    }

    public void close() {
        scanner.close();
    }
}
