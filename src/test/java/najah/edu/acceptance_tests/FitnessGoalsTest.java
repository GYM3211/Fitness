package najah.edu.acceptance_tests;


import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import com.example.fitness.FitnessGoals;
import com.example.fitness.Main;

import java.io.*;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FitnessGoalsTest {

    @TempDir
    static Path tempDir;

    private static File goalsFile;

    @BeforeAll
    static void setupAll() {
        goalsFile = tempDir.resolve("fitness_goals.txt").toFile();
        // Point Main.FITNESS_GOALS_FILE to that file
        Main.FITNESS_GOALS_FILE = goalsFile.getAbsolutePath();
    }

    @BeforeEach
    void clearFile() throws IOException {
        if (!goalsFile.exists()) {
            goalsFile.createNewFile();
        } else {
            new PrintWriter(goalsFile).close(); // Empties file
        }
    }

    // ----------------------------------------------------------------
    // 1) viewFitnessGoals() => no file or empty => warns user
    // ----------------------------------------------------------------
    @Test
    @Order(1)
    @DisplayName("viewFitnessGoals(): empty => 'No fitness goals found'")
    void testViewFitnessGoals_empty() {
        // The file is empty
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outStream));

        FitnessGoals.viewFitnessGoals("johnUser");

        System.setOut(System.out);
        String output = outStream.toString();
        assertTrue(output.contains("Your Fitness Goals:"),
                   "Should show header");
        assertTrue(output.contains("No fitness goals found for this username."),
                   "Should say no goals found since file is empty");
    }

    // ----------------------------------------------------------------
    // 2) addFitnessGoal() => success => new line in file
    // ----------------------------------------------------------------
    @Test
    @Order(2)
    @DisplayName("addFitnessGoal(): success => new line appended")
    void testAddFitnessGoal_success() throws IOException {
        // We provide user input for the goal
        String input = "Lose 5 kg in 2 months\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outStream));

        // The user is "johnUser"
        FitnessGoals.addFitnessGoal("johnUser");

        System.setOut(System.out);
        System.setIn(System.in);

        String output = outStream.toString();
        assertTrue(output.contains("Fitness goal added successfully with ID"),
                   "Should confirm the goal was added");

        // Check the file
        try (BufferedReader br = new BufferedReader(new FileReader(goalsFile))) {
            String line = br.readLine();
            assertNotNull(line, "File should have 1 line now");
            String[] parts = line.split(",", 3);
            // parts[0] is the ID, parts[1] = 'johnUser', parts[2] = 'Lose 5 kg in 2 months'
            assertEquals("johnUser", parts[1], "Should store correct username");
            assertEquals("Lose 5 kg in 2 months", parts[2], "Should store the goal text");
        }
    }

    // ----------------------------------------------------------------
    // 3) viewFitnessGoals() => user has some goals => show them
    // ----------------------------------------------------------------
    @Test
    @Order(3)
    @DisplayName("viewFitnessGoals(): user has goals => displays them")
    void testViewFitnessGoals_withData() throws IOException {
        // We'll add 3 lines for different users
        // Format: ID, username, goal
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(goalsFile))) {
            bw.write("G001,johnUser,Gain muscle\n");
            bw.write("G002,janeUser,Run 5k\n");
            bw.write("G003,johnUser,Track daily steps\n");
        }

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outStream));

        FitnessGoals.viewFitnessGoals("johnUser");

        System.setOut(System.out);
        String output = outStream.toString();
        // Should show "Your Fitness Goals:" header
        assertTrue(output.contains("Your Fitness Goals:"),
                   "Should print the header");
        // Should see "Gain muscle" and "Track daily steps" but not "Run 5k"
        assertTrue(output.contains("Gain muscle"), "Should show 'Gain muscle'");
        assertTrue(output.contains("Track daily steps"), "Should show 'Track daily steps'");
        assertFalse(output.contains("Run 5k"),
                    "Should not list other user's goals (janeUser)");
    }

    // ----------------------------------------------------------------
    // 4) deleteFitnessGoal() => not found => no changes
    // ----------------------------------------------------------------
    @Test
    @Order(4)
    @DisplayName("deleteFitnessGoal(): goal not found => warns user")
    void testDeleteFitnessGoal_notFound() throws IOException {
        // We'll add one line for user "johnUser"
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(goalsFile))) {
            bw.write("G999,johnUser,Do push-ups\n");
        }

        // We try to delete "G123"
        String input = "G123\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outStream));

        FitnessGoals.deleteFitnessGoal("johnUser");

        System.setOut(System.out);
        System.setIn(System.in);

        String output = outStream.toString();
        assertTrue(output.contains("No matching fitness goal found"),
                   "Should warn that goal ID not found");
        
        // The file should remain the same
        try (BufferedReader br = new BufferedReader(new FileReader(goalsFile))) {
            String line = br.readLine();
            assertTrue(line.contains("G999"), "Should still have the original goal");
            assertNull(br.readLine(), "No more lines");
        }
    }

    // ----------------------------------------------------------------
    // 5) deleteFitnessGoal() => found => removed from file
    // ----------------------------------------------------------------
    @Test
    @Order(5)
    @DisplayName("deleteFitnessGoal(): user has a goal => successfully removed")
    void testDeleteFitnessGoal_success() throws IOException {
        // We'll create 2 goals for "johnUser", plus 1 for "anotherUser"
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(goalsFile))) {
            bw.write("ABC123,johnUser,Lose weight\n");
            bw.write("XYZ999,anotherUser,Swim daily\n");
            bw.write("DEF456,johnUser,Build stamina\n");
        }

        // We'll delete "ABC123" for "johnUser"
        String input = "ABC123\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outStream));

        FitnessGoals.deleteFitnessGoal("johnUser");

        System.setOut(System.out);
        System.setIn(System.in);

        String output = outStream.toString();
        assertTrue(output.contains("Goal ID ABC123 for user johnUser has been deleted successfully."),
                   "Should confirm goal is deleted");

        // Check file: only "XYZ999,anotherUser,Swim daily" and "DEF456,johnUser,Build stamina" should remain
        try (BufferedReader br = new BufferedReader(new FileReader(goalsFile))) {
            String line1 = br.readLine();
            String line2 = br.readLine();
            assertNull(br.readLine(), "Should have only 2 lines now");
            assertNotNull(line1);
            assertNotNull(line2);
            assertTrue(line1.contains("XYZ999") || line1.contains("DEF456"), 
                       "Should keep these lines, not ABC123");
            assertTrue(line2.contains("XYZ999") || line2.contains("DEF456"), 
                       "Should keep these lines, not ABC123");
        }
    }
}
