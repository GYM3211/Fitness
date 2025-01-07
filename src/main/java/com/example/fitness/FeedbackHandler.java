package com.example.fitness;
import java.io.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FeedbackHandler {
    private static final Logger logger = Logger.getLogger(FeedbackHandler.class.getName());

    public static void sendFeedback(String username , String userType) {
        Scanner scanner = new Scanner(System.in);
        logger.log(Level.INFO, "\u001b[32mEnter {0}''s username: \u001b[0m", userType);
        String instructorUsername = scanner.nextLine();

        logger.log(Level.INFO, "\u001B[32mEnter your feedback message: \u001B[0m");
        String message = scanner.nextLine();

        String feedbackId = String.valueOf(System.currentTimeMillis()); // Unique ID based on current time
        String creationDate = java.time.LocalDate.now().toString();

        String feedback = String.format("%s,%s,%s,%s,%s", feedbackId, instructorUsername, username, message, creationDate);

        try (FileWriter writer = new FileWriter(Main.FEEDBACK, true)) {
            writer.write(feedback + "\n");
            logger.log(Level.INFO, "\u001B[34mFeedback sent successfully! \u001B[0m");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "\u001B[31mError writing feedback: {0}\u001B[0m", e.getMessage());
        }
    }

   public static void viewAllFeedback(String username) {
        try (BufferedReader br = new BufferedReader(new FileReader(Main.FEEDBACK))) {
            String line;
            boolean found = false;
            while ((line = br.readLine()) != null) {
                String[] feedbackData = line.split(",");
                String feedbackId = feedbackData[0];
                String instructorUsername = feedbackData[1];
                String clientUsername = feedbackData[2];
                String message = feedbackData[3];
                String creationDate = feedbackData[4];

                if (clientUsername.equals(username) || instructorUsername.equals(username)) {
                    logger.log(Level.INFO, "\n\u001B[36mFeedback ID: {0}\nInstructor: {1}\nClient: {2}\nMessage: {3}\nDate: {4}\n", 
                            new Object[]{feedbackId, instructorUsername, clientUsername, message, creationDate});
                    found = true;
                }
            }
            if (!found) {
                logger.log(Level.INFO, "\u001B[33mNo feedback found for this user.\u001B[0m");
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "\u001B[31mError reading feedback file: {0}\u001B[0m", e.getMessage());
        }
    }

    
    public static void showFeedbackDashboard(String username,String userType) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            logger.log(Level.INFO, "\u001B[36m------ Feedback Dashboard ------\u001B[0m\n" +
                    "\u001B[32m1. View All Feedback\n" +
                    "2. Add New Feedback\n" +
                    "3. Exit\n" +
                    "\u001B[33mChoose an option: \u001B[0m");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume the newline

            switch (choice) {
                case 1:
                    FeedbackHandler.viewAllFeedback(username);
                    break;
                case 2:
                    FeedbackHandler.sendFeedback(username, userType);
                    break;
                case 3:
                    return; // Exit
                default:
                    logger.log(Level.WARNING, "\u001B[31mInvalid choice, please try again.\u001B[0m");
            }
        }
    }
}
