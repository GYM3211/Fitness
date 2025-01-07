package najah.edu.acceptance_tests;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import com.example.fitness.Main;
import com.example.fitness.UserManagement;

import java.io.*;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserManagementTest {

    @TempDir
    static Path tempDir;

    private static File usersFile;
    private static File usersTempFile;

    @BeforeAll
    static void setupAll() {
        // We point Main.USERS_FILE and Main.USERS_TEMP_FILE to temp files
        usersFile = tempDir.resolve("users.txt").toFile();
        usersTempFile = tempDir.resolve("users_temp.txt").toFile();

        Main.USERS_FILE = usersFile.getAbsolutePath();
        Main.USERS_TEMP_FILE = usersTempFile.getAbsolutePath();

      
    }

    @BeforeEach
    void setupEach() throws IOException {
        // Make sure we start each test with empty files
        if (!usersFile.exists()) {
            usersFile.createNewFile();
        } else {
            new PrintWriter(usersFile).close();
        }

        if (!usersTempFile.exists()) {
            usersTempFile.createNewFile();
        } else {
            new PrintWriter(usersTempFile).close();
        }
    }

    @Test
    @Order(1)
    @DisplayName("manageUsers() minimal check => choose 5 to exit immediately")
    void testManageUsers_exit() {
        // We'll just do "5" => which means "Back to Dashboard"
        String input = "5\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        UserManagement.manageUsers();

        System.setOut(System.out);
        System.setIn(System.in);

        String output = out.toString();
        assertTrue(output.contains("Returning to Dashboard..."),
                   "Should mention returning to dashboard after choosing 5");
    }

    @Test
    @Order(2)
    @DisplayName("manageUsers() => invalid input => error, then choose 5 => exit")
    void testManageUsers_invalidInput() {
        // "abc" => triggers "Invalid input!", then "5" => exit
        String input = "abc\n5\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        UserManagement.manageUsers();

        System.setOut(System.out);
        System.setIn(System.in);

        String output = out.toString();
        assertTrue(output.contains("Invalid input! Please enter a number."),
                   "Should warn about invalid input");
        assertTrue(output.contains("Returning to Dashboard..."),
                   "Should eventually exit with choice 5");
    }

    @Test
    @Order(3)
    @DisplayName("viewAllUsers() => no users in file => shows header (or empty message)")
    void testViewAllUsers_empty() {
        // Capture console output
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // Call viewAllUsers() directly, bypassing the manageUsers() menu.
        UserManagement.viewAllUsers();

        // Restore standard output
        System.setOut(System.out);

        // Convert captured output to string
        String output = out.toString();

        // If the file is empty, the code prints the header lines but no user lines.
        // Adjust your assertion(s) as needed, for example:
        assertTrue(
            output.contains("------ All Registered Users ------"),
            "Should print the user header even when no users exist."
        );
    }

    @Test
    @Order(4)
    @DisplayName("createUser() => success => writes new user to file")
    void testCreateUser_success() throws IOException {
        // Provide user input for each prompt
        //  username, password, role, gender, age, subscription
        // We must provide 6 lines:
        String input =
            "johnDoe\n" +      // username
            "secretPass\n" +   // password
            "client\n" +       // role
            "male\n" +         // gender
            "30\n" +           // age
            "premium\n";       // subscription
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // Call method
        // Or choose "4" in manageUsers() => but let's do it directly
        UserManagement.createUser();

        System.setOut(System.out);
        System.setIn(System.in);

        String output = out.toString();
        assertTrue(output.contains("User created successfully!"),
                   "Should see success message");

        // Check the file
        try (BufferedReader br = new BufferedReader(new FileReader(usersFile))) {
            String line = br.readLine();
            assertNotNull(line, "Should have 1 user line in the file");
            // Format: username, hashedPassword, role, gender, age, status, subscription
            String[] parts = line.split(",");
            assertEquals("johnDoe", parts[0], "Should store the username");
            assertNotNull(parts[1], "The stored password hash should not be null.");
            assertNotEquals("secretPass", parts[1], "Should not store the plaintext password.");
            assertEquals(64, parts[1].length(), "SHA-256 hash should be 64 hex chars long.");
            assertEquals("client", parts[2], "Role should be 'client'");
            assertEquals("male", parts[3], "Gender should be 'male'");
            assertEquals("30", parts[4], "Age should be '30'");
            assertEquals("pending", parts[5], "User status should be 'pending'");
            assertEquals("premium", parts[6], "Subscription should be 'premium'");
        }
    }

    @Test
    @Order(5)
    @DisplayName("approveUser() => user in pending => changed to accepted")
    void testApproveUser_pendingBecomesAccepted() throws IOException {
        // We'll create a file with 1 user in "pending"
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(usersFile))) {
            bw.write("alice,hash,client,female,25,pending,basic\n");
        }

        String input = "alice\n"; // user to approve
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        UserManagement.approveUser();

        System.setOut(System.out);
        System.setIn(System.in);

        String output = out.toString();
        assertTrue(output.contains("User has been approved successfully."),
                   "Should confirm the user was approved");
        assertTrue(output.contains("User approval changes have been saved successfully."),
                   "Should confirm changes saved");

        // Check the file => "pending" => "accepted"
        try (BufferedReader br = new BufferedReader(new FileReader(usersFile))) {
            String line = br.readLine();
            assertNotNull(line);
            String[] parts = line.split(",");
            assertEquals("alice", parts[0]);
            assertEquals("accepted", parts[5].trim(), "Should now be accepted");
        }
    }

    @Test
    @Order(6)
    @DisplayName("approveUser() => user not found => message shown")
    void testApproveUser_notFound() throws IOException {
        // File has no 'alice'
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(usersFile))) {
            bw.write("bob,hash,client,male,30,pending,basic\n");
        }

        String input = "alice\n"; // user doesn't exist
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        UserManagement.approveUser();

        System.setOut(System.out);
        System.setIn(System.in);

        String outStr = out.toString();
        assertTrue(outStr.contains("User not found in the system."),
                   "Should mention user not found");
        assertTrue(outStr.contains("User approval changes have been saved successfully."),
                   "Should finalize changes anyway, though no user changed");
    }

    @Test
    @Order(7)
    @DisplayName("denyUser() => pending or accepted => changed to denied")
    void testDenyUser_pendingOrAccepted() throws IOException {
        // We'll store 2 users: one is pending, the other is accepted
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(usersFile))) {
            bw.write("charlie,hash,client,male,22,pending,basic\n");
            bw.write("david,hash,client,male,40,accepted,premium\n");
        }

        // We'll test denying "charlie"
        String input = "charlie\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        UserManagement.denyUser();

        System.setOut(System.out);
        System.setIn(System.in);

        String outStr = out.toString();
        assertTrue(outStr.contains("User has been denied successfully."),
                   "Should confirm user was denied");
        assertTrue(outStr.contains("User denial changes have been saved successfully."),
                   "Should mention changes saved");

        // Check that "charlie" is now "denied", but david is still "accepted"
        try (BufferedReader br = new BufferedReader(new FileReader(usersFile))) {
            String line1 = br.readLine();
            String line2 = br.readLine();
            assertNotNull(line1);
            assertNotNull(line2);
            String[] c = line1.split(",");
            String[] d = line2.split(",");
            assertEquals("charlie", c[0]);
            assertEquals("denied", c[5].trim());
            assertEquals("david", d[0]);
            assertEquals("accepted", d[5].trim());
        }
    }

    @Test
    @Order(8)
    @DisplayName("denyUser() => not found => message shown")
    void testDenyUser_notFound() throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(usersFile))) {
            bw.write("eva,hash,client,female,29,pending,basic\n");
        }

        String input = "frank\n"; // does not exist
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        UserManagement.denyUser();

        System.setOut(System.out);
        System.setIn(System.in);

        String outStr = out.toString();
        assertTrue(outStr.contains("User not found in the system."),
                   "Should mention user not found");
        assertTrue(outStr.contains("User denial changes have been saved successfully."),
                   "Should mention changes saved");
    }
}
