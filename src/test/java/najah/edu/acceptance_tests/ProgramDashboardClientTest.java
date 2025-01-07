package najah.edu.acceptance_tests;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import com.example.fitness.Main;
import com.example.fitness.ProgramDashboardClient;

import java.io.*;
import java.nio.file.Path;
import java.util.logging.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProgramDashboardClientTest {
	 @TempDir
	    static Path tempDir;

	    private static File programsFile;
	    private static File subscriptionsFile;
	    private static File subscriptionsTempFile;

	    @BeforeAll
	    static void setUpAll() {
	        // Create references to the files in a temp directory
	        programsFile = tempDir.resolve("programs.txt").toFile();
	        subscriptionsFile = tempDir.resolve("subscriptions.txt").toFile();
	        subscriptionsTempFile = tempDir.resolve("subscriptions_temp.txt").toFile();

	        // Set them in Main
	        Main.PROGRAMS_FILE = programsFile.getAbsolutePath();
	        Main.SUBSCRIPTIONS_FILE = subscriptionsFile.getAbsolutePath();
	        Main.SUBSCRIPTIONS_TEMP_FILE = subscriptionsTempFile.getAbsolutePath();
	    }

	    @BeforeEach
	    void setUp() throws IOException {
	        // Ensure we start fresh each time
	        try (PrintWriter pw = new PrintWriter(programsFile)) {
	            // Clears the file
	        }
	        try (PrintWriter pw = new PrintWriter(subscriptionsFile)) {
	            // Clears the file
	        }
	        try (PrintWriter pw = new PrintWriter(subscriptionsTempFile)) {
	            // Clears the file
	        }
	    }

	    @Test
	    @Order(1)
	    @DisplayName("viewAllPrograms() - No programs available")
	    void testViewAllPrograms_Empty() {
	        // We do not write anything into programsFile => it's empty

	        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	        System.setOut(new PrintStream(outContent));

	        ProgramDashboardClient.viewAllPrograms();

	        System.setOut(System.out);
	        String output = outContent.toString();

	        // The code prints: "No programs available at the moment."
	        assertTrue(output.contains("No programs available at the moment."),
	                   "Expected a message about no programs available");
	    }

	    @Test
	    @Order(2)
	    @DisplayName("viewAllPrograms() - Some programs exist")
	    void testViewAllPrograms_Some() throws IOException {
	        // Write sample programs
	        try (BufferedWriter bw = new BufferedWriter(new FileWriter(programsFile))) {
	            // Format: programId, instructor, title, description, date
	            bw.write("P101,instrA,Title A,Desc A,2025-01-01\n");
	            bw.write("P202,instrB,Title B,Desc B,2025-02-02\n");
	        }

	        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	        System.setOut(new PrintStream(outContent));

	        ProgramDashboardClient.viewAllPrograms();

	        System.setOut(System.out);
	        String output = outContent.toString();

	        assertTrue(output.contains("Program ID: P101"), "Should show program P101");
	        assertTrue(output.contains("Title: Title A"), "Should show Title A");
	        assertTrue(output.contains("Instructor: instrA"), "Should show instrA");
	        assertTrue(output.contains("Description: Desc A"), "Should show Desc A");

	        assertTrue(output.contains("Program ID: P202"), "Should show program P202");
	        assertTrue(output.contains("Title: Title B"), "Should show Title B");
	        assertTrue(output.contains("Instructor: instrB"), "Should show instrB");
	    }

	    @Test
	    @Order(3)
	    @DisplayName("subscribeToProgram() - Program not found")
	    void testSubscribeToProgram_NotFound() {
	        // No programs in the file => any user attempt will fail "Program not found!"
	        String input = "XYZ999\n"; // user enters a programId that doesn't exist
	        System.setIn(new ByteArrayInputStream(input.getBytes()));

	        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	        System.setOut(new PrintStream(outContent));

	        ProgramDashboardClient.subscribeToProgram("client1");

	        System.setOut(System.out);
	        System.setIn(System.in);

	        String output = outContent.toString();
	        assertTrue(output.contains("Program not found! Please check the ID."),
	                   "Expected a 'not found' message for program");
	    }

	    @Test
	    @Order(4)
	    @DisplayName("subscribeToProgram() - Successful subscription")
	    void testSubscribeToProgram_Success() throws IOException {
	        // We'll add a single program to programsFile
	        try (BufferedWriter bw = new BufferedWriter(new FileWriter(programsFile))) {
	            bw.write("ABC123,instructorZ,Some Title,Desc,2025-03-03\n");
	        }

	        // The user will enter "ABC123" to subscribe
	        String input = "ABC123\n";
	        System.setIn(new ByteArrayInputStream(input.getBytes()));

	        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	        System.setOut(new PrintStream(outContent));

	        ProgramDashboardClient.subscribeToProgram("clientXYZ");

	        System.setOut(System.out);
	        System.setIn(System.in);

	        String output = outContent.toString();
	        assertTrue(output.contains("You have successfully subscribed to the program!"),
	                   "Expected success message for subscription");

	        // Check that it was written to the subscriptions file
	        try (BufferedReader br = new BufferedReader(new FileReader(subscriptionsFile))) {
	            String line = br.readLine();
	            assertNotNull(line, "Should have a line in subscriptions file");
	            assertEquals("clientXYZ,ABC123", line, "Subscription format should be 'clientUsername,programId'");
	        }
	    }

	    @Test
	    @Order(5)
	    @DisplayName("subscribeToProgram() - Already subscribed")
	    void testSubscribeToProgram_AlreadySubscribed() throws IOException {
	        // We'll add the program to programsFile
	        try (BufferedWriter bw = new BufferedWriter(new FileWriter(programsFile))) {
	            bw.write("DEF555,instructorK,Title Def,Desc,2025-04-04\n");
	        }

	        // We'll add a subscription for client1 => DEF555
	        try (BufferedWriter bw = new BufferedWriter(new FileWriter(subscriptionsFile))) {
	            bw.write("client1,DEF555\n");
	        }

	        // Then the user tries to subscribe again to "DEF555"
	        String input = "DEF555\n";
	        System.setIn(new ByteArrayInputStream(input.getBytes()));

	        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	        System.setOut(new PrintStream(outContent));

	        ProgramDashboardClient.subscribeToProgram("client1");

	        System.setOut(System.out);
	        System.setIn(System.in);

	        String output = outContent.toString();
	        assertTrue(output.contains("You are already subscribed to this program."),
	                   "Expected an 'already subscribed' message");
	    }

	    @Test
	    @Order(6)
	    @DisplayName("viewMyPrograms() - none subscribed")
	    void testViewMyPrograms_Empty() {
	        // subscriptionsFile is empty => user is not subscribed to anything
	        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	        System.setOut(new PrintStream(outContent));

	        ProgramDashboardClient.viewMyPrograms("clientX");

	        System.setOut(System.out);
	        String output = outContent.toString();
	        assertTrue(output.contains("You are not subscribed to any programs."),
	                   "Expected a 'not subscribed' message");
	    }

	    @Test
	    @Order(7)
	    @DisplayName("viewMyPrograms() - show subscribed programs")
	    void testViewMyPrograms_Some() throws IOException {
	        // We'll have 2 programs in the file
	        try (BufferedWriter bw = new BufferedWriter(new FileWriter(programsFile))) {
	            bw.write("PRG1,instructorA,TitleA,DescA,2025-01-01\n");
	            bw.write("PRG2,instructorB,TitleB,DescB,2025-02-02\n");
	            bw.write("PRG3,instructorC,TitleC,DescC,2025-03-03\n");
	        }
	        // The user is subscribed to PRG1 and PRG3
	        try (BufferedWriter bw = new BufferedWriter(new FileWriter(subscriptionsFile))) {
	            bw.write("clientX,PRG1\n");
	            bw.write("clientX,PRG3\n");
	            bw.write("otherClient,PRG2\n");
	        }

	        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	        System.setOut(new PrintStream(outContent));

	        ProgramDashboardClient.viewMyPrograms("clientX");

	        System.setOut(System.out);
	        String output = outContent.toString();
	        // We should see details for PRG1 and PRG3
	        assertTrue(output.contains("Program ID: PRG1"), "Should show PRG1 info");
	        assertTrue(output.contains("Title: TitleA"), "Should show TitleA");
	        assertTrue(output.contains("Instructor: instructorA"), "Should show instructorA");

	        assertTrue(output.contains("Program ID: PRG3"), "Should show PRG3 info");
	        assertTrue(output.contains("Title: TitleC"), "Should show TitleC");
	        assertTrue(output.contains("Instructor: instructorC"), "Should show instructorC");

	        // Should NOT show PRG2
	        assertFalse(output.contains("TitleB"), "Should not show PRG2 from otherClient");
	    }

	    @Test
	    @Order(8)
	    @DisplayName("unsubscribeFromProgram() - no matching subscription")
	    void testUnsubscribe_NoMatch() throws IOException {
	        // We'll have a single subscription for someone else
	        try (BufferedWriter bw = new BufferedWriter(new FileWriter(subscriptionsFile))) {
	            bw.write("clientABC,PRG999\n");
	        }

	        // user tries to unsubscribe from PRG999 => doesn't match clientX
	        String input = "PRG999\n";
	        System.setIn(new ByteArrayInputStream(input.getBytes()));

	        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	        System.setOut(new PrintStream(outContent));

	        ProgramDashboardClient.unsubscribeFromProgram("clientX");

	        System.setOut(System.out);
	        System.setIn(System.in);

	        String output = outContent.toString();
	        assertTrue(output.contains("No subscription found for this client to the program with ID: PRG999"),
	                   "Expected 'No subscription found' message");
	    }

	    @Test
	    @Order(9)
	    @DisplayName("unsubscribeFromProgram() - success")
	    void testUnsubscribe_Success() throws IOException {
	        // We'll store 2 subscriptions for the same user => clientY
	        // Only one matches the program the user will unsubscribe from
	        try (BufferedWriter bw = new BufferedWriter(new FileWriter(subscriptionsFile))) {
	            bw.write("clientY,ABC100\n");
	            bw.write("clientY,XYZ200\n");
	        }

	        // user unsubscribes from "XYZ200"
	        String input = "XYZ200\n";
	        System.setIn(new ByteArrayInputStream(input.getBytes()));

	        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	        System.setOut(new PrintStream(outContent));

	        ProgramDashboardClient.unsubscribeFromProgram("clientY");

	        System.setOut(System.out);
	        System.setIn(System.in);

	        String output = outContent.toString();
	        assertTrue(output.contains("You have successfully unsubscribed from the program."),
	                   "Expected success message for unsubscribing");

	        // Check the new subscriptions file => only "clientY,ABC100" should remain
	        try (BufferedReader br = new BufferedReader(new FileReader(subscriptionsFile))) {
	            String line1 = br.readLine();
	            assertNotNull(line1, "We should have at least one line left");
	            assertEquals("clientY,ABC100", line1, "We expect the unsubscribed line removed");
	            assertNull(br.readLine(), "No more lines after that");
	        }
	    }

	    @Test
	    @Order(10)
	    @DisplayName("showProgramsDashboard() - minimal test (choose '5' to exit immediately)")
	    void testShowProgramsDashboardExit() {
	        // We'll feed a single input "5" => triggers the 'Exit Dashboard'
	        String input = "5\n";
	        System.setIn(new ByteArrayInputStream(input.getBytes()));

	        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	        System.setOut(new PrintStream(outContent));

	        // Just call with some username
	        ProgramDashboardClient.showProgramsDashboard("testClient");

	        System.setOut(System.out);
	        System.setIn(System.in);

	        String output = outContent.toString();
	        // We expect to see "Exiting Programs Dashboard..."
	        assertTrue(output.contains("Exiting Programs Dashboard..."),
	                   "Should show exit message after choosing option 5");
	    }

}
