package com.example.fitness;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ContentManagement {

    public static void manageContent(String username) {
        Scanner scanner = new Scanner(System.in);
        int choice;

        while (true) {
            System.out.println("------ Content Management ------");
            System.out.println("1. Print All Articles");
            System.out.println("2. Add New Article");
            System.out.println("3. Edit Article by ID");
            System.out.println("4. Delete Article by ID");
            System.out.println("5. Back to Dashboard");
            System.out.print("Please select an option: ");

            try {
                choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        printAllArticles();
                        break;
                    case 2:
                        addNewArticle(username);
                        break;
                    case 3:
                        editArticleById();
                        break;
                    case 4:
                        deleteArticleById();
                        break;
                    case 5:
                        System.out.println("Returning to Dashboard...");
                        return;
                    default:
                        System.out.println("Invalid option. Please select a valid option.");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
            }
        }
    }

    public static void printAllArticles() {
        File file = new File(Main.ARTICLES_FILE);
        if (!file.exists()) {
            System.out.println("Articles file not found.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean foundAny = false;

            while ((line = reader.readLine()) != null) {
                String[] articleDetails = line.split(",", 5);
                if (articleDetails.length >= 5) {
                    foundAny = true;
                    // e.g. "ID: 12345 - Title: My Article"
                    System.out.println("ID: " + articleDetails[0] + " - Title: " + articleDetails[1]);
                }
            }

            if (!foundAny) {
                System.out.println("No articles available.");
            }
        } catch (IOException e) {
            System.out.println("Error reading articles file: " + e.getMessage());
        }
    }

    public static void addNewArticle(String username) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the article title: ");
        String title = scanner.nextLine();

        System.out.print("Enter the article content: ");
        String content = scanner.nextLine();

        // Current date as publish date
        String publishDate = java.time.LocalDate.now().toString();

        String articleId = generateArticleId();  
        String author = username;  

        // Save article
        File file = new File(Main.ARTICLES_FILE);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(articleId + "," + title + "," + author + "," + publishDate + "," + content);
            writer.newLine();
            System.out.println("Article created successfully with ID: " + articleId);
        } catch (IOException e) {
            System.out.println("Error saving article: " + e.getMessage());
        }
    }

    public static void editArticleById() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the ID of the article to edit: ");
        String id = scanner.nextLine();

        System.out.print("Enter the new content for article ID " + id + ": ");
        String newContent = scanner.nextLine();

        File file = new File(Main.ARTICLES_FILE);
        if (!file.exists()) {
            System.out.println("Articles file not found. Cannot edit.");
            return;
        }

        List<String> updatedLines = new ArrayList<>();
        boolean found = false;

        // Read existing articles
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 5);
                if (parts[0].equals(id)) {
                    // Overwrite content
                    updatedLines.add(parts[0] + "," + parts[1] + "," + parts[2] + "," + parts[3] + "," + newContent);
                    found = true;
                } else {
                    updatedLines.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading articles file: " + e.getMessage());
            return;
        }

        // If not found
        if (!found) {
            System.out.println("Article ID " + id + " not found.");
            return;
        }

        // Rewrite file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String updatedLine : updatedLines) {
                writer.write(updatedLine);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error editing article: " + e.getMessage());
            return;
        }

        System.out.println("Article ID " + id + " updated!\nNew Content: " + newContent);
    }

    public static void deleteArticleById() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the ID of the article to delete: ");
        String id = scanner.nextLine();

        File file = new File(Main.ARTICLES_FILE);
        if (!file.exists()) {
            System.out.println("Articles file not found. Cannot delete.");
            return;
        }

        List<String> updatedLines = new ArrayList<>();
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // If line starts with "ID," means we found the article
                String[] parts = line.split(",", 5);
                if (parts[0].equals(id)) {
                    found = true;
                } else {
                    updatedLines.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading articles file: " + e.getMessage());
            return;
        }

        if (!found) {
            System.out.println("Article ID " + id + " not found.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String updatedLine : updatedLines) {
                writer.write(updatedLine);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error deleting article: " + e.getMessage());
            return;
        }

        System.out.println("Article ID " + id + " deleted successfully!");
    }

    // Helper to generate a unique article ID (could be time-based or a UUID)
    private static String generateArticleId() {
        return String.valueOf(System.currentTimeMillis());
    }
}
