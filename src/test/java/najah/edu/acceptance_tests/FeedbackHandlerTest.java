package najah.edu.acceptance_tests;


import com.example.fitness.FeedbackHandler;
import com.example.fitness.Main;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FeedbackHandlerTest {
	  @TempDir
	    static Path tempDir;

	    private static File feedbackFile;

	    @BeforeAll
	    static void setupAll() {
	        // Redirect FeedbackHandler to use a temp file for testing
	        feedbackFile = tempDir.resolve("feedback.csv").toFile();
	        FeedbackHandler.FEEDBACK_FILE = feedbackFile.getAbsolutePath();
	    }

	    @BeforeEach
	    void clearFile() throws IOException {
	        // Ensure the file is empty for each test
	        if (!feedbackFile.exists()) {
	            feedbackFile.createNewFile();
	        } else {
	            new PrintWriter(feedbackFile).close();
	        }
	    }

	    // Test for sendFeedback() (Interactive Version)
	    @Test
	    @Order(1)
	    @DisplayName("sendFeedback(): writes feedback to file")
	    void testSendFeedbackInteractive() throws IOException {
	        String input = "Instructor123\nGreat session!\n";
	        System.setIn(new ByteArrayInputStream(input.getBytes()));

	        FeedbackHandler.sendFeedback("Client123", "Instructor");

	        System.setIn(System.in); // Reset System.in

	        try (BufferedReader br = new BufferedReader(new FileReader(feedbackFile))) {
	            String line = br.readLine();
	            assertNotNull(line, "Feedback should be written to the file");
	            String[] parts = line.split(",", 5);
	            assertEquals("Instructor123", parts[1], "Instructor username should match input");
	            assertEquals("Client123", parts[2], "Client username should match input");
	            assertEquals("Great session!", parts[3], "Feedback message should match input");
	        }
	    }

	    // Test for sendFeedback() (Overloaded Non-Interactive Version)
	    @Test
	    @Order(2)
	    @DisplayName("sendFeedback(): writes feedback to file with parameters")
	    void testSendFeedbackNonInteractive() throws IOException {
	        FeedbackHandler.sendFeedback("Instructor123", "Client123", "Excellent work!");

	        try (BufferedReader br = new BufferedReader(new FileReader(feedbackFile))) {
	            String line = br.readLine();
	            assertNotNull(line, "Feedback should be written to the file");
	            String[] parts = line.split(",", 5);
	            assertEquals("Instructor123", parts[1]);
	            assertEquals("Client123", parts[2]);
	            assertEquals("Excellent work!", parts[3]);
	        }
	    }

	    // Test for viewAllFeedback()
	    @Test
	    @Order(3)
	    @DisplayName("viewAllFeedback(): displays feedback for user")
	    void testViewAllFeedback() throws IOException {
	        try (BufferedWriter bw = new BufferedWriter(new FileWriter(feedbackFile))) {
	            bw.write("1,Instructor123,Client123,Great session!,2025-01-01\n");
	            bw.write("2,Instructor456,Client123,Good job!,2025-01-02\n");
	            bw.write("3,Instructor123,AnotherClient,Nice work!,2025-01-03\n");
	        }

	        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
	        System.setOut(new PrintStream(outStream));

	        FeedbackHandler.viewAllFeedback("Client123");

	        System.setOut(System.out); // Reset System.out

	        String output = outStream.toString();
	        assertTrue(output.contains("Feedback ID: 1"), "Should display feedback ID 1");
	        assertTrue(output.contains("Feedback ID: 2"), "Should display feedback ID 2");
	        assertTrue(output.contains("Great session!"), "Should display feedback message 1");
	        assertTrue(output.contains("Good job!"), "Should display feedback message 2");
	        assertFalse(output.contains("Feedback ID: 3"), "Should not display feedback for other clients");
	    }

	    // Test for getAllFeedback()
	    @Test
	    @Order(4)
	    @DisplayName("getAllFeedback(): retrieves feedback lines for user")
	    void testGetAllFeedback() throws IOException {
	        try (BufferedWriter bw = new BufferedWriter(new FileWriter(feedbackFile))) {
	            bw.write("1,Instructor123,Client123,Great session!,2025-01-01\n");
	            bw.write("2,Instructor456,Client123,Good job!,2025-01-02\n");
	            bw.write("3,Instructor123,AnotherClient,Nice work!,2025-01-03\n");
	        }

	        List<String> feedback = FeedbackHandler.getAllFeedback("Client123");
	        assertEquals(2, feedback.size(), "Should return 2 feedback entries for Client123");
	        assertTrue(feedback.get(0).contains("Great session!"));
	        assertTrue(feedback.get(1).contains("Good job!"));
	    }

	    // Test for viewAllFeedback() when no feedback exists
	    @Test
	    @Order(5)
	    @DisplayName("viewAllFeedback(): no feedback found")
	    void testViewAllFeedbackNoFeedback() {
	        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
	        System.setOut(new PrintStream(outStream));

	        FeedbackHandler.viewAllFeedback("NonexistentUser");

	        System.setOut(System.out);

	        String output = outStream.toString();
	        assertTrue(output.contains("No feedback found for this user."));
	    }

	    // Test for showFeedbackDashboard() - Invalid Choice
	    @Test
	    @Order(6)
	    @DisplayName("showFeedbackDashboard(): invalid choice")
	    void testShowFeedbackDashboardInvalidChoice() {
	        String input = "9\n3\n";
	        System.setIn(new ByteArrayInputStream(input.getBytes()));

	        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
	        System.setOut(new PrintStream(outStream));

	        FeedbackHandler.showFeedbackDashboard("Client123", "Instructor");

	        System.setIn(System.in);
	        System.setOut(System.out);

	        String output = outStream.toString();
	        assertTrue(output.contains("Invalid choice, please try again."));
	    }

	    // Test for showFeedbackDashboard() - Exit
	    @Test
	    @Order(7)
	    @DisplayName("showFeedbackDashboard(): exit option")
	    void testShowFeedbackDashboardExit() {
	        String input = "3\n";
	        System.setIn(new ByteArrayInputStream(input.getBytes()));

	        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
	        System.setOut(new PrintStream(outStream));

	        FeedbackHandler.showFeedbackDashboard("Client123", "Instructor");

	        System.setIn(System.in);
	        System.setOut(System.out);

	        String output = outStream.toString();
	        assertFalse(output.contains("Invalid choice"));
	    }
	    }
