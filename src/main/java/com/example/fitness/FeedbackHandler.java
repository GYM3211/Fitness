package com.example.fitness;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Revised FeedbackHandler class without Logger.
 * Uses a static file path for feedback but can be changed for tests.
 */
public class FeedbackHandler {

    // Point to a default file (in production, set to Main.FEEDBACK or another path).
    // In tests, we will override FEEDBACK_FILE with a test-specific file.
    public static String FEEDBACK_FILE = "feedback.csv";

    /**
     * Original method that uses Scanner (for interactive usage).
     * Now replaced System.out.println instead of logger logs.
     */
    public static void sendFeedback(String username, String userType) {
        Scanner scanner = new Scanner(System.in);

        System.out.printf("Enter %s's username: ", userType);
        String instructorUsername = scanner.nextLine();

        System.out.print("Enter your feedback message: ");
        String message = scanner.nextLine();

        String feedbackId = String.valueOf(System.currentTimeMillis());
        String creationDate = LocalDate.now().toString();

        String feedback = String.format("%s,%s,%s,%s,%s", 
                                         feedbackId, instructorUsername, username, message, creationDate);

        try (FileWriter writer = new FileWriter(FEEDBACK_FILE, true)) {
            writer.write(feedback + "\n");
            System.out.println("Feedback sent successfully!");
        } catch (IOException e) {
            System.err.println("Error writing feedback: " + e.getMessage());
            
        }
    }

    /**
     * Overloaded method for testing (no Scanner interaction).
     * You pass all necessary parameters directly.
     */
    public static void sendFeedback(String instructorUsername, 
                                    String clientUsername, 
                                    String message) 
    {
        String feedbackId = String.valueOf(System.currentTimeMillis());
        String creationDate = LocalDate.now().toString();

        String feedbackLine = String.format("%s,%s,%s,%s,%s",
                feedbackId,
                instructorUsername,
                clientUsername,
                message,
                creationDate);

        try (FileWriter writer = new FileWriter(FEEDBACK_FILE, true)) {
            writer.write(feedbackLine + "\n");
        } catch (IOException e) {
            System.err.println("Error writing feedback: " + e.getMessage());
        }
    }

    /**
     * Original viewAllFeedback method (interactive).
     */
    public static void viewAllFeedback(String username) {
        File file = new File(FEEDBACK_FILE);
        if (!file.exists()) {
            System.out.println("No feedback file found.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean found = false;
            while ((line = br.readLine()) != null) {
                String[] feedbackData = line.split(",");
                if (feedbackData.length < 5) continue; // skip malformed lines

                String feedbackId = feedbackData[0];
                String instructorUsername = feedbackData[1];
                String clientUsername = feedbackData[2];
                String message = feedbackData[3];
                String creationDate = feedbackData[4];

                // Show feedback if the user is instructor or client
                if (clientUsername.equals(username) || instructorUsername.equals(username)) {
                    System.out.println("-------------------------------");
                    System.out.println("Feedback ID: " + feedbackId);
                    System.out.println("Instructor: " + instructorUsername);
                    System.out.println("Client: " + clientUsername);
                    System.out.println("Message: " + message);
                    System.out.println("Date: " + creationDate);
                    found = true;
                }
            }
            if (!found) {
                System.out.println("No feedback found for this user.");
            }
        } catch (IOException e) {
            System.err.println("Error reading feedback file: " + e.getMessage());
        }
    }

    /**
     * Overloaded method that returns all feedback (lines) relevant to a username.
     * Useful for testing, so we can assert on the returned data instead of just printing.
     */
    public static List<String> getAllFeedback(String username) {
        List<String> result = new ArrayList<>();
        File file = new File(FEEDBACK_FILE);
        if (!file.exists()) {
            return result; // empty
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] feedbackData = line.split(",");
                if (feedbackData.length < 5) {
                    continue; // skip malformed lines
                }
                String instructorUsername = feedbackData[1];
                String clientUsername = feedbackData[2];
                if (clientUsername.equals(username) || instructorUsername.equals(username)) {
                    result.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading feedback file: " + e.getMessage());
        }
        return result;
    }

    /**
     * Original interactive dashboard (uses System.out.println instead of logger).
     */
    public static void showFeedbackDashboard(String username, String userType) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n------ Feedback Dashboard ------");
            System.out.println("1. View All Feedback");
            System.out.println("2. Add New Feedback");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            int choice = -1;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
            }

            switch (choice) {
                case 1:
                    viewAllFeedback(username);
                    break;
                case 2:
                    sendFeedback(username, userType);
                    break;
                case 3:
                    return; // Exit
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }
}