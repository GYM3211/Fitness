package com.example.fitness;


import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Login {

    private static final Logger logger = Logger.getLogger(Login.class.getName());
    
    private static String userType = "";

    public static void loginMenu() {
    	
        Scanner scanner = new Scanner(System.in);

        logger.log(Level.INFO, "\u001B[34m------ Login ------\nPlease enter your credentials:\n\u001B[0m");


        logger.log(Level.INFO, "\u001B[32mEnter your username: \u001B[0m");
        String username = scanner.nextLine();

        logger.log(Level.INFO, "\u001B[32mEnter your password: \u001B[0m");
        String password = scanner.nextLine();

        // Placeholder for user authentication logic
        boolean isAuthenticated = authenticateUser(username, password);

        if (isAuthenticated) {
            logger.log(Level.INFO, "\u001B[32mLogin successful! Redirecting to your dashboard...\u001B[0m");
            // Redirect to specific user dashboard based on role
            redirectToDashboard(username,userType);
        } else {
            Main.displayMenu();
        }
    }
    
        // Authentication Method
    private static boolean authenticateUser(String username, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(Main.USERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] userDetails = line.split(",");
                String storedUsername = userDetails[0].trim();
                String storedPasswordHash = userDetails[1].trim();
                String accountStatus = userDetails[5].trim();

                if (storedUsername.equals(username)) {
                    if (storedPasswordHash.equals(Main.hashPassword(password))) { // Consistent hashing
                        userType = userDetails[2];
                        // Check the account status
                        if (accountStatus.equals("pending")) {
                            logger.log(Level.INFO, "\u001B[33mYour account is still pending approval.\u001B[0m");
                            return false;
                        } else if (accountStatus.equals("denied")) {
                            logger.log(Level.WARNING, "\u001B[31mYour account has been denied.\u001B[0m");
                            return false;
                        } else if (accountStatus.equals("accepted")) {
                            logger.log(Level.INFO, "\u001B[32mWelcome! Redirecting...\u001B[0m");
                            return true; // Authentication successful
                        }
                    }
                    logger.log(Level.WARNING, "\u001B[31mInvalid password! Please try again.\u001B[0m");
                    return false;
                }
            }
            logger.log(Level.WARNING, "\u001B[31mInvalid username! Please try again.\u001B[0m");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false; // Authentication failed
    }    


    private static void redirectToDashboard(String username, String userType) {
     // Check user role and redirect accordingly
     if (userType.toLowerCase().equals("admin")) {
         logger.log(Level.INFO, "\u001B[34mRedirecting Admin: {0} to the Admin Dashboard...\u001B[0m", username);
          AdminDashboard.showDashboardOptions(username);
         // Additional logic to handle Admin options
     } else if (userType.toLowerCase().equals("instructor")) {
         logger.log(Level.INFO, "\u001B[34mRedirecting Instructor: {0} to the Instructor Dashboard...\u001B[0m", username);
         InstructorDashboard.showDashboard(username);
         // Redirect to instructor dashboard
     } else if (userType.toLowerCase().equals("client")) {
         logger.log(Level.INFO, "\u001B[34mRedirecting Client: {0} to the Client Dashboard...\u001B[0m", username);
         ClientDashboard.showDashboard(username);
         // Redirect to client dashboard
     } else {
         logger.log(Level.WARNING, "\u001B[31mInvalid user type: {0}. Cannot redirect.\u001B[0m", userType);
         
     }
    }

}
