package najah.edu.acceptance_tests;
// JUnit 5
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import com.example.fitness.Main;
import com.example.fitness.ProgramDashboardClient;
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

