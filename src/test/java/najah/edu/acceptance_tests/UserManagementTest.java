package najah.edu.acceptance_tests;
import org.junit.jupiter.api.*;

import com.example.fitness.UserManagement;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.util.*;

	 public class UserManagementTest {

	    private static final String USERS_FILE = "test_users.txt";

	    @BeforeAll
	    static void setup() throws IOException {
	        new File(USERS_FILE).createNewFile();
	    }

	    @BeforeEach
	    void prepareFiles() throws IOException {
	        try (FileWriter writer = new FileWriter(USERS_FILE)) {
	            writer.write("testUser1,hashedPass,instructor,male,25,pending,basic\n");
	            writer.write("testUser2,hashedPass,client,female,30,accepted,premium\n");
	        }
	    }

	    @AfterAll
	    static void cleanup() {
	        new File(USERS_FILE).delete();
	    }

	    @Test
	    void testViewAllUsers_PopulatedList() {
	        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	        System.setOut(new PrintStream(outContent));

	        UserManagement.viewAllUsers();

	        String output = outContent.toString();
	        assertTrue(output.contains("testUser1"), "Should display testUser1");
	        assertTrue(output.contains("testUser2"), "Should display testUser2");
	    }

	    @Test
	    void testViewAllUsers_EmptyList() throws IOException {
	    	 try (FileWriter writer = new FileWriter(USERS_FILE)) {
	    	        writer.write(""); // Clear the file
	    	    }

	    	    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	    	    System.setOut(new PrintStream(outContent));

	    	    UserManagement.viewAllUsers();

	    	    String output = outContent.toString();
	    	    assertTrue(output.contains("------ All Registered Users ------"), "Should display the headers");
	    	    assertTrue(output.contains("Username\tUser Type\tGender\tAge\tAccount Status\tSubscription"), "Should display the headers");
	    	    assertFalse(output.contains("testUser1"), "Should not display any users");
	    }

	    @Test
	    void testApproveUser() throws IOException {
	        String input = "testUser1\n"; // Simulate input for username
	        System.setIn(new ByteArrayInputStream(input.getBytes()));

	        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	        System.setOut(new PrintStream(outContent));

	        UserManagement.approveUser();

	        String output = outContent.toString();
	        assertTrue(output.contains("User has been approved successfully"), "Should indicate successful approval");

	        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
	            String line;
	            boolean approved = false;
	            while ((line = reader.readLine()) != null) {
	                if (line.contains("testUser1") && line.contains("accepted")) {
	                    approved = true;
	                    break;
	                }
	            }
	            assertTrue(approved, "User status should be updated to 'accepted'");
	        }
	    }

	    @Test
	    void testDenyUser() throws IOException {
	        String input = "testUser2\n"; // Simulate input for username
	        System.setIn(new ByteArrayInputStream(input.getBytes()));

	        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	        System.setOut(new PrintStream(outContent));

	        UserManagement.denyUser();

	        String output = outContent.toString();
	        assertTrue(output.contains("User has been denied successfully"), "Should indicate successful denial");

	        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
	            String line;
	            boolean denied = false;
	            while ((line = reader.readLine()) != null) {
	                if (line.contains("testUser2") && line.contains("denied")) {
	                    denied = true;
	                    break;
	                }
	            }
	            assertTrue(denied, "User status should be updated to 'denied'");
	        }
	    }

	    @Test
	    void testCreateUser() throws IOException {
	        String input = "newUser\npassword123\nclient\nfemale\n28\npremium\n"; // Simulate input for creating a user
	        System.setIn(new ByteArrayInputStream(input.getBytes()));

	        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	        System.setOut(new PrintStream(outContent));

	        UserManagement.createUser();

	        String output = outContent.toString();
	        assertTrue(output.contains("User created successfully"), "Should indicate successful creation");

	        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
	            String line;
	            boolean userCreated = false;
	            while ((line = reader.readLine()) != null) {
	                if (line.contains("newUser")) {
	                    userCreated = true;
	                    break;
	                }
	            }
	            assertTrue(userCreated, "New user should be added to the system");
	        }
	    }
}
