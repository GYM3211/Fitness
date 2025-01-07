package najah.edu.acceptance_tests;
import org.junit.jupiter.api.*;
import com.example.fitness.Main;
import com.example.fitness.Registration;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

class RegistrationTest {

    private static final String TEST_USERS_FILE = "test_users.txt";

    @BeforeAll
    static void setup() {
        // Replace the USERS_FILE in Main with a test file
        Main.USERS_FILE = TEST_USERS_FILE;
    }

    @AfterEach
    void cleanUp() {
        // Delete the test file after each test
        File file = new File(TEST_USERS_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void testHashPassword() {
        String password = "test123";
        String expectedHash = "ecd71870d1963316a97e3ac3408c9835ad8cf0f3c1bc703527c30265534f75ae";
        String actualHash = Registration.hashPassword(password);
    
        assertEquals(expectedHash, actualHash, "Hashed password does not match the expected hash");
    }

    @Test
    void testSaveToFile() throws IOException {
        String testData = "testuser,testhash,instructor,male,25,pending,free";

        Registration.saveToFile(testData);

        // Read the test file and verify the content
        try (BufferedReader reader = new BufferedReader(new FileReader(TEST_USERS_FILE))) {
            String line = reader.readLine();
            assertEquals(testData, line, "Data saved to file does not match expected data");
        }
    }

    @Test
    void testEmptyUsername() {
        boolean result = Registration.registerUser("", "testpass", 1, "male", 25, "premium");
        assertFalse(result, "Empty username should return false.");
    }
    
    @Test
    void testValidUserRegistration() {
        boolean result = Registration.registerUser("testuser", "testpass", 1, "male", 25, "premium");
        assertTrue(result, "Valid user registration should return true.");

        try (BufferedReader reader = new BufferedReader(new FileReader(TEST_USERS_FILE))) {
            String savedData = reader.readLine();
            assertNotNull(savedData, "User data should be saved in the file.");
            assertTrue(savedData.startsWith("testuser,"), "Saved data should start with the username.");
        } catch (IOException e) {
            fail("Error reading test file: " + e.getMessage());
        }
    }



    @Test
    void testEmptyPassword() {
        boolean result = Registration.registerUser("testuser", "", 1, "male", 25, "premium");
        assertFalse(result, "Empty password should return false.");
    }

    @Test
    void testInvalidUserType() {
        boolean result = Registration.registerUser("testuser", "testpass", 3, "male", 25, "premium");
        assertFalse(result, "Invalid user type should return false.");
    }

    @Test
    void testEmptyGender() {
        boolean result = Registration.registerUser("testuser", "testpass", 1, "", 25, "premium");
        assertFalse(result, "Empty gender should return false.");
    }

    @Test
    void testInvalidAge() {
        boolean result = Registration.registerUser("testuser", "testpass", 1, "male", -1, "premium");
        assertFalse(result, "Invalid age should return false.");
    }

    @Test
    void testEmptySubscription() {
        boolean result = Registration.registerUser("testuser", "testpass", 1, "male", 25, "");
        assertFalse(result, "Empty subscription should return false.");
    }
}
