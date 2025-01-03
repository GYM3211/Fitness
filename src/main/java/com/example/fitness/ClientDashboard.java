package com.example.fitness;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientDashboard {
	 private static final Logger logger = Logger.getLogger(ClientDashboard.class.getName());

	    public static void showDashboard(String username) {
	        Scanner scanner = new Scanner(System.in);
	        int choice;

	        while (true) {
	            logger.log(Level.INFO, "\u001B[36m------ Client Dashboard ------\n" +
	                    "1. View Profile\n" +
	                    "2. Read an Article\n" +
	                    "3. Change Subscription\n" +
	                    "4. Logout\n" +
	                    "\u001B[0m");
	            logger.log(Level.INFO, "\u001B[32mEnter your choice: \u001B[0m");

	            try {
	                choice = Integer.parseInt(scanner.nextLine());
	                switch (choice) {
	                    case 1 : {
	                        logger.log(Level.INFO, "\u001B[34mViewing profile...\u001B[0m");
	                        viewProfile(username);
	                        break;
	                    }
	                    case 2 : {
	                        logger.log(Level.INFO, "\u001B[34mReading an article...\u001B[0m");
	                        readArticles();
	                        break;
	                    }
	                    case 3 : {
	                        logger.log(Level.INFO, "\u001B[34mChanging subscription...\u001B[0m");
	                        changeSubscription(username);
	                        break;
	                    }
	                    case 4 : {
	                        logger.log(Level.INFO, "\u001B[33mLogging out... Goodbye, {0}!\u001B[0m", new Object[]{username});
	                        return; // Exit the dashboard
	                    }
	                    default : logger.log(Level.WARNING, "\u001B[31mInvalid option! Please select a valid option.\u001B[0m");
	                }
	            } catch (NumberFormatException e) {
	                logger.log(Level.WARNING, "\u001B[31mInvalid input! Please enter a number.\u001B[0m");
	            }
	        }
	    }

	    private static void viewProfile(String username) {
	        try (BufferedReader br = new BufferedReader(new FileReader(Main.USERS_FILE))) {
	            String line;
	            while ((line = br.readLine()) != null) {
	                String[] userDetails = line.split(",");
	                if (userDetails[0].trim().equals(username)) {
	                    logger.log(Level.INFO, "\u001B[34mProfile Details:\u001B[0m");
	                    logger.log(Level.INFO, "\u001B[34mUsername: {0}\nRole: {1}\nGender: {2}\nAge: {3}\nStatus: {4}\nSubscription: {5}\u001B[0m",
	                            new Object[]{userDetails[0].trim(), userDetails[2].trim(), userDetails[3].trim(), userDetails[4].trim(), userDetails[5].trim(), userDetails[6].trim()});
	                    return;
	                }
	            }
	            logger.log(Level.WARNING, "\u001B[31mNo profile found for username: {0}\u001B[0m", username);
	        } catch (IOException e) {
	            logger.log(Level.SEVERE, "\u001B[31mError reading user data: {0}\u001B[0m", e.getMessage());
	        }
	    }

	    private static void readArticles() {
	        List<String[]> articles = new ArrayList<>();
	        try (BufferedReader reader = new BufferedReader(new FileReader(Main.ARTICLES_FILE))) {
	            String line;
	            while ((line = reader.readLine()) != null) {
	                String[] articleDetails = line.split(",");
	                if (articleDetails.length >= 5) { // Ensure there are enough details for an article
	                    articles.add(articleDetails);
	                    logger.log(Level.INFO, "\u001B[32mID: {0} - Title: {1}\u001B[0m", new Object[]{articleDetails[0], articleDetails[1]});
	                }
	            }

	            if (articles.isEmpty()) {
	                logger.log(Level.WARNING, "\u001B[31mNo articles available at the moment.\u001B[0m");
	                return;
	            }

	            // Ask the client to choose an article by ID
	            Scanner scanner = new Scanner(System.in);
	            logger.log(Level.INFO, "\u001B[32mEnter the article ID to read full details: \u001B[0m");
	            String articleId = scanner.nextLine();

	            // Find and display the selected article
	            boolean found = false;
	            for (String[] article : articles) {
	                if (article[0].equals(articleId)) {
	                    logger.log(Level.INFO, "\u001B[36m---- Article Details ----\u001B[0m");
	                    logger.log(Level.INFO, "\u001B[32mID: {0}\nTitle: {1}\nAuthor: {2}\nPublish Date: {3}\nContent: {4}\u001B[0m",
	                            new Object[]{article[0], article[1], article[2], article[3], article[4]});
	                    found = true;
	                    break;
	                }
	            }

	            if (!found) {
	                logger.log(Level.WARNING, "\u001B[31mArticle with ID {0} not found.\u001B[0m", articleId);
	            }
	        } catch (IOException e) {
	            logger.log(Level.SEVERE, "\u001B[31mError reading articles file: {0}\u001B[0m", e.getMessage());
	        }
	    }

	    private static void changeSubscription(String username) {
	        Scanner scanner = new Scanner(System.in);

	        // Read user data from file and find the user's current subscription status
	        String currentSubscription = getCurrentSubscription(username);

	        logger.log(Level.INFO, "\u001B[32mYour current subscription: {0}\n", currentSubscription);
	        logger.log(Level.INFO, "\u001B[32mAvailable subscriptions: \n1. Free\n2. Basic\n3. Premium\n4. Lifetime\u001B[0m");
	        logger.log(Level.INFO, "\u001B[32mEnter the number of the subscription you want to switch to: \u001B[0m");
	        int choice = Integer.parseInt(scanner.nextLine());

	        String newSubscription = null;
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
	                newSubscription = null;
	                break;
	        }


	        if (newSubscription == null) {
	            logger.log(Level.WARNING, "\u001B[31mInvalid option! Subscription not changed.\u001B[0m");
	            return;
	        }

	        // Update the user's subscription in the file
	        updateSubscription(username, newSubscription);
	        logger.log(Level.INFO, "\u001B[32mYour subscription has been changed to: {0}\n", newSubscription);
	    }

	    private static String getCurrentSubscription(String username) {
	        try (BufferedReader br = new BufferedReader(new FileReader(Main.USERS_FILE))) {
	            String line;
	            while ((line = br.readLine()) != null) {
	                String[] userDetails = line.split(",");
	                if (userDetails[0].trim().equals(username)) {
	                    return userDetails[6].trim();  // Assuming subscription is at index 6
	                }
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return null;  // Default case if no subscription found
	    }

	    private static void updateSubscription(String username, String newSubscription) {
	        try {
	            File file = new File(Main.USERS_FILE);
	            File tempFile = new File("C:\\Users\\musta\\Documents\\NetBeansProjects\\gym\\src\\users_temp.txt");

	            BufferedReader reader = new BufferedReader(new FileReader(file));
	            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

	            String line;
	            while ((line = reader.readLine()) != null) {
	                String[] userDetails = line.split(",");
	                if (userDetails[0].trim().equals(username)) {
	                    userDetails[6] = newSubscription;  // Update the subscription in the array
	                    line = String.join(",", userDetails);
	                }
	                writer.write(line);
	                writer.newLine();
	            }

	            writer.close();
	            reader.close();

	            // Delete the original file and rename the temporary file to the original file
	            file.delete();
	            tempFile.renameTo(file);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
}
