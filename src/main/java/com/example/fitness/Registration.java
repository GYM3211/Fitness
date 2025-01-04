package com.example.fitness;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Registration {

    private static final Logger logger = Logger.getLogger(Registration.class.getName());

    public static void signupMenu() {
        Scanner scanner = new Scanner(System.in);

        logger.log(Level.INFO, "\u001B[34m------ Registration ------\nPlease enter the following details:\n\u001B[0m");

        // Username
        logger.log(Level.INFO, "\u001B[32mEnter your username: \u001B[0m");
        String username = scanner.nextLine();

        // Password
        logger.log(Level.INFO, "\u001B[32mEnter your password: \u001B[0m");
        String password = scanner.nextLine();

        // User Type
        logger.log(Level.INFO, "\u001B[32mSelect your user type: \n1. Instructor \n2. Client \u001B[0m");
        int userType = Integer.parseInt(scanner.nextLine());

        String role;
        switch (userType) {
            case 1:
                role = "instructor";
                break;
            case 2:
                role = "client";
                break;
            default:
                logger.log(Level.WARNING, "\u001B[31mInvalid user type selected! Returning to the main menu.\u001B[0m");
                Main.displayMenu();
                return;
        }

        // Gender
        logger.log(Level.INFO, "\u001B[32mEnter your gender: \u001B[0m");
        String gender = scanner.nextLine();

        // Age
        logger.log(Level.INFO, "\u001B[32mEnter your age: \u001B[0m");
        int age = Integer.parseInt(scanner.nextLine());
        
        //Subscription
        logger.log(Level.INFO, "\u001B[32mEnter subscription (free/basic/premium/lifetime): \u001B[0m");
        String subscription = scanner.nextLine().toLowerCase();


        // Account Type (default is pending)
        String accountType = "pending";

        // Hash password
        String hashedPassword = hashPassword(password);

        // Save user data
        String userData = username + "," + hashedPassword + "," + role + "," + gender + "," + age + "," + accountType + "," + subscription;
        saveToFile(userData);

        // Log user registration success
        logger.log(Level.INFO, "\u001B[34mUser registered successfully!\nDetails:\nUsername: {0}\nRole: {1}\nAccount Type: {2}\u001B[0m", 
                new Object[]{username, role, accountType});

        // Return to main menu
        Main.displayMenu();
    }

    // Helper method to hash the password using SHA-256
    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Helper method to save user data to the file
    private static void saveToFile(String userData) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter( Main.USERS_FILE, true))) {
            writer.write(userData + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
