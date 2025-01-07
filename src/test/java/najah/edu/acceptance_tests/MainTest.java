package najah.edu.acceptance_tests;

import org.junit.jupiter.api.*;

import com.example.fitness.Main;
import com.example.fitness.Registration;

import java.io.*;
import java.security.MessageDigest;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    private ByteArrayOutputStream outContent;
    private InputStream originalIn;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        originalIn = System.in;
        originalOut = System.out;
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }



    @Test
    void testAboutUs() {
        // Call the method
     String actualOutput=   Main.aboutUs();

     String expectedOutput = "\u001B[36m------ About Us ------\u001B[0m\n" +
             "\u001B[32mWelcome to the Gym Management System.\n" +
             "Our goal is to provide an efficient, user-friendly platform for gym clients, instructors, and admins.\n" +
             "With this system, clients can easily book classes, read informative articles, and manage their profiles.\n" +
             "Instructors can create articles and interact with clients.\n" +
             "Admins manage users, content, and more.\n" +
             "Thank you for using our system!\u001B[0m\n";

	assertEquals(expectedOutput, actualOutput, "The 'About Us' message should match the expected output."); 
        
    }

    @Test
    void testHashPassword() {
        // Test password hashing
        String password = "test123";
        String expectedHash = "ecd71870d1963316a97e3ac3408c9835ad8cf0f3c1bc703527c30265534f75ae";
        String actualHash = Main.hashPassword(password);
        assertEquals(expectedHash, actualHash, "The hashed password should match the expected hash.");
    }
    @Test
    void testHashPasswordNullInput() {
        // Test null input
        String password = null;
        String actualHash = Main.hashPassword(password);
        assertNull(actualHash, "Hashing a null password should return null.");
    }

    @Test
    void testHashPasswordEmptyInput() {
        // Test empty input
        String password = "";
        String actualHash = Main.hashPassword(password);
        assertNotNull(actualHash, "Hashing an empty password should not return null.");
        assertFalse(actualHash.isEmpty(), "Hashing an empty password should not result in an empty string.");
    }

    @Test
    void testHashPasswordSpecialCharacters() {
        // Test password with special characters
        String password = "!@#$%^&*()";
        String actualHash = Main.hashPassword(password);
        assertNotNull(actualHash, "Hashing a password with special characters should not return null.");
        assertFalse(actualHash.isEmpty(), "Hashing a password with special characters should not result in an empty string.");
    }



    @Test
    void testHashPasswordSameInput() {
        // Test the same input multiple times to ensure consistency
        String password = "consistent";
        String hash1 = Main.hashPassword(password);
        String hash2 = Main.hashPassword(password);
        assertEquals(hash1, hash2, "Hashing the same input should produce consistent results.");
    }
}
