package com.example.fitness;

import java.io.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FitnessGoals {
    private static final Logger logger = Logger.getLogger(FitnessGoals.class.getName());

    public static void viewFitnessGoals(String username) {
        if (username == null || username.isEmpty()) {
            logger.log(Level.WARNING, "\u001B[31mUsername cannot be null or empty. Please provide a valid username.\u001B[0m");
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(Main.FITNESS_GOALS_FILE))) {
            String line;
            boolean found = false;
            logger.log(Level.INFO, "\u001B[36mYour Fitness Goals:\u001B[0m");
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", 3);
                if (data[1].equals(username)) {
                    logger.log(Level.INFO, "\u001B[34mID: {0}\nGoal: {1}\u001B[0m", new Object[]{data[0], data[2]});
                    found = true;
                }
            }
            if (!found) {
                logger.log(Level.INFO, "\u001B[33mNo fitness goals found for this username.\u001B[0m");
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "\u001B[31mError reading the fitness goals file: {0}\u001B[0m", e.getMessage());
        }
    }


    public static void addFitnessGoal(String username) {
        if (username == null || username.isEmpty()) {
            logger.log(Level.WARNING, "\u001B[31mUsername cannot be null or empty. Please provide a valid username.\u001B[0m");
            return;
        }
        Scanner scanner = new Scanner(System.in);
        logger.log(Level.INFO, "\u001B[32mEnter your fitness goal: \u001B[0m");
        String goal = scanner.nextLine();
        if (goal.isEmpty()) {
            logger.log(Level.WARNING, "\u001B[31mFitness goal cannot be empty.\u001B[0m");
            return;
        }

        String id = generateGoalId();
        String newGoalEntry = id + "," + username + "," + goal;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(Main.FITNESS_GOALS_FILE, true))) {
            bw.write(newGoalEntry + "\n");
            logger.log(Level.INFO, "\u001B[34mFitness goal added successfully with ID {0} for user: {1}\u001B[0m", new Object[]{id, username});
        } catch (IOException e) {
            logger.log(Level.SEVERE, "\u001B[31mError writing to the fitness goals file: {0}\u001B[0m", e.getMessage());
        }
    }


    private static String generateGoalId() {
        return String.valueOf(System.currentTimeMillis());
    }

    public static void deleteFitnessGoal(String username) {
        if (username == null || username.isEmpty()) {
            logger.log(Level.WARNING, "\u001B[31mUsername cannot be null or empty. Please provide a valid username.\u001B[0m");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        logger.log(Level.INFO, "\u001B[32mEnter the Goal ID to delete: \u001B[0m");
        String goalId = scanner.nextLine();

        if (goalId == null || goalId.isEmpty()) {
            logger.log(Level.WARNING, "\u001B[31mGoal ID cannot be null or empty. Please provide a valid Goal ID.\u001B[0m");
            return;
        }

        File originalFile = new File(Main.FITNESS_GOALS_FILE);
        File tempFile = new File("temp_fitness_goals.txt");
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(originalFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", 3);
                if (data.length == 3) {
                    String currentGoalId = data[0].trim();
                    String currentUsername = data[1].trim();
                    if (currentGoalId.equals(goalId) && currentUsername.equals(username)) {
                        found = true;
                    } else {
                        bw.write(line);
                        bw.newLine();
                    }
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "\u001B[31mError processing the fitness goals file: {0}\u001B[0m", e.getMessage());
            return;
        }

        if (found) {
            if (originalFile.delete()) {
                if (tempFile.renameTo(originalFile)) {
                    logger.log(Level.INFO, "\u001B[34mGoal ID {0} for user {1} has been deleted successfully.\u001B[0m", new Object[]{goalId, username});
                } else {
                    logger.log(Level.SEVERE, "\u001B[31mError renaming temporary file to the original fitness goals file.\u001B[0m");
                }
            } else {
                logger.log(Level.SEVERE, "\u001B[31mError deleting the original fitness goals file.\u001B[0m");
            }
        } else {
            tempFile.delete();
            logger.log(Level.INFO, "\u001B[33mNo matching fitness goal found for Goal ID {0} and username {1}.\u001B[0m", new Object[]{goalId, username});
        }
    }


}
