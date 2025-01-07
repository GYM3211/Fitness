package com.example.fitness;

import java.io.*;
import java.util.Scanner;

public class FitnessGoals {

    public static void viewFitnessGoals(String username) {
        if (username == null || username.isEmpty()) {
            System.out.println("Username cannot be null or empty.");
            return;
        }

        File file = new File(Main.FITNESS_GOALS_FILE);
        if (!file.exists()) {
            System.out.println("No fitness goals file found.");
            return;
        }

        boolean foundAny = false;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            System.out.println("Your Fitness Goals:");
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", 3);
                if (data.length == 3 && data[1].equals(username)) {
                    System.out.println("ID: " + data[0]);
                    System.out.println("Goal: " + data[2]);
                    System.out.println("---------------------------");
                    foundAny = true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading the fitness goals file: " + e.getMessage());
            return;
        }

        if (!foundAny) {
            System.out.println("No fitness goals found for this username.");
        }
    }

    public static void addFitnessGoal(String username) {
        if (username == null || username.isEmpty()) {
            System.out.println("Username cannot be null or empty.");
            return;
        }
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your fitness goal: ");
        String goal = scanner.nextLine();
        if (goal.isEmpty()) {
            System.out.println("Fitness goal cannot be empty.");
            return;
        }

        String id = generateGoalId();
        String newGoalEntry = id + "," + username + "," + goal;

        File file = new File(Main.FITNESS_GOALS_FILE);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(newGoalEntry + "\n");
            System.out.println("Fitness goal added successfully with ID " + id
                               + " for user: " + username);
        } catch (IOException e) {
            System.out.println("Error writing to the fitness goals file: " + e.getMessage());
        }
    }

    public static void deleteFitnessGoal(String username) {
        if (username == null || username.isEmpty()) {
            System.out.println("Username cannot be null or empty.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the Goal ID to delete: ");
        String goalId = scanner.nextLine();

        if (goalId == null || goalId.isEmpty()) {
            System.out.println("Goal ID cannot be null or empty.");
            return;
        }

        File originalFile = new File(Main.FITNESS_GOALS_FILE);
        if (!originalFile.exists()) {
            System.out.println("No fitness goals file found.");
            return;
        }

        File tempFile = new File("temp_fitness_goals.txt");
        boolean found = false;

        try (
            BufferedReader br = new BufferedReader(new FileReader(originalFile));
            BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", 3);
                if (data.length == 3) {
                    String currentGoalId = data[0].trim();
                    String currentUsername = data[1].trim();
                    if (currentGoalId.equals(goalId) && currentUsername.equals(username)) {
                        found = true;
                        // skip writing => remove
                    } else {
                        bw.write(line);
                        bw.newLine();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error processing the fitness goals file: " + e.getMessage());
            return;
        }

        if (!found) {
            tempFile.delete();
            System.out.println("No matching fitness goal found for Goal ID " 
                               + goalId + " and username " + username);
            return;
        }

        // Overwrite original file with updated lines
        if (!originalFile.delete()) {
            System.out.println("Error deleting the original fitness goals file.");
            return;
        }

        if (!tempFile.renameTo(originalFile)) {
            System.out.println("Error renaming temp file to fitness goals file.");
            return;
        }

        System.out.println("Goal ID " + goalId + " for user " + username 
                           + " has been deleted successfully.");
    }

    private static String generateGoalId() {
        return String.valueOf(System.currentTimeMillis());
    }
}
