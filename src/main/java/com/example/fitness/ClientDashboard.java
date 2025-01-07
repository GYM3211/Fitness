package com.example.fitness;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ClientDashboard {

    public static void showDashboard(String username) {
        Scanner scanner = new Scanner(System.in);
        int choice;

        while (true) {
            System.out.println("------ Client Dashboard ------");
            System.out.println("1. View Profile");
            System.out.println("2. Read an Article");
            System.out.println("3. Fitness Goals");
            System.out.println("4. Change Subscription");
            System.out.println("5. Programs Dashboard");
            System.out.println("6. Feedback");
            System.out.println("7. Logout");
            System.out.print("Enter your choice: ");

            try {
                choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        System.out.println("Viewing profile...");
                        viewProfile(username);
                        break;
                    case 2:
                        System.out.println("Reading an article...");
                        readArticles();
                        break;
                    case 3:
                        System.out.println("Displaying fitness goals options...");
                        showFitnessGoalDashboard(username);
                        break;
                    case 4:
                        System.out.println("Changing subscription...");
                        changeSubscription(username);
                        break;
                    case 5:
                        System.out.println("Viewing programs dashboard...");
                        ProgramDashboardClient.showProgramsDashboard(username);
                        break;
                    case 6:
                        System.out.println("Viewing Feedback...");
                        // If your FeedbackHandler code is also logger-based, you can similarly adapt it
                        FeedbackHandler.showFeedbackDashboard(username, "Instructor");
                        break;
                    case 7:
                        System.out.println("Logging out... Goodbye, " + username + "!");
                        return; // Exit the dashboard
                    default:
                        System.out.println("Invalid option! Please select a valid option.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
            }
        }
    }

    public static void viewProfile(String username) {
        File file = new File(Main.USERS_FILE);
        if (!file.exists()) {
            System.out.println("No user data file found. Cannot view profile.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] userDetails = line.split(",");
                if (userDetails[0].trim().equals(username)) {
                    System.out.println("Profile Details:");
                    System.out.println("Username: " + userDetails[0].trim());
                    System.out.println("Role: " + userDetails[2].trim());
                    System.out.println("Gender: " + userDetails[3].trim());
                    System.out.println("Age: " + userDetails[4].trim());
                    System.out.println("Status: " + userDetails[5].trim());
                    System.out.println("Subscription: " + userDetails[6].trim());
                    return;
                }
            }
            System.out.println("No profile found for username: " + username);
        } catch (IOException e) {
            System.out.println("Error reading user data: " + e.getMessage());
        }
    }

    public static void readArticles() {
        List<String[]> articles = new ArrayList<>();
        File file = new File(Main.ARTICLES_FILE);
        if (!file.exists()) {
            System.out.println("No articles file found.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] articleDetails = line.split(",", 5);
                if (articleDetails.length >= 5) {
                    articles.add(articleDetails);
                    System.out.println("ID: " + articleDetails[0] + " - Title: " + articleDetails[1]);
                }
            }

            if (articles.isEmpty()) {
                System.out.println("No articles available at the moment.");
                return;
            }

            // Ask the client to choose an article by ID
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the article ID to read full details: ");
            String articleId = scanner.nextLine();

            // Find and display the selected article
            boolean found = false;
            for (String[] article : articles) {
                if (article[0].equals(articleId)) {
                    System.out.println("---- Article Details ----");
                    System.out.println("ID: " + article[0]);
                    System.out.println("Title: " + article[1]);
                    System.out.println("Author: " + article[2]);
                    System.out.println("Publish Date: " + article[3]);
                    System.out.println("Content: " + article[4]);
                    found = true;
                    break;
                }
            }

            if (!found) {
                System.out.println("Article with ID " + articleId + " not found.");
            }
        } catch (IOException e) {
            System.out.println("Error reading articles file: " + e.getMessage());
        }
    }

    private static void showFitnessGoalDashboard(String username) {
        Scanner scanner = new Scanner(System.in);
        int choice;
        while (true) {
            System.out.println("------ Fitness Goals Dashboard ------");
            System.out.println("1. View My Fitness Goals");
            System.out.println("2. Add a New Fitness Goal");
            System.out.println("3. Delete a Fitness Goal");
            System.out.println("4. Back to Dashboard");
            System.out.print("Choose an option: ");

            try {
                choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        FitnessGoals.viewFitnessGoals(username);
                        break;
                    case 2:
                        FitnessGoals.addFitnessGoal(username);
                        break;
                    case 3:
                        FitnessGoals.deleteFitnessGoal(username);
                        break;
                    case 4:
                        System.out.println("Returning to Dashboard...");
                        return;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
            }
        }
    }

    public static void changeSubscription(String username) {
        // Read user data from file and find the user's current subscription status
        String currentSubscription = getCurrentSubscription(username);
        if (currentSubscription == null) {
            System.out.println("No subscription found or user not found. Cannot change subscription.");
            return;
        }

        System.out.println("Your current subscription: " + currentSubscription);
        System.out.println("Available subscriptions: \n1. Free\n2. Basic\n3. Premium\n4. Lifetime");
        System.out.print("Enter the number of the subscription you want to switch to: ");

        Scanner scanner = new Scanner(System.in);
        String newSubscription = null;
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1:
                    newSubscription = "Free";
                    break;
                case 2:
                    newSubscription = "Basic";
                    break;
                case 3:
                    newSubscription = "Premium";
                    break;
                case 4:
                    newSubscription = "Lifetime";
                    break;
                default:
                    System.out.println("Invalid option! Subscription not changed.");
                    return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a number.");
            return;
        }

        // Update the user's subscription in the file
        updateSubscription(username, newSubscription);
        System.out.println("Your subscription has been changed to: " + newSubscription);
    }

    private static String getCurrentSubscription(String username) {
        File file = new File(Main.USERS_FILE);
        if (!file.exists()) {
            return null;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] userDetails = line.split(",");
                if (userDetails[0].trim().equals(username)) {
                    // Assuming subscription is at index 6
                    return userDetails[6].trim();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;  // If user not found or file read fails
    }

    private static void updateSubscription(String username, String newSubscription) {
        File file = new File(Main.USERS_FILE);
        File tempFile = new File(Main.USERS_TEMP_FILE);
        if (!file.exists()) {
            System.out.println("Users file not found. Cannot update subscription.");
            return;
        }

        try (
            BufferedReader reader = new BufferedReader(new FileReader(file));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userDetails = line.split(",");
                if (userDetails[0].trim().equals(username)) {
                    userDetails[6] = newSubscription;  // Update subscription
                    line = String.join(",", userDetails);
                }
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error updating subscription: " + e.getMessage());
        }

        // Replace original file with updated file
        if (!file.delete()) {
            System.out.println("Error deleting original users file.");
        } else if (!tempFile.renameTo(file)) {
            System.out.println("Error renaming temporary file to users file.");
        }
    }
}
