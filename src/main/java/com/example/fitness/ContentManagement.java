package com.example.fitness;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ContentManagement {

    private static final Logger logger = Logger.getLogger(ContentManagement.class.getName());

    public static void manageContent() {
        Scanner scanner = new Scanner(System.in);
        int choice;

        while (true) {
            // Display content management options
            logger.log(Level.INFO, "\u001B[36m------ Content Management ------\u001B[0m\n" +
                    "\u001B[32m1. Print All Articles\n" +
                    "2. Add New Article\n" +
                    "3. Edit Article by ID\n" +
                    "4. Delete Article by ID\n" +
                    "5. Back to Dashboard\u001B[0m");

            logger.log(Level.INFO, "\u001B[32mPlease select an option: \u001B[0m");
            try {
                choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        printAllArticles();  // Method to display all articles
                        break;
                    case 2:
                        //addNewArticle();  // Method to add a new article
                        break;
                    case 3:
                        editArticleById();  // Method to edit an article by ID
                        break;
                    case 4:
                        deleteArticleById();  // Method to delete an article by ID
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

    private static void printAllArticles() {
        // Logic to read and print all articles from the ARTICLES_FILE
        try (BufferedReader reader = new BufferedReader(new FileReader(Main.ARTICLES_FILE))) {
            String line;
            boolean found = false;

            // Read the articles from the file and display the titles and IDs
            while ((line = reader.readLine()) != null) {
                String[] articleDetails = line.split(",");
                if (articleDetails.length >= 5) { // Ensure there are enough details for an article
                    found = true;
                    logger.log(Level.INFO, "\u001B[32mID: {0} - Title: {1}\u001B[0m", new Object[]{articleDetails[0], articleDetails[1]});
                }
            }

            if (!found) {
                logger.log(Level.WARNING, "\u001B[31mNo articles available.\u001B[0m");
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "\u001B[31mError reading articles file: {0}\u001B[0m", e.getMessage());
        }
    }


    private static void addNewArticle(String username) {
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
   
    private static void editArticleById() {
        logger.log(Level.INFO, "\u001B[32mEnter the ID of the article to edit: \u001B[0m");
        Scanner scanner = new Scanner(System.in);
        String id = scanner.nextLine();

        logger.log(Level.INFO, "\u001B[32mEnter the new content for article ID {0}: \u001B[0m", id);
        String newContent = scanner.nextLine();

        try {
            File file = new File(Main.ARTICLES_FILE);
            List<String> updatedLines = new ArrayList<>();
            boolean found = false;

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",", 5);
                    if (parts[0].equals(id)) {
                        updatedLines.add(parts[0] + "," + parts[1] + "," + parts[2] + "," + parts[3] + "," + newContent);
                        found = true;
                    } else {
                        updatedLines.add(line);
                    }
                }
            }

            if (!found) {
                logger.log(Level.WARNING, "\u001B[31mArticle ID {0} not found.\u001B[0m", id);
                return;
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (String updatedLine : updatedLines) {
                    writer.write(updatedLine);
                    writer.newLine();
                }
            }

            logger.log(Level.INFO, "\u001B[34mArticle ID {0} updated!\nNew Content: {1}\u001B[0m", new Object[]{id, newContent});
        } catch (IOException e) {
            logger.log(Level.SEVERE, "\u001B[31mError editing article: {0}\u001B[0m", e.getMessage());
        }
    }

    
    private static String generateArticleId() {
        // Generate a unique ID for the article, this can be based on the current time or a UUID
        return String.valueOf(System.currentTimeMillis());
    }
    
    private static void deleteArticleById() {
        logger.log(Level.INFO, "\u001B[31mEnter the ID of the article to delete: \u001B[0m");
        Scanner scanner = new Scanner(System.in);
        String id = scanner.nextLine();

        try {
            File file = new File(Main.ARTICLES_FILE);
            List<String> updatedLines = new ArrayList<>();
            boolean found = false;

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.startsWith(id + ",")) {
                        updatedLines.add(line);
                    } else {
                        found = true;
                    }
                }
            }

            if (!found) {
                logger.log(Level.WARNING, "\u001B[31mArticle ID {0} not found.\u001B[0m", id);
                return;
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (String updatedLine : updatedLines) {
                    writer.write(updatedLine);
                    writer.newLine();
                }
            }

            logger.log(Level.INFO, "\u001B[34mArticle ID {0} deleted successfully!\u001B[0m", id);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "\u001B[31mError deleting article: {0}\u001B[0m", e.getMessage());
        }
    }

}
