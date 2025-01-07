package najah.edu.acceptance_tests;
import org.junit.jupiter.api.Test;  // JUnit 5
import org.junit.jupiter.api.*;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;

import org.junit.jupiter.api.io.TempDir;
import com.example.fitness.Main;
import com.example.fitness.Programs;

import java.io.*;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

public class ProgramsTest {
	 // We'll mimic the programs and subscriptions files using temp files
    @TempDir
    static Path tempDir;  // Provided by JUnit to create a temporary directory

    private static File programsFile;
    private static File programsTempFile;
    private static File subscriptionsFile;

    // Adjust the constants from Main as needed (e.g., if you need to inject paths)
    @BeforeAll
    static void setUpAll() {
        programsFile = tempDir.resolve("programs.txt").toFile();
        programsTempFile = tempDir.resolve("programs_temp.txt").toFile();
        subscriptionsFile = tempDir.resolve("subscriptions.txt").toFile();

        // Set the paths for Main (if applicable)
        Main.PROGRAMS_FILE = programsFile.getAbsolutePath();
        Main.PROGRAMS_TEMP_FILE = programsTempFile.getAbsolutePath();
        Main.SUBSCRIPTIONS_FILE = subscriptionsFile.getAbsolutePath();
    }

    @BeforeEach
    void setUp() throws IOException {
        // Clean up or create empty files before each test
        if (!programsFile.exists()) {
            programsFile.createNewFile();
        } else {
            new PrintWriter(programsFile).close();
        }

        if (!programsTempFile.exists()) {
            programsTempFile.createNewFile();
        } else {
            new PrintWriter(programsTempFile).close();
        }

        if (!subscriptionsFile.exists()) {
            subscriptionsFile.createNewFile();
        } else {
            new PrintWriter(subscriptionsFile).close();
        }
    }

    @Test
    @DisplayName("View programs for an instructor when no programs exist")
    void testViewProgramsWhenNoPrograms() throws IOException {
        // Attempt to view programs for an instructor with no programs
        // Expecting a message indicating no programs found
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        Programs.viewPrograms("instructorX"); // The file is empty

        String output = outContent.toString();
        assertTrue(output.contains("No programs found for instructorX"),
                   "Expected a 'no programs found' message for instructorX");

        // Reset the System.out
        System.setOut(System.out);
    }

    @Test
    @DisplayName("View programs for an instructor when some programs exist")
    void testViewProgramsWithExistingPrograms() throws IOException {
        // Write some sample programs data to the programs file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(programsFile))) {
            // Format: programId, instructorUsername, title, description, creationDate
            bw.write("12345,instructorA,Title A,Desc A,Date A\n");
            bw.write("67890,instructorB,Title B,Desc B,Date B\n");
            bw.write("11111,instructorA,Title C,Desc C,Date C\n");
        }

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        Programs.viewPrograms("instructorA");

        String output = outContent.toString();
        // We should see IDs 12345 and 11111 (programs belonging to instructorA)
        assertTrue(output.contains("ID: 12345"), "Expected program 12345 to be shown");
        assertTrue(output.contains("Title: Title A"), "Expected Title A to appear");
        assertTrue(output.contains("ID: 11111"), "Expected program 11111 to be shown");
        assertFalse(output.contains("67890"), "Should not list programs from another instructor");

