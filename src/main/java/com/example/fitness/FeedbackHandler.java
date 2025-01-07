package com.example.fitness;

import java.io.*;
import java.util.Scanner;

public class FeedbackHandler {

    public static void sendFeedback(String username, String userType) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter " + userType + "'s username: ");
        // Trim here to prevent trailing whitespace from messing up comparisons
        String instructorUsername = scanner.nextLine().trim();

        System.out.print("Enter your feedback message: ");
        // Trim here too, so "Hello instructor, nice session.\n"
        // won't include trailing newline/spaces
        String message = scanner.nextLine().trim();

        String feedbackId = String.valueOf(System.currentTimeMillis());
        String creationDate = java.time.LocalDate.now().toString();

        // Format => ID, instructorUsername, clientUsername, message, creationDate
        String feedback = String.format(
            "%s,%s,%s,%s,%s",
            feedbackId, instructorUsername, username, message, creationDate
        );

        File file = new File(Main.FEEDBACK);
        try (FileWriter writer = new FileWriter(file, true)) {
            writer.write(feedback + "\n");
            System.out.println("Feedback sent successfully!");
        } catch (IOException e) {
            System.out.println("Error writing feedback: " + e.getMessage());
        }
    }

    public static void viewAllFeedback(String username) {
        File file = new File(Main.FEEDBACK);
        if (!file.exists()) {
            System.out.println("No feedback file found.");
            return;
        }

        boolean foundAny = false;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] feedbackData = line.split(",", 5);
                if (feedbackData.length < 5) continue;

                String feedbackId         = feedbackData[0];
                String instructorUsername = feedbackData[1];
                String clientUsername     = feedbackData[2];
                String message           = feedbackData[3];
                String creationDate      = feedbackData[4];

                // Show feedback if user is either the instructor or the client
                if (clientUsername.equals(username) || instructorUsername.equals(username)) {
                    System.out.println("Feedback ID: " + feedbackId);
                    System.out.println("Instructor: " + instructorUsername);
                    System.out.println("Client: " + clientUsername);
                    System.out.println("Message: " + message);
                    System.out.println("Date: " + creationDate);
                    System.out.println("-------------------------------------");
                    foundAny = true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading feedback file: " + e.getMessage());
            return;
        }

        if (!foundAny) {
            System.out.println("No feedback found for this user.");
        }
    }

    public static void showFeedbackDashboard(String username, String userType) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("------ Feedback Dashboard ------");
            System.out.println("1. View All Feedback");
            System.out.println("2. Add New Feedback");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            // Use nextLine() so each line of test input aligns with each read
            String choiceLine = scanner.nextLine().trim();
            int choice;
            try {
                choice = Integer.parseInt(choiceLine);
            } catch (NumberFormatException e) {
                System.out.println("Invalid choice, please try again.");
                continue;
            }

            switch (choice) {
                case 1:
                    viewAllFeedback(username);
                    break;
                case 2:
                    sendFeedback(username, userType);
                    break;
                case 3:
                    return; // exit
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }
}
