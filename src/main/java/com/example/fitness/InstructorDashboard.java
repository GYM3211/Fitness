
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

	    public static void showDashboard(String username) {
	        Scanner scanner = new Scanner(System.in);
	        int choice;

	        while (true) {
	            // Minimal user menu
	            System.out.println("------ Instructor Dashboard ------\n" +
	                    "1. View Articles Related to Me\n" +
	                    "2. View Profile\n" +
	                    "3. Create an Article\n" +
	                    "4. Programs Dashboard\n" +
	                    "5. Feedback\n" +
	                    "6. Logout\n");
	            System.out.print("Enter your choice: ");

	            try {
	                choice = Integer.parseInt(scanner.nextLine());
	                switch (choice) {
	                
	                    case 1:
	                        System.out.println("Viewing articles related to you...");
	                        viewArticles(username);
	                        break;
	                    case 2:
	                        System.out.println("Viewing profile...");
	                        viewProfile(username);
	                        break;
	                    case 3:
	                        System.out.println("Creating an article...");
	                        createArticle(username);
	                        break;
	                    case 4:
	                        System.out.println("Viewing programs dashboard...");
	                        showProgramsDashboard(username);
	                        break;
	                    case 5:
	                        System.out.println("Viewing Feedback...");
	                        // Make sure FeedbackHandler.showFeedbackDashboard(...) works in your project
	                        FeedbackHandler.showFeedbackDashboard(username, "Client");
	                        break;
	                    case 6:
	                        System.out.println("Logging out... Goodbye, " + username + "!");
	                        return; // Exit the dashboard
	                    default:
	                        System.out.println("Invalid option! Please select a valid option.");
	                        break;
	                }
	            } catch (NumberFormatException e) {
	                System.out.println("Invalid input! Please enter a number.");
	            }
	        }
	    }

	    public static void showProgramsDashboard(String instructorUsername) {
	        Scanner scanner = new Scanner(System.in);
	        while (true) {
	            System.out.println("--- Programs Dashboard ---\n" +
	                    "1. View Programs\n" +
	                    "2. Add Program\n" +
	                    "3. Edit Program\n" +
	                    "4. Delete Program\n" +
	                    "5. View Subscribers\n" +
	                    "6. Exit");
	            System.out.print("Choose an option: ");
	            String choice = scanner.nextLine();

	            switch (choice) {
	                case "1":
	                    Programs.viewPrograms(instructorUsername);
	                    break;
	                case "2":
	                    Programs.addProgram(instructorUsername);
	                    break;
	                case "3":
	                    Programs.editProgram(instructorUsername);
	                    break;
	                case "4":
	                    Programs.deleteProgram(instructorUsername);
	                    break;
	                case "5":
	                    Programs.viewSubscribers(instructorUsername);
	                    break;
	                case "6":
	                    System.out.println("Exiting Programs Dashboard...");
	                    return;
	                default:
	                    System.out.println("Invalid choice. Please try again.");
	            }
	        }
	    }

	    //-------------------------------------------------------------------------
	    // This method is what your tests call "viewArticles(username)"
	    //-------------------------------------------------------------------------
	    public static void viewArticles(String username) {
	        List<String[]> articles = new ArrayList<>();

	        // Read articles from Main.ARTICLES_FILE
	        try (BufferedReader reader = new BufferedReader(new FileReader(Main.ARTICLES_FILE))) {
	            String line;
	            while ((line = reader.readLine()) != null) {
	                String[] articleDetails = line.split(",", 5);
	                // Format: [0]=articleId, [1]=title, [2]=author, [3]=publishDate, [4]=content
	                if (articleDetails.length >= 5 && articleDetails[2].trim().equals(username)) {
	                    articles.add(articleDetails);
	                    // The test expects lines like: "ID: A111 - Title: Article One"
	                    System.out.println("ID: " + articleDetails[0] + " - Title: " + articleDetails[1]);
	                }
	            }

	            if (articles.isEmpty()) {
	                // The test expects EXACT: "No articles found related to you."
	                System.out.println("No articles found related to you.");
	                return;
	            }

	            // Ask the instructor to choose an article by ID
	            Scanner scanner = new Scanner(System.in);
	            System.out.print("Enter the article ID to read full details: ");
	            String articleId = scanner.nextLine();

	            // Find and display the selected article
	            boolean found = false;
	            for (String[] article : articles) {
	                if (article[0].equals(articleId)) {
	                    // The test expects:
	                    //  "---- Article Details ----"
	                    //   "ID: A111"
	                    //   "Title: Article One"
	                    //   "Author: instructorA"
	                    //   "Publish Date: 2025-01-01"
	                    //   "Content: Some content"
	                    System.out.println("---- Article Details ----");
	                    System.out.println("ID: " + article[0]);
	                    System.out.println("Title: " + article[1]);
	                    System.out.println("Author: " + article[2]);
	                    System.out.println("Publish Date: " + article[3]);
	                    System.out.println("Content: " + article[4]);
	                    found = true;
	                    break;
	                }
	            }

	            if (!found) {
	                // The test expects: "Article with ID {0} not found."
	                System.out.println("Article with ID " + articleId + " not found.");
	            }

	        } catch (IOException e) {
	            System.err.println("Error reading articles file: " + e.getMessage());
	        }
	    }

	    //-------------------------------------------------------------------------
	    // This method is tested by "viewProfile() - shows correct profile details"
	    //-------------------------------------------------------------------------
	    public static void viewProfile(String username) {
	        // The test expects lines like:
	        // "Username: instructorX"
	        // "Role: Instructor"
	        // "Gender: Male"
	        // "Age: 35"
	        // "Status: Active"
	        // "Subscription: Monthly"
	        boolean found = false;
	        try (BufferedReader br = new BufferedReader(new FileReader(Main.USERS_FILE))) {
	            String line;
	            while ((line = br.readLine()) != null) {
	                // Format: [0]=username, [1]=???, [2]=role, [3]=gender, [4]=age, [5]=status, [6]=subscription
	                String[] userDetails = line.split(",");
	                if (userDetails[0].trim().equals(username)) {
	                    System.out.println("Username: " + userDetails[0].trim());
	                    System.out.println("Role: " + userDetails[2].trim());
	                    System.out.println("Gender: " + userDetails[3].trim());
	                    System.out.println("Age: " + userDetails[4].trim());
	                    System.out.println("Status: " + userDetails[5].trim());
	                    System.out.println("Subscription: " + userDetails[6].trim());
	                    found = true;
	                    break;
	                }
	            }
	        } catch (IOException e) {
	            System.err.println("Error reading user data: " + e.getMessage());
	        }

	        if (!found) {
	            // Test expects EXACT: "No profile found for username: instructorX"
	            System.out.println("No profile found for username: " + username);
	        }
	    }

	    //-------------------------------------------------------------------------
	    // This method is tested by "createArticle() - successfully creates a new article"
	    //-------------------------------------------------------------------------
	    public static void createArticle(String username) {
	        Scanner scanner = new Scanner(System.in);

	        // Ask for article details
	        System.out.print("Enter the article title: ");
	        String title = scanner.nextLine();

	        System.out.print("Enter the article content: ");
	        String content = scanner.nextLine();

	        // Get the current date
	        String publishDate = java.time.LocalDate.now().toString();

	        // Create article entry
	        String articleId = generateArticleId();  // e.g. System.currentTimeMillis()
	        String author = username;

	        // Save article to file
	        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Main.ARTICLES_FILE, true))) {
	            // Format: articleId, title, author, publishDate, content
	            writer.write(articleId + "," + title + "," + author + "," + publishDate + "," + content);
	            writer.newLine();
	            System.out.println("Article created successfully with ID: " + articleId);
	        } catch (IOException e) {
	            System.err.println("Error saving article: " + e.getMessage());
	        }
	    }

	    private static String generateArticleId() {
	        // Generate a unique ID for the article, e.g. current time
	        return String.valueOf(System.currentTimeMillis());
	    }


}