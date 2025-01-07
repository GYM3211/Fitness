package com.example.fitness;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminDashboard {

    private static final Logger logger = Logger.getLogger(AdminDashboard.class.getName());

    public static void showDashboardOptions(String username) {
        Scanner scanner = new Scanner(System.in);
        int choice;

        while (true) {
            // Display options
            logger.log(Level.INFO, "\u001B[36m------ Admin Dashboard ------\u001B[0m\n" +
                    "\u001B[32m1. Users Management\n" +
                    "2. Content Management\n" +
                    "3. System Logs\n" +
                    "4. Logout/Exit\u001B[0m");

            logger.log(Level.INFO, "\u001B[32mPlease select an option: \u001B[0m");
            try {
                choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        UserManagement.manageUsers();
                        break;
                    case 2:
                        ContentManagement.manageContent(username);
                        break;
                    case 3:
                        SystemLogs.manageLogs();
                        break;
                    case 4:
                        logger.log(Level.INFO, "\u001B[33mLogging out...\u001B[0m");
                        return;  // Exit the loop and return to login
                    default:
                        logger.log(Level.WARNING, "\u001B[31mInvalid option. Please select a valid option.\u001B[0m");
                        break;
                }
            } catch (NumberFormatException e) {
                logger.log(Level.WARNING, "\u001B[31mInvalid input! Please enter a number.\u001B[0m");
            }
        }
    }
}
