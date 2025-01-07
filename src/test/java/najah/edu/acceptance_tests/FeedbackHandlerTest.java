package najah.edu.acceptance_tests;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import com.example.fitness.FeedbackHandler;
import com.example.fitness.Main;

import java.io.*;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FeedbackHandlerTest {

    @TempDir
    static Path tempDir;

    private static File feedbackFile;

    @BeforeAll
    static void setupAll() {
        feedbackFile = tempDir.resolve("feedback.txt").toFile();
        Main.FEEDBACK = feedbackFile.getAbsolutePath();
    }

    @BeforeEach
    void clearFile() throws IOException {
        if (!feedbackFile.exists()) {
            feedbackFile.createNewFile();
        } else {
            new PrintWriter(feedbackFile).close(); 
        }
    }

    @Test
    @Order(1)
    @DisplayName("sendFeedback(): success => new line in file")
    void testSendFeedback_success() throws IOException {
        // Provide instructor username + message
        String input = 
            "instructorA\n" +
            "Hello instructor, nice session.\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outStream));

        FeedbackHandler.sendFeedback("clientX", "Instructor");

        System.setOut(System.out);
        System.setIn(System.in);

        String consoleOutput = outStream.toString();
        assertTrue(consoleOutput.contains("Feedback sent successfully!"),
                   "Should confirm feedback was sent.");

        try (BufferedReader br = new BufferedReader(new FileReader(feedbackFile))) {
            String line = br.readLine();
            assertNotNull(line, "File should have 1 line");
            // Format => ID, instructorUsername, clientUsername, message, date
            String[] parts = line.split(",", 5);

            // Because we now do .trim() in the code, parts[1] or [3] won't have trailing spaces
            assertEquals("instructorA", parts[1]);
            assertEquals("clientX", parts[2]);

            // Also ensure we have the correct message, exactly, without trailing spaces:
            assertEquals("Hello instructor, nice session.", parts[3],
                         "Feedback message should match exactly");
        }
    }

    @Test
    @Order(2)
    @DisplayName("viewAllFeedback(): no data => warns user")
    void testViewAllFeedback_empty() {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outStream));

        FeedbackHandler.viewAllFeedback("clientX");

        System.setOut(System.out);
        String output = outStream.toString();
        assertTrue(output.contains("No feedback found for this user."),
                   "Should say no feedback found");
    }

    @Test
    @Order(3)
    @DisplayName("viewAllFeedback(): user matches some feedback => shows it")
    void testViewAllFeedback_matches() throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(feedbackFile))) {
            bw.write("F001,instructorA,clientX,Hi there,2025-01-01\n");
            bw.write("F002,instructorB,clientY,Hello,2025-01-02\n");
            bw.write("F003,instructorA,clientZ,What's up,2025-01-03\n");
        }

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outStream));

        FeedbackHandler.viewAllFeedback("clientX");

        System.setOut(System.out);
        String output = outStream.toString();
        assertTrue(output.contains("Feedback ID: F001"), "Should see ID=F001");
        assertTrue(output.contains("Instructor: instructorA"), "Should see instructorA");
        assertTrue(output.contains("Client: clientX"), "Should see clientX");
        assertTrue(output.contains("Message: Hi there"), "Should see message 'Hi there'");
        // Ensure we do NOT see F002 or F003
        assertFalse(output.contains("F002") || output.contains("F003"),
                    "Should not show feedback for other users");
    }

    @Test
    @Order(4)
    @DisplayName("showFeedbackDashboard(): choose 3 => exit")
    void testShowFeedbackDashboard_exit() {
        String input = "3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outStream));

        FeedbackHandler.showFeedbackDashboard("clientABC", "Instructor");

        System.setOut(System.out);
        System.setIn(System.in);

        String consoleOutput = outStream.toString();
        // If user picks "3", we exit the loop. No "Invalid choice" lines expected
        assertFalse(consoleOutput.contains("Invalid choice"),
                    "Should not say invalid choice if user picks 3");
    }

    @Test
    @Order(5)
    @DisplayName("showFeedbackDashboard(): send feedback then exit")
    void testShowFeedbackDashboard_sendFeedback() throws IOException {
        // 4 lines for the code:
        //  1) "2" => Add New Feedback
        //  2) "instructorZ" => instructor's username
        //  3) "Great job" => feedback message
        //  4) "3" => exit
        String input = 
            "2\n" +
            "instructorZ\n" +
            "Great job\n" +
            "3\n";

        System.setIn(new ByteArrayInputStream(input.getBytes()));
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outStream));

        FeedbackHandler.showFeedbackDashboard("clientABC", "Instructor");

        System.setOut(System.out);
        System.setIn(System.in);

        String output = outStream.toString();
        assertTrue(output.contains("Feedback sent successfully!"),
                   "Should confirm feedback was added");

        try (BufferedReader br = new BufferedReader(new FileReader(feedbackFile))) {
            String line = br.readLine();
            assertNotNull(line, "File should have 1 line of feedback");
            String[] parts = line.split(",", 5);
            assertEquals("instructorZ", parts[1], "Should store 'instructorZ' as the target");
            assertEquals("clientABC", parts[2], "Should store 'clientABC' as the sender");
            assertEquals("Great job", parts[3], "Should store the message 'Great job'");
        }
    }
}
