package com.example.fitness;
import java.io.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SystemLogs {


	 private static final Logger logger = Logger.getLogger(SystemLogs.class.getName());

	    public static void manageLogs() {
	        Scanner scanner = new Scanner(System.in);
	        while (true) {
	            logger.log(Level.INFO, "\u001B[36m------ System Logs Management ------\u001B[0m\n" +
	                    "\u001B[32m1. View Logs\n" +
	                    "2. Search Logs\n" +
	                    "3. Clear Logs\n" +
	                    "4. Exit\u001B[0m");
	            logger.log(Level.INFO, "\u001B[33mChoose an option: \u001B[0m");


	            String choice = scanner.nextLine();

	            switch (choice) {
	                case "1":
	                    viewLogs();
	                    break;
	                case "2":
	                    logger.log(Level.INFO, "\u001B[32mEnter keyword to search: \u001B[0m");
	                    String keyword = scanner.nextLine();
	                    searchLogs(keyword);
	                    break;
	                case "3":
	                    clearLogs();
	                    break;
	                case "4":
	                    logger.log(Level.INFO, "\u001B[32mExiting System Logs Management.\u001B[0m");
	                    return;
	                
	                default:
	                    logger.log(Level.WARNING, "\u001B[31mInvalid choice. Please try again.\u001B[0m");
	            }
	        }
	    }

	    private static void viewLogs() {
	        logger.log(Level.INFO, "\u001B[32m--- System Logs ---\u001B[0m");
	        try (BufferedReader reader = new BufferedReader(new FileReader(Main.LOGS_FILE))) {
	            String line;
	            while ((line = reader.readLine()) != null) {
	                System.out.println(line);
	            }
	        } catch (IOException e) {
	            logger.log(Level.SEVERE, "\u001B[31mError reading logs file: {0}\u001B[0m", e.getMessage());
	        }
	    }

	    private static void searchLogs(String keyword) {
	        logger.log(Level.INFO, "\u001B[32m--- Searching Logs for Keyword: {0} ---\u001B[0m", keyword);
	        try (BufferedReader reader = new BufferedReader(new FileReader(Main.LOGS_FILE))) {
	            String line;
	            boolean found = false;
	            while ((line = reader.readLine()) != null) {
	                if (line.contains(keyword)) {
	                    System.out.println(line);
	                    found = true;
	                }
	            }
	            if (!found) {
	                logger.log(Level.INFO, "\u001B[33mNo logs found containing the keyword: {0}\u001B[0m", keyword);
	            }
	        } catch (IOException e) {
	            logger.log(Level.SEVERE, "\u001B[31mError reading logs file: {0}\u001B[0m", e.getMessage());
	        }
	    }

	    private static void clearLogs() {
	        logger.log(Level.INFO, "\u001B[33m--- Clearing Logs ---\u001B[0m");
	        try (FileWriter writer = new FileWriter(Main.LOGS_FILE)) {
	            writer.write("");
	            logger.log(Level.INFO, "\u001B[34mAll logs have been cleared successfully.\u001B[0m");
	        } catch (IOException e) {
	            logger.log(Level.SEVERE, "\u001B[31mError clearing logs file: {0}\u001B[0m", e.getMessage());
	        }
	    }
}
