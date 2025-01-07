package com.example.fitness;

import java.io.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProgramDashboardClient {
    
	 public static void showProgramsDashboard(String clientUsername) {
	        Scanner scanner = new Scanner(System.in);
	        while (true) {
	            System.out.println("------ Programs Dashboard ------");
	            System.out.println("1. View All Programs");
	            System.out.println("2. View My Programs");
	            System.out.println("3. Subscribe to a Program");
	            System.out.println("4. Unsubscribe from a Program");
	            System.out.println("5. Exit Dashboard");
	            System.out.print("Choose an option: ");

	            // Use nextLine() + parse to avoid partial consumption
	            String choiceStr = scanner.nextLine();
	            int choice;
	            try {
	                choice = Integer.parseInt(choiceStr);
	            } catch (NumberFormatException e) {
	                System.out.println("Invalid option. Please try again.");
	                continue;
	            }

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
	                    System.out.println("Exiting Programs Dashboard...");
	                    return;
	                default:
	                    System.out.println("Invalid option. Please try again.");
	            }
	        }
	    }

	    public static void viewAllPrograms() {
	        // Display all programs from Main.PROGRAMS_FILE
	        try (BufferedReader br = new BufferedReader(new FileReader(Main.PROGRAMS_FILE))) {
	            String line;
	            boolean hasPrograms = false;
	            while ((line = br.readLine()) != null) {
	                String[] programDetails = line.split(",", 5);
	                if (programDetails.length == 5) {
	                    String programId = programDetails[0];
	                    String instructorUsername = programDetails[1];
	                    String title = programDetails[2];
	                    String description = programDetails[3];
	                    String creationDate = programDetails[4];

	                    System.out.println("Program ID: " + programId);
	                    System.out.println("Title: " + title);
	                    System.out.println("Instructor: " + instructorUsername);
	                    System.out.println("Description: " + description);
	                    System.out.println("Created on: " + creationDate);
	                    System.out.println("------------------------------------------");
	                    hasPrograms = true;
	                }
	            }
	            if (!hasPrograms) {
	                System.out.println("No programs available at the moment.");
	            }
	        } catch (IOException e) {
	            System.out.println("Error reading programs file: " + e.getMessage());
	        }
	    }

	    public static void subscribeToProgram(String clientUsername) {
	        Scanner scanner = new Scanner(System.in);
	        System.out.print("Enter the ID of the program to subscribe to: ");
	        String programId = scanner.nextLine();

	        boolean programFound = false;
	        boolean alreadySubscribed = false;

	        // Check if the program exists
	        try (BufferedReader br = new BufferedReader(new FileReader(Main.PROGRAMS_FILE))) {
	            String line;
	            while ((line = br.readLine()) != null) {
	                String[] data = line.split(",", 5);
	                if (data.length == 5 && data[0].equals(programId)) {
	                    programFound = true;
	                    break;
	                }
	            }
	        } catch (IOException e) {
	            System.out.println("Error reading the programs file: " + e.getMessage());
	        }

	        if (!programFound) {
	            System.out.println("Program not found! Please check the ID.");
	            return;
	        }

	        // Check if user is already subscribed
	        try (BufferedReader br = new BufferedReader(new FileReader(Main.SUBSCRIPTIONS_FILE))) {
	            String line;
	            while ((line = br.readLine()) != null) {
	                String[] data = line.split(",", 2);
	                // Format: clientUsername,programId
	                if (data.length == 2 && data[0].equals(clientUsername) && data[1].equals(programId)) {
	                    alreadySubscribed = true;
	                    break;
	                }
	            }
	        } catch (IOException e) {
	            System.out.println("Error reading the subscriptions file: " + e.getMessage());
	        }

	        if (alreadySubscribed) {
	            System.out.println("You are already subscribed to this program.");
	        } else {
	            try (FileWriter writer = new FileWriter(Main.SUBSCRIPTIONS_FILE, true)) {
	                writer.write(clientUsername + "," + programId + "\n");
	                System.out.println("You have successfully subscribed to the program!");
	            } catch (IOException e) {
	                System.out.println("Error writing to the subscriptions file: " + e.getMessage());
	            }
	        }
	    }

	    public static void viewMyPrograms(String clientUsername) {
	        // Find all subscriptions for this user, then read the program details
	        boolean foundAny = false;

	        try (BufferedReader br = new BufferedReader(new FileReader(Main.SUBSCRIPTIONS_FILE))) {
	            String line;
	            while ((line = br.readLine()) != null) {
	                String[] data = line.split(",", 2);
	                // Format: clientUsername,programId
	                if (data.length == 2 && data[0].equals(clientUsername)) {
	                    String programId = data[1];

	                    // Now fetch details from programs file
	                    try (BufferedReader programReader = new BufferedReader(new FileReader(Main.PROGRAMS_FILE))) {
	                        String programLine;
	                        while ((programLine = programReader.readLine()) != null) {
	                            String[] programData = programLine.split(",", 5);
	                            if (programData.length == 5 && programData[0].equals(programId)) {
	                                System.out.println("Program ID: " + programData[0]);
	                                System.out.println("Title: " + programData[2]);
	                                System.out.println("Instructor: " + programData[1]);
	                                System.out.println("Description: " + programData[3]);
	                                System.out.println("Creation Date: " + programData[4]);
	                                System.out.println("------------------------------------------");
	                                foundAny = true;
	                            }
	                        }
	                    } catch (IOException e) {
	                        System.out.println("Error reading the programs file: " + e.getMessage());
	                    }
	                }
	            }
	        } catch (IOException e) {
	            System.out.println("Error reading the subscriptions file: " + e.getMessage());
	        }

	        if (!foundAny) {
	            System.out.println("You are not subscribed to any programs.");
	        }
	    }

	    public static void unsubscribeFromProgram(String clientUsername) {
	        Scanner scanner = new Scanner(System.in);
	        System.out.print("Enter the ID of the program to unsubscribe from: ");
	        String programId = scanner.nextLine();

	        File tempFile = new File(Main.SUBSCRIPTIONS_TEMP_FILE);
	        boolean found = false;

	        try (BufferedReader br = new BufferedReader(new FileReader(Main.SUBSCRIPTIONS_FILE));
	             BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {

	            String line;
	            while ((line = br.readLine()) != null) {
	                String[] data = line.split(",", 2);
	                if (data.length == 2 && data[0].equals(clientUsername) && data[1].equals(programId)) {
	                    found = true;
	                    // skip writing => remove subscription
	                } else {
	                    bw.write(line);
	                    bw.newLine();
	                }
	            }
	        } catch (IOException e) {
	            System.out.println("Error reading the subscriptions file: " + e.getMessage());
	        }

	        if (!found) {
	            System.out.println("No subscription found for this client to the program with ID: " + programId);
	            return;
	        }

	        File originalFile = new File(Main.SUBSCRIPTIONS_FILE);
	        if (!originalFile.exists()) {
	            System.out.println("Original subscriptions file does not exist.");
	            return;
	        }

	        if (originalFile.delete()) {
	            if (tempFile.renameTo(originalFile)) {
	                System.out.println("You have successfully unsubscribed from the program.");
	            } else {
	                System.out.println("Error renaming the temporary file to original file.");
	            }
	        } else {
	            System.out.println("Error deleting the original subscriptions file.");
	        }
	    }


}