        System.setOut(System.out);
    }

    @Test
    @DisplayName("Add a new program successfully")
    void testAddProgram() throws IOException {
        // Simulate user input for adding a new program
        String userInput = "New Program Title\nNew Program Description\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));

        // Call the method to add a program
        Programs.addProgram("instructorTest");

        // Verify the contents of the programs file
        try (BufferedReader br = new BufferedReader(new FileReader(programsFile))) {
            String line = br.readLine();
            assertNotNull(line, "Expected a new line in the programs file after adding a program");
            String[] data = line.split(",", 5);
            assertEquals("instructorTest", data[1], "Instructor username should match");
            assertEquals("New Program Title", data[2], "Title should match user input");
            assertEquals("New Program Description", data[3], "Description should match user input");
            // data[0] is the generated ID (System.currentTimeMillis), data[4] is the creation date
        }

        // Reset System.in
        System.setIn(System.in);
    }

    @Test
    @DisplayName("Edit an existing program successfully")
    void testEditProgram() throws IOException {
        // Write an existing program for instructorX
        String existingProgramId = "99999";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(programsFile))) {
            bw.write(existingProgramId + ",instructorX,Old Title,Old Desc,Old Date\n");
        }

        // Simulate user input to edit the program
        String userInput = existingProgramId + "\nNew Title\nNew Description\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));

        Programs.editProgram("instructorX");

        try (BufferedReader br = new BufferedReader(new FileReader(programsFile))) {
            String line = br.readLine();
            assertNotNull(line, "Expected the updated line in the programs file");
            String[] data = line.split(",", 5);
            assertEquals("99999", data[0], "ID should remain unchanged");
            assertEquals("instructorX", data[1], "Instructor username should remain the same");
            assertEquals("New Title", data[2], "The updated title should match");
            assertEquals("New Description", data[3], "The updated description should match");
            assertEquals("Old Date", data[4], "Creation date should remain unchanged");
        }

        System.setIn(System.in);
    }

    @Test
    @DisplayName("Fail to edit a program (not found or unauthorized)")
    void testEditProgramNotFoundOrUnauthorized() throws IOException {
        // Write a program belonging to a different instructor
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(programsFile))) {
            bw.write("44444,instructorA,TitleA,DescA,DateA\n");
        }

        // Simulate user input attempting to edit the above program as instructorY
        String userInput = "44444\nNew Title\nNew Description\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        Programs.editProgram("instructorY");

        String output = outContent.toString();
        assertTrue(output.contains("Program not found or unauthorized action."),
                   "Expected a message indicating the program is either not found or unauthorized");

        // Ensure the original program remains unchanged
        try (BufferedReader br = new BufferedReader(new FileReader(programsFile))) {
            String line = br.readLine();
            assertNotNull(line);
            assertTrue(line.contains("TitleA"), "Title should remain unchanged");
            assertTrue(line.contains("DescA"), "Description should remain unchanged");
        }

        System.setIn(System.in);
        System.setOut(System.out);
    }

    @Test
    @DisplayName("Delete a program successfully")
    void testDeleteProgram() throws IOException {
        // Add a program for instructorZ
        String programId = "55555";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(programsFile))) {
            bw.write(programId + ",instructorZ,TitleZ,DescZ,DateZ\n");
        }

        // Simulate user input
        String userInput = programId + "\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        Programs.deleteProgram("instructorZ");
        String output = outContent.toString();
        assertTrue(output.contains("Program with ID 55555 deleted successfully."),
                   "Expected a success message for deleting program with ID 55555");

        // Verify the program no longer exists in the file
        try (BufferedReader br = new BufferedReader(new FileReader(programsFile))) {
            String line = br.readLine();
            assertNull(line, "Expected no lines left after deletion");
        }

        System.setIn(System.in);
        System.setOut(System.out);
    }

    @Test
    @DisplayName("Fail to delete a program (not found or unauthorized)")
    void testDeleteProgramNotFoundOrUnauthorized() throws IOException {
        // Add a program for instructorX
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(programsFile))) {
            bw.write("33333,instructorX,TitleX,DescX,DateX\n");
        }

        // Simulate user input
        String userInput = "33333\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Attempt to delete as a different instructor
        Programs.deleteProgram("instructorY");
        String output = outContent.toString();
        assertTrue(output.contains("Program not found or unauthorized action."),
                   "Expected a 'not found or unauthorized' message");

        // Verify the program still exists
        try (BufferedReader br = new BufferedReader(new FileReader(programsFile))) {
            String line = br.readLine();
            assertNotNull(line, "The program should still exist");
            assertTrue(line.contains("33333"), "The program should not have been deleted");
        }

        System.setIn(System.in);
        System.setOut(System.out);
    }

    @Test
    @DisplayName("View subscribers of an instructor's programs")
    void testViewSubscribers() throws IOException {
        // Write some subscription data
        // Format: programId, instructorUsername, subscriberUsername
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(subscriptionsFile))) {
            bw.write("P111,instructorA,subUser1\n");
            bw.write("P222,instructorB,subUser2\n");
            bw.write("P333,instructorA,subUser3\n");
        }

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        Programs.viewSubscribers("instructorA");
        String output = outContent.toString();

        assertTrue(output.contains("P111"), "Expected to see subscriber info for P111");
        assertTrue(output.contains("subUser1"), "Expected to see subUser1");
        assertTrue(output.contains("P333"), "Expected to see subscriber info for P333");
        assertTrue(output.contains("subUser3"), "Expected to see subUser3");
        assertFalse(output.contains("P222"), "Should not show info for another instructor's program");

        System.setOut(System.out);
    }

    @Test
    @DisplayName("View subscribers when no subscribers exist for the instructor")
    void testViewSubscribersNoData() throws IOException {
        // Add a subscription for a different instructor
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(subscriptionsFile))) {
            bw.write("P999,instructorX,subUserX\n");
        }

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        Programs.viewSubscribers("instructorY");
        String output = outContent.toString();
        assertTrue(output.contains("No subscribers found for your programs."),
                   "Expected a 'no subscribers found' message for instructorY");

        System.setOut(System.out);
    }
}
