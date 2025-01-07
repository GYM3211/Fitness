package najah.edu.acceptance_tests;
import org.junit.jupiter.api.*;

import com.example.fitness.AdminDashboard;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AdminDashboardTest {

    @BeforeAll
    static void setupAll() {
        // If needed, you can configure stubs or mocks for
        // UserManagement, ContentManagement, SystemLogs
        // For now, we'll assume they just do a simple print or do nothing.
    }

    @BeforeEach
    void setupEach() {
        // Nothing special to do before each test for this class
    }

    @Test
    @Order(1)
    @DisplayName("Show admin dashboard and exit immediately with option 4")
    void testShowDashboardOptions_exit() {
        // Provide the input: '4' => should immediately trigger logout
        String input = "4\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Capture console output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Call method
        AdminDashboard.showDashboardOptions("adminUser");

        // Restore
        System.setOut(System.out);
        System.setIn(System.in);

        String output = outContent.toString();

        // We expect "Logging out..." to appear
        assertTrue(output.contains("Logging out..."),
                   "Should show logout message on option 4");
    }

    @Test
    @Order(2)
    @DisplayName("Show admin dashboard, pick invalid numeric option, then exit")
    void testShowDashboardOptions_invalidOption() {
        // We'll pick '99' => invalid => see the error message, then '4' => exit
        String input = "99\n4\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Capture console output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        AdminDashboard.showDashboardOptions("adminUser");

        System.setOut(System.out);
        System.setIn(System.in);

        String output = outContent.toString();

        // Expect "Invalid option. Please select a valid option."
        // then eventually "Logging out..."
        assertTrue(output.contains("Invalid option. Please select a valid option."),
                   "Should show invalid option message for '99'");
        assertTrue(output.contains("Logging out..."),
                   "Should log out after choosing 4");
    }

    @Test
    @Order(3)
    @DisplayName("Show admin dashboard, non-numeric input => error, then exit")
    void testShowDashboardOptions_nonNumeric() {
        // We'll pick 'abc' => triggers NumberFormatException => "Invalid input!"
        // then '4' => exit
        String input = "abc\n4\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        AdminDashboard.showDashboardOptions("adminUser");

        System.setOut(System.out);
        System.setIn(System.in);

        String output = outContent.toString();
        assertTrue(output.contains("Invalid input! Please enter a number."),
                   "Should warn about invalid (non-numeric) input");
        assertTrue(output.contains("Logging out..."),
                   "Should log out after picking 4");
    }

  

}