package com.example.fitness;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private static final Scanner scanner = new Scanner(System.in);
    public static String USERS_FILE = "C:\\Users\\Sewar\\git\\gym\\src\\main\\java\\com\\example\\fitness\\users";
    public static String USERS_TEMP_FILE = "C:\\Users\\Sewar\\git\\gym\\src\\main\\java\\com\\example\\fitness\\users_temp.txt";
    public static String ARTICLES_FILE = "C:\\Users\\Sewar\\git\\gym\\src\\main\\java\\com\\example\\fitness\\articles";
    protected static final String LOGS_FILE = "C:\\Users\\Sewar\\git\\gym\\src\\main\\java\\com\\example\\fitness\\logs";
    public static String FITNESS_GOALS_FILE = "\"C:\\Users\\Sewar\\git\\gym\\src\\main\\java\\com\\example\\fitness\\fitness_goals";
    public static String FEEDBACK = "C:\\Users\\Sewar\\git\\gym\\src\\main\\java\\com\\example\\fitness\\feedback";
    public static String SUBSCRIPTIONS_FILE = "C:\\Users\\Sewar\\git\\gym\\src\\main\\java\\com\\example\\fitness\\subscriptions";
    public static String SUBSCRIPTIONS_TEMP_FILE = "C:\\Users\\Sewar\\git\\gym\\src\\main\\java\\com\\example\\fitness\\subscriptions_temp";
    public static String PROGRAMS_FILE = "C:\\Users\\Sewar\\git\\gym\\src\\main\\java\\com\\example\\fitness\\programs";
    public static String PROGRAMS_TEMP_FILE = "C:\\Users\\Sewar\\git\\gym\\src\\main\\java\\com\\example\\fitness\\programs_temp";

    public static void displayMenu() {
        int choice;

        logger.log(Level.INFO, "\u001B[36m------ Welcome to the Gym System ------\n" +
            "|                                    |\n" +
            "|          1. Register              |\n" +
            "|          2. Login                 |\n" +
            "|          3. About The Program     |\n" +
            "|          4. Exit                  |\n" +
            "|                                    |\n" +
            "--------------------------------------\n" +
            "\u001B[0m");


        logger.log(Level.INFO, "\u001B[32mEnter your choice: \u001B[0m");

        while (true) {
            try {
                choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        Registration.signupMenu();
                        break;
                    case 2:
                        Login.loginMenu();
                        break;
                    case 3:
                        aboutUs();
                        displayMenu();
                        break;
                        
                    case 4:
                        logger.log(Level.INFO, "\u001B[33mThank you for using the Gym System. Goodbye!\u001B[0m");
                        System.exit(0);
                        break;
                    default:
                        logger.log(Level.WARNING, "\u001B[31mInvalid choice! Please select a valid option.\u001B[0m");
                }
                break; // Exit the loop after a valid choice
            } catch (NumberFormatException e) {
                logger.log(Level.WARNING, "\u001B[31mInvalid input! Please enter a number.\u001B[0m");
            }
      }

    }
    
    public static String  aboutUs() {
        logger.log(Level.INFO, "\u001B[36m------ About Us ------\u001B[0m");
        logger.log(Level.INFO, "\u001B[32mWelcome to the Gym Management System.\n" +
                "Our goal is to provide an efficient, user-friendly platform for gym clients, instructors, and admins.\n" +
                "With this system, clients can easily book classes, read informative articles, and manage their profiles.\n" +
                "Instructors can create articles and interact with clients.\n" +
                "Admins manage users, content, and more.\n" +
                "Thank you for using our system!\u001B[0m");
		return 
			             "\u001B[36m------ About Us ------\u001B[0m\n" +
			             "\u001B[32mWelcome to the Gym Management System.\n" +
			             "Our goal is to provide an efficient, user-friendly platform for gym clients, instructors, and admins.\n" +
			             "With this system, clients can easily book classes, read informative articles, and manage their profiles.\n" +
			             "Instructors can create articles and interact with clients.\n" +
			             "Admins manage users, content, and more.\n" +
			             "Thank you for using our system!\u001B[0m\n";
    }
    
    
    public static String hashPassword(String password) {
        if (password == null) {
            return null; // Return null for null input
        }
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



    public static void main(String[] args) {
        displayMenu();
    }
}
