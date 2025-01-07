package com.example.fitness;

import java.io.*;
import java.util.Scanner;

public class UserManagement {

    public static void manageUsers() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            // Display user management options
            System.out.println("------ User Management ------");
            System.out.println("1. View All Users");
            System.out.println("2. Approve User");
            System.out.println("3. Deny User");
            System.out.println("4. Create User");
            System.out.println("5. Back to Dashboard");
            System.out.print("Please select an option: ");

            String input = scanner.nextLine();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    viewAllUsers();
                    break;
                case 2:
                    approveUser();
                    break;
                case 3:
                    denyUser();
                    break;
                case 4:
                    createUser();
                    break;
                case 5:
                    System.out.println("Returning to Dashboard...");
                    return;
                default:
                    System.out.println("Invalid option. Please select a valid option.");
                    break;
            }
        }
    }

    public static void viewAllUsers() {
        File file = new File(Main.USERS_FILE);
        if (!file.exists()) {
            System.out.println("No users file found.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            System.out.println("------ All Registered Users ------");
            System.out.println("Username\tUser Type\tGender\tAge\tAccount Status\tSubscription");

            String line;
            while ((line = reader.readLine()) != null) {
                String[] userDetails = line.split(",");
                if (userDetails.length >= 7) {
                    String username = userDetails[0];
                    String userType = userDetails[2];
                    String gender = userDetails[3];
                    String age = userDetails[4];
                    String accountStatus = userDetails[5];
                    String accountPlan = userDetails[6];

                    System.out.println(username + "\t" + userType + "\t" + gender
                            + "\t" + age + "\t" + accountStatus + "\t" + accountPlan);
                } else {
                    System.out.println("Malformed user data: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading users file: " + e.getMessage());
        }
    }

    public static void approveUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the username to approve: ");
        String usernameToApprove = scanner.nextLine();

        File inputFile = new File(Main.USERS_FILE);
        File tempFile = new File(Main.USERS_TEMP_FILE);

        if (!inputFile.exists()) {
            System.out.println("Users file does not exist.");
            return;
        }

        boolean userFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] userDetails = line.split(",");
                if (userDetails.length >= 6 && userDetails[0].equals(usernameToApprove)) {
                    userFound = true;
                    // Check if user is pending
                    if ("pending".equalsIgnoreCase(userDetails[5].trim())) {
                        userDetails[5] = "accepted";
                        System.out.println("User has been approved successfully.");
                    } else {
                        System.out.println("User is not in a pending state.");
                    }
                }
                writer.write(String.join(",", userDetails));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error processing the users file: " + e.getMessage());
        }

        if (!userFound) {
            System.out.println("User not found in the system.");
        }

        // Replace original file with updated file
        if (inputFile.delete() && tempFile.renameTo(inputFile)) {
            System.out.println("User approval changes have been saved successfully.");
        } else {
            System.out.println("Failed to save the user approval changes.");
        }
    }

    public static void denyUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the username to deny: ");
        String usernameToDeny = scanner.nextLine();

        File inputFile = new File(Main.USERS_FILE);
        File tempFile = new File(Main.USERS_TEMP_FILE);

        if (!inputFile.exists()) {
            System.out.println("Users file does not exist.");
            return;
        }

        boolean userFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] userDetails = line.split(",");
                if (userDetails.length >= 6 && userDetails[0].equals(usernameToDeny)) {
                    userFound = true;
                    // If user is pending or accepted, set to denied
                    String status = userDetails[5].trim();
                    if ("pending".equalsIgnoreCase(status) || "accepted".equalsIgnoreCase(status)) {
                        userDetails[5] = "denied";
                        System.out.println("User has been denied successfully.");
                    } else {
                        System.out.println("User is not in a pending state.");
                    }
                }
                writer.write(String.join(",", userDetails));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error processing the users file: " + e.getMessage());
        }

        if (!userFound) {
            System.out.println("User not found in the system.");
        }

        // Replace original file with updated file
        if (inputFile.delete() && tempFile.renameTo(inputFile)) {
            System.out.println("User denial changes have been saved successfully.");
        } else {
            System.out.println("Failed to save the user denial changes.");
        }
    }

    public static void createUser() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        System.out.print("Enter role (admin/instructor/client): ");
        String role = scanner.nextLine().toLowerCase();

        System.out.print("Enter gender (male/female): ");
        String gender = scanner.nextLine().toLowerCase();

        System.out.print("Enter age: ");
        int age;
        try {
            age = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid age! User creation aborted.");
            return;
        }

        System.out.print("Enter subscription (free/basic/premium/lifetime): ");
        String subscription = scanner.nextLine().toLowerCase();

        String userStatus = "pending";
        String hashedPassword = Main.hashPassword(password); // We'll assume this is implemented

        String newUser = String.format("%s,%s,%s,%s,%d,%s,%s",
                username, hashedPassword, role, gender, age, userStatus, subscription);

        try (FileWriter writer = new FileWriter(Main.USERS_FILE, true)) {
            writer.write(newUser + "\n");
            System.out.println("User created successfully!");
        } catch (IOException e) {
            System.out.println("Error writing to users file: " + e.getMessage());
        }
    }

}
