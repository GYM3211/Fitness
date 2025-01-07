package com.example.fitness;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Programs {
    private static final Logger logger = Logger.getLogger(Programs.class.getName());

    public static void viewPrograms(String instructorUsername) {
        try (BufferedReader br = new BufferedReader(new FileReader(Main.PROGRAMS_FILE))) {
            String line;
            boolean found = false;
            logger.log(Level.INFO, "\u001B[36m--- Programs by {0} ---\u001B[0m", instructorUsername);
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", 5);
                if (data[1].equals(instructorUsername)) {
                    logger.log(Level.INFO, "ID: {0}\nTitle: {1}\nDescription: {2}\nCreation Date: {3}\n",
                            new Object[]{data[0], data[2], data[3], data[4]});
                    found = true;
                }
            }
            if (!found) {
                logger.log(Level.INFO, "\u001B[33mNo programs found for {0}.\u001B[0m", instructorUsername);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "\u001B[31mError reading the programs file: {0}\u001B[0m", e.getMessage());
        }
    }

   public static void addProgram(String instructorUsername) {
        Scanner scanner = new Scanner(System.in);
        logger.log(Level.INFO, "\u001B[32mEnter the title of the program: \u001B[0m");
        String title = scanner.nextLine().trim();
        logger.log(Level.INFO, "\u001B[32mEnter the description of the program: \u001B[0m");
        String description = scanner.nextLine().trim();

        if (title.isEmpty() || description.isEmpty()) {
            logger.log(Level.WARNING, "\u001B[33mTitle or description cannot be empty.\u001B[0m");
            return;
        }

        String programId = String.valueOf(System.currentTimeMillis());
        String creationDate = new Date().toString();
        String newProgram = String.format("%s,%s,%s,%s,%s", programId, instructorUsername, title, description, creationDate);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Main.PROGRAMS_FILE, true))) {
            writer.write(newProgram + "\n");
            logger.log(Level.INFO, "\u001B[34mProgram added successfully!\u001B[0m");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "\u001B[31mError writing to the programs file: {0}\u001B[0m", e.getMessage());
        }
    }

   public static void editProgram(String instructorUsername) {
        Scanner scanner = new Scanner(System.in);
        logger.log(Level.INFO, "\u001B[32mEnter the ID of the program to edit: \u001B[0m");
        String programId = scanner.nextLine().trim();

        File originalFile = new File(Main.PROGRAMS_FILE);
        File tempFile = new File(Main.PROGRAMS_TEMP_FILE);

        if (!originalFile.exists()) {
            logger.log(Level.SEVERE, "\u001B[31mThe programs file does not exist.\u001B[0m");
            return;
        }

        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(originalFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", 5);
                if (data.length == 5 && data[0].equals(programId) && data[1].equals(instructorUsername)) {
                    logger.log(Level.INFO, "\u001B[32mEnter the new title: \u001B[0m");
                    String newTitle = scanner.nextLine().trim();
                    logger.log(Level.INFO, "\u001B[32mEnter the new description: \u001B[0m");
                    String newDescription = scanner.nextLine().trim();

                    if (newTitle.isEmpty() || newDescription.isEmpty()) {
                        logger.log(Level.WARNING, "\u001B[33mTitle or description cannot be empty. Changes discarded.\u001B[0m");
                        bw.write(line + "\n"); // Write the original line back
                    } else {
                        String updatedProgram = String.format("%s,%s,%s,%s,%s", data[0], data[1], newTitle, newDescription, data[4]);
                        bw.write(updatedProgram + "\n");
                    }
                    found = true;
                } else {
                    bw.write(line + "\n");
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "\u001B[31mError processing the programs file: {0}\u001B[0m", e.getMessage());
        }

        // Renaming logic
        if (found) {
            if (originalFile.delete()) {
                if (tempFile.renameTo(originalFile)) {
                    logger.log(Level.INFO, "\u001B[34mProgram updated successfully and saved.\u001B[0m");
                } else {
                    logger.log(Level.SEVERE, "\u001B[31mError renaming the temporary file to the original file.\u001B[0m");
                }
            } else {
                logger.log(Level.SEVERE, "\u001B[31mError deleting the original programs file.\u001B[0m");
            }
        } else {
            logger.log(Level.WARNING, "\u001B[33mProgram not found or unauthorized action.\u001B[0m");
            tempFile.delete(); // Cleanup temporary file
        }
    }

    public static void deleteProgram(String instructorUsername) {
    Scanner scanner = new Scanner(System.in);
    logger.log(Level.INFO, "\u001B[32mEnter the ID of the program to delete: \u001B[0m");
    String programId = scanner.nextLine().trim();

    File originalFile = new File(Main.PROGRAMS_FILE);
    File tempFile = new File(Main.PROGRAMS_TEMP_FILE);

    if (!originalFile.exists()) {
        logger.log(Level.SEVERE, "\u001B[31mThe programs file does not exist.\u001B[0m");
        return;
    }

    boolean found = false;

    try (BufferedReader br = new BufferedReader(new FileReader(originalFile));
         BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {

        String line;
        while ((line = br.readLine()) != null) {
            String[] data = line.split(",", 5);
            // Check if this line matches the programId and instructorUsername
            if (data.length == 5 && data[0].equals(programId) && data[1].equals(instructorUsername)) {
                logger.log(Level.INFO, "\u001B[34mProgram with ID {0} deleted successfully.\u001B[0m", programId);
                found = true; // Mark program as found, and do not write it to the temporary file
                continue; // Skip writing this line
            }
            // Otherwise, write the line to the temporary file
            bw.write(line + "\n");
        }
    } catch (IOException e) {
        logger.log(Level.SEVERE, "\u001B[31mError processing the programs file: {0}\u001B[0m", e.getMessage());
    }

    // Now, handle renaming the temporary file
    if (found) {
        if (originalFile.delete()) { // Delete the original file
            if (tempFile.renameTo(originalFile)) { // Rename temp file to original
                logger.log(Level.INFO, "\u001B[34mProgram file updated successfully.\u001B[0m");
            } else {
                logger.log(Level.SEVERE, "\u001B[31mError renaming the temporary file to the original file.\u001B[0m");
            }
        } else {
            logger.log(Level.SEVERE, "\u001B[31mError deleting the original programs file.\u001B[0m");
        }
    } else {
        logger.log(Level.WARNING, "\u001B[33mProgram not found or unauthorized action.\u001B[0m");
        tempFile.delete(); // Clean up the temporary file if nothing was deleted
    }
}
   
    public static void viewSubscribers(String instructorUsername) {
        logger.log(Level.INFO, "\u001B[36m--- Subscribers for Programs by {0} ---\u001B[0m", instructorUsername);
        try (BufferedReader br = new BufferedReader(new FileReader(Main.SUBSCRIPTIONS_FILE))) {
            String line;
            boolean found = false;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", 3);
                if (data[1].equals(instructorUsername)) {
                    logger.log(Level.INFO, "Program ID: {0}\nSubscriber: {1}\n", new Object[]{data[0], data[2]});
                    found = true;
                }
            }
            if (!found) {
                logger.log(Level.INFO, "\u001B[33mNo subscribers found for your programs.\u001B[0m");
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "\u001B[31mError reading the subscriptions file: {0}\u001B[0m", e.getMessage());
        }
    }
}
