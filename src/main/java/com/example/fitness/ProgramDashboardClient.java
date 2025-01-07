package com.example.fitness;

import java.io.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProgramDashboardClient {
    
    private static final Logger logger = Logger.getLogger(ProgramDashboardClient.class.getName());
    
      public static void showProgramsDashboard(String clientUsername) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            logger.log(Level.INFO, 
            "\u001B[36m------ Programs Dashboard ------\u001B[0m\n" +
            "\u001B[32m1. View All Programs\u001B[0m\n" +
            "\u001B[32m2. View My Programs\u001B[0m\n" +
            "\u001B[32m3. Subscribe to a Program\u001B[0m\n" +
            "\u001B[32m4. Unsubscribe from a Program\u001B[0m\n" +
            "\u001B[32m5. Exit Dashboard\u001B[0m\n" +
            "\u001B[33mChoose an option: \u001B[0m");

            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume the newline

            switch (choice) {
                case 1:
                    viewAllPrograms();
                    break;
                case 2:
                    viewMyPrograms(clientUsername);
                    break;
                case 3:
                    subscribeToProgram(clientUsername);
                    break;
                case 4:
                    unsubscribeFromProgram(clientUsername);
                    break;
                case 5:
                    logger.log(Level.INFO, "\u001B[34mExiting Programs Dashboard...\u001B[0m");
                    return;
                default:
                    logger.log(Level.WARNING, "\u001B[31mInvalid option. Please try again.\u001B[0m");
            }
        }
    }

    public static void viewAllPrograms() {
        // Method to view all programs
        try (BufferedReader br = new BufferedReader(new FileReader(Main.PROGRAMS_FILE))) {
            String line;
            boolean hasPrograms = false;
            while ((line = br.readLine()) != null) {
                String[] programDetails = line.split(",", 5);
                String programId = programDetails[0];
                String instructorUsername = programDetails[1];
                String title = programDetails[2];
                String description = programDetails[3];
                String creationDate = programDetails[4];
                logger.log(Level.INFO, "Program ID: {0}\nTitle: {1}\nInstructor: {2}\nDescription: {3}\nCreated on: {4}\n------------------------------------------", 
                new Object[]{programId, title, instructorUsername, description, creationDate});
                hasPrograms = true;
            }
            if (!hasPrograms) {
                logger.log(Level.INFO, "\u001B[33mNo programs available at the moment.\u001B[0m");
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "\u001B[31mError reading programs file: {0}\u001B[0m", e.getMessage());
        }
    }

    public static void subscribeToProgram(String clientUsername) {
        Scanner scanner = new Scanner(System.in);
        logger.log(Level.INFO, "\u001B[32mEnter the ID of the program to subscribe to: \u001B[0m");
        String programId = scanner.nextLine();
        boolean programFound = false;
        boolean alreadySubscribed = false;

        // Check if the program exists in the programs file
        try (BufferedReader br = new BufferedReader(new FileReader(Main.PROGRAMS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", 5);
                if (data[0].equals(programId)) {
                    programFound = true;
                    break; // Program found, no need to continue checking
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "\u001B[31mError reading the programs file: {0}\u001B[0m", e.getMessage());
        }

        if (!programFound) {
            logger.log(Level.WARNING, "\u001B[33mProgram not found! Please check the ID.\u001B[0m");
            return;
        }

        // Check if the user is already subscribed by reading the subscription file
        try (BufferedReader br = new BufferedReader(new FileReader(Main.SUBSCRIPTIONS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", 2);
                if (data[0].equals(clientUsername) && data[1].equals(programId)) {
                    alreadySubscribed = true;
                    break; // User already subscribed, no need to continue checking
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "\u001B[31mError reading the subscriptions file: {0}\u001B[0m", e.getMessage());
        }

        if (alreadySubscribed) {
            logger.log(Level.WARNING, "\u001B[33mYou are already subscribed to this program.\u001B[0m");
        } else {
            // Add the subscription to the subscriptions file
            try (FileWriter writer = new FileWriter(Main.SUBSCRIPTIONS_FILE, true)) {
                writer.write(clientUsername + "," + programId + "\n");
                logger.log(Level.INFO, "\u001B[34mYou have successfully subscribed to the program! \u001B[0m");
            } catch (IOException e) {
                logger.log(Level.SEVERE, "\u001B[31mError writing to the subscriptions file: {0}\u001B[0m", e.getMessage());
            }
        }
    }
    
    public static void viewMyPrograms(String clientUsername) {
        try (BufferedReader br = new BufferedReader(new FileReader(Main.SUBSCRIPTIONS_FILE))) {
            String line;
            boolean found = false;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", 2); // assuming it's [programId, clientUsername]
                if (data[0].equals(clientUsername)) {
                    String programId = data[1];

                    // Now, fetch the program details using programId from the programs file
                    try (BufferedReader programReader = new BufferedReader(new FileReader(Main.PROGRAMS_FILE))) {
                        String programLine;
                        while ((programLine = programReader.readLine()) != null) {
                            String[] programData = programLine.split(",", 5);
                            if (programData[0].equals(programId)) {
                                // Program found, print its details
                                logger.log(Level.INFO, 
                                        "Program ID: {0}\nTitle: {1}\nInstructor: {2}\nDescription: {3}\nCreation Date: {4}\n", 
                                        new Object[]{programData[0], programData[2], programData[1], programData[3], programData[4]});
                                found = true;
                            }
                        }
                    } catch (IOException e) {
                        logger.log(Level.SEVERE, "\u001B[31mError reading the programs file: {0}\u001B[0m", e.getMessage());
                    }
                }
            }

            if (!found) {
                logger.log(Level.INFO, "\u001B[33mYou are not subscribed to any programs.\u001B[0m");
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, "\u001B[31mError reading the subscriptions file: {0}\u001B[0m", e.getMessage());
        }
    }

    
    public static void unsubscribeFromProgram(String clientUsername) {
    Scanner scanner = new Scanner(System.in);
    logger.log(Level.INFO, "\u001B[32mEnter the ID of the program to unsubscribe from: \u001B[0m");
    String programId = scanner.nextLine();

    File tempFile = new File(Main.SUBSCRIPTIONS_TEMP_FILE);
    boolean found = false;

    try (BufferedReader br = new BufferedReader(new FileReader(Main.SUBSCRIPTIONS_FILE));
         BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {

        String line;
        while ((line = br.readLine()) != null) {
            String[] data = line.split(",", 2);
            if (data[0].equals(clientUsername) && data[1].equals(programId)) {
                found = true;
            } else {
                bw.write(line + "\n");
            }
        }
    } catch (IOException e) {
        logger.log(Level.SEVERE, "\u001B[31mError reading the subscriptions file: {0}\u001B[0m", e.getMessage());
    }

    if (!found) {
        logger.log(Level.WARNING, "\u001B[33mNo subscription found for this client to the program with ID: {0}\u001B[0m", programId);
        return;
    }

    // Ensure the original subscriptions file exists before renaming
    File originalFile = new File(Main.SUBSCRIPTIONS_FILE);
    if (!originalFile.exists()) {
        logger.log(Level.SEVERE, "\u001B[31mOriginal subscriptions file does not exist.\u001B[0m");
        return;
    }

    // Attempt to delete the original file before renaming the temp file
    if (originalFile.delete()) {
        if (tempFile.renameTo(originalFile)) {
            logger.log(Level.INFO, "\u001B[34mYou have successfully unsubscribed from the program. \u001B[0m");
        } else {
            logger.log(Level.SEVERE, "\u001B[31mError renaming the temporary file to original file.\u001B[0m");
        }
    } else {
        logger.log(Level.SEVERE, "\u001B[31mError deleting the original subscriptions file.\u001B[0m");
    }
}

}
