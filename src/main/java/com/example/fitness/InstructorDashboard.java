
package com.example.fitness;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InstructorDashboard {

    private static final Logger logger = Logger.getLogger(InstructorDashboard.class.getName());

    public static void showDashboard(String username) {
        Scanner scanner = new Scanner(System.in);
        int choice;

        while (true) {
            logger.log(Level.INFO, "\u001B[36m------ Instructor Dashboard ------\n" +
                    "1. View Articles Related to Me\n" +
                    "2. View Profile\n" +
                    "3. Create an Article\n" +
                    "4. Logout\n" +
                    "\u001B[0m");
            logger.log(Level.INFO, "\u001B[32mEnter your choice: \u001B[0m");

            try {
                choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1: {
                        logger.log(Level.INFO, "\u001B[34mViewing articles related to you...\u001B[0m");
                        viewArticles(username);
                        break;
                    }
                    case 2: {
                        logger.log(Level.INFO, "\u001B[34mViewing profile...\u001B[0m");
                        viewProfile(username);
                        break;
                    }
                    case 3: {
                        logger.log(Level.INFO, "\u001B[34mCreating an article...\u001B[0m");
                        createArticle(username);
                        break;
                    }
                    case 4: {
                        logger.log(Level.INFO, "\u001B[33mLogging out... Goodbye, {0}!\u001B[0m", username);
                        return; // Exit the dashboard
                    }
                    default: {
                        logger.log(Level.WARNING, "\u001B[31mInvalid option! Please select a valid option.\u001B[0m");
                        break;
                    }
                }
            } catch (NumberFormatException e) {
                logger.log(Level.WARNING, "\u001B[31mInvalid input! Please enter a number.\u001B[0m");
            }
        }
    }

    private static void viewArticles(String username) {
    List<String[]> articles = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(Main.ARTICLES_FILE))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] articleDetails = line.split(",");
            if (articleDetails.length >= 5 && articleDetails[2].trim().equals(username)) {
                articles.add(articleDetails);
                logger.log(Level.INFO, "\u001B[32mID: {0} - Title: {1}\u001B[0m", new Object[]{articleDetails[0], articleDetails[1]});
            }
        }

        if (articles.isEmpty()) {
            logger.log(Level.WARNING, "\u001B[31mNo articles found related to you.\u001B[0m");
            return;
        }

        // Ask the instructor to choose an article by ID
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

   private static void createArticle(String username) {
        Scanner scanner = new Scanner(System.in);

        // Ask for article details
        logger.log(Level.INFO, "\u001B[32mEnter the article title: \u001B[0m");
        String title = scanner.nextLine();

        logger.log(Level.INFO, "\u001B[32mEnter the article content: \u001B[0m");
        String content = scanner.nextLine();

        // Get the current date
        String publishDate = java.time.LocalDate.now().toString();

        // Create article entry
        String articleId = generateArticleId();  // Implement a method to generate unique ID for each article
        String author = username;

        // Save article to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Main.ARTICLES_FILE, true))) {
            writer.write(articleId + "," + title + "," + author + "," + publishDate + "," + content);
            writer.newLine();
            logger.log(Level.INFO, "\u001B[32mArticle created successfully with ID: {0}\u001B[0m", articleId);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "\u001B[31mError saving article: {0}\u001B[0m", e.getMessage());
        }
    }

    private static String generateArticleId() {
        // Generate a unique ID for the article, this can be based on the current time or a UUID
        return String.valueOf(System.currentTimeMillis());
    }

}
