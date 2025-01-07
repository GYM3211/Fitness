package com.example.fitness;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserManagement {

    private static final Logger logger = Logger.getLogger(UserManagement.class.getName());

    public static void manageUsers() {
        Scanner scanner = new Scanner(System.in);
        int choice;

        while (true) {
            // Display user management options
            logger.log(Level.INFO, "\u001B[36m------ User Management ------\u001B[0m\n" +
                    "\u001B[32m1. View All Users\n" +
                    "2. Approve User\n" +
                    "3. Deny User\n" +
                    "4. Create User\n" +
                    "5. Back to Dashboard\u001B[0m");

            logger.log(Level.INFO, "\u001B[32mPlease select an option: \u001B[0m");
            try {
                choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        viewAllUsers();  // Method to display all users
                        break;
                    case 2:
                        approveUser();  // Method to approve a user
                        break;
                    case 3:
                        denyUser();  // Method to deny a user
                        break;
                    case 4:
                        createUser(); // Methold to create a user
                        break;
                    case 5:
                        logger.log(Level.INFO, "\u001B[33mReturning to Dashboard...\u001B[0m");
                        return;  // Exit the loop and go back to the dashboard
                    default:
                        logger.log(Level.WARNING, "\u001B[31mInvalid option. Please select a valid option.\u001B[0m");
                        break;
                }
            } catch (NumberFormatException e) {
                logger.log(Level.WARNING, "\u001B[31mInvalid input! Please enter a number.\u001B[0m");
            }
        }
    }

    private static void viewAllUsers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(Main.USERS_FILE))) {
            String line;
            logger.log(Level.INFO, "\u001B[36m------ All Registered Users ------\u001B[0m");
            logger.log(Level.INFO, "\u001B[34mUsername\tUser Type\tGender\tAge\tAccount Status\tSubscription\u001B[0m");

            while ((line = reader.readLine()) != null) {
                String[] userDetails = line.split(",");
                if (userDetails.length >= 6) {
                    String username = userDetails[0];
                    String userType = userDetails[2];
                    String gender = userDetails[3];
                    String age = userDetails[4];
                    String accountStatus = userDetails[5];
                    String accountPlan = userDetails[6];

                    logger.log(Level.INFO, "\u001B[32m{0}\t{1}\t{2}\t{3}\t{4}\t{5}\u001B[0m",
                            new Object[]{username, userType, gender, age, accountStatus, accountPlan});
                } else {
                    logger.log(Level.WARNING, "\u001B[31mMalformed user data: {0}\u001B[0m", line);
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "\u001B[31mError reading users file: {0}\u001B[0m", e.getMessage());
        }
    }

    private static void approveUser() {
        Scanner scanner = new Scanner(System.in);
        logger.log(Level.INFO, "\u001B[32mEnter the username to approve: \u001B[0m");
        String usernameToApprove = scanner.nextLine();

        File inputFile = new File(Main.USERS_FILE);
        File tempFile = new File(Main.USERS_TEMP_FILE);

        boolean userFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] userDetails = line.split(",");
                if (userDetails[0].equals(usernameToApprove)) {
                    if ("pending".equalsIgnoreCase(userDetails[5].trim())) {
                        userDetails[5] = "accepted";
                        logger.log(Level.INFO, "\u001B[32mUser has been approved successfully.\u001B[0m", usernameToApprove);
                        userFound = true;
                    } else {
                        logger.log(Level.WARNING, "\u001B[31mUser is not in a pending state.\u001B[0m", usernameToApprove);
                        userFound = true;
                    }
                }
                writer.write(String.join(",", userDetails));
                writer.newLine();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "\u001B[31mError processing the users file: {0}\u001B[0m", e.getMessage());
        }

        if (!userFound) {
            logger.log(Level.WARNING, "\u001B[31mUser not found in the system.\u001B[0m", usernameToApprove);
        }

        // Replace original file with updated file
        if (inputFile.delete() && tempFile.renameTo(inputFile)) {
            logger.log(Level.INFO, "\u001B[32mUser approval changes have been saved successfully.\u001B[0m");
        } else {
            logger.log(Level.SEVERE, "\u001B[31mFailed to save the user approval changes.\u001B[0m");
        }
    }
    
   private static void createUser() {
        Scanner scanner = new Scanner(System.in);
        logger.log(Level.INFO, "\u001B[32mEnter username: \u001B[0m");
        String username = scanner.nextLine();

        logger.log(Level.INFO, "\u001B[32mEnter password: \u001B[0m");
        String password = scanner.nextLine();

        logger.log(Level.INFO, "\u001B[32mEnter role (admin/instructor/client): \u001B[0m");
        String role = scanner.nextLine().toLowerCase();

        logger.log(Level.INFO, "\u001B[32mEnter gender (male/female): \u001B[0m");
        String gender = scanner.nextLine().toLowerCase();

        logger.log(Level.INFO, "\u001B[32mEnter age: \u001B[0m");
        int age;
        try {
            age = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "\u001B[31mInvalid age! User creation aborted.\u001B[0m");
            return;
        }

        logger.log(Level.INFO, "\u001B[32mEnter subscription (free/basic/premium/lifetime): \u001B[0m");
        String subscription = scanner.nextLine().toLowerCase();

        String userStatus = "pending";
        String hashedPassword = Main.hashPassword(password);

        String newUser = String.format("%s,%s,%s,%s,%d,%s,%s", username, hashedPassword, role, gender, age, userStatus, subscription);

        try (FileWriter writer = new FileWriter(Main.USERS_FILE, true)) {
            writer.write(newUser + "\n");
            logger.log(Level.INFO, "\u001B[34mUser created successfully!\u001B[0m");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "\u001B[31mError writing to users file: {0}\u001B[0m", e.getMessage());
        }
}



     private static void denyUser() {
        Scanner scanner = new Scanner(System.in);
        logger.log(Level.INFO, "\u001B[32mEnter the username to deny: \u001B[0m");
        String usernameToDeny = scanner.nextLine();

        File inputFile = new File(Main.USERS_FILE);
        File tempFile = new File(Main.USERS_TEMP_FILE);

        boolean userFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] userDetails = line.split(",");
                if (userDetails[0].equals(usernameToDeny)) {
                    if ("pending".equalsIgnoreCase(userDetails[5].trim()) || "accepted".equalsIgnoreCase(userDetails[5].trim())) {
                        userDetails[5] = "denied";
                        logger.log(Level.INFO, "\u001B[32mUser has been denied successfully.\u001B[0m", usernameToDeny);
                        userFound = true;
                    } else {
                        logger.log(Level.WARNING, "\u001B[31mUser is not in a pending state.\u001B[0m", usernameToDeny);
                        userFound = true;
                    }
                }
                writer.write(String.join(",", userDetails));
                writer.newLine();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "\u001B[31mError processing the users file: {0}\u001B[0m", e.getMessage());
        }

        if (!userFound) {
            logger.log(Level.WARNING, "\u001B[31mUser not found in the system.\u001B[0m", usernameToDeny);
        }

        // Replace original file with updated file
        if (inputFile.delete() && tempFile.renameTo(inputFile)) {
            logger.log(Level.INFO, "\u001B[32mUser denial changes have been saved successfully.\u001B[0m");
        } else {
            logger.log(Level.SEVERE, "\u001B[31mFailed to save the user denial changes.\u001B[0m");
        }
    }

}
