package najah.edu.acceptance_tests;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import com.example.fitness.InstructorDashboard;
import com.example.fitness.Main;

import java.io.*;
import java.nio.file.Path;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InstructorDashboardTest {

    @TempDir
    static Path tempDir;

    private static File articlesFile;
    private static File usersFile;

    @BeforeAll
    static void setupAll() {
        // We'll create references to the "articles" and "users" files in a temp directory
        articlesFile = tempDir.resolve("articles.txt").toFile();
        usersFile = tempDir.resolve("users.txt").toFile();

        // Now, set these paths in Main so InstructorDashboard uses them
        Main.ARTICLES_FILE = articlesFile.getAbsolutePath();
        Main.USERS_FILE = usersFile.getAbsolutePath();

        // If you have additional references in Main (like Main.USERS_FILE),
        // set them here as well.
    }

    @BeforeEach
    void setupEach() throws IOException {
        // Clear out (or create) the articles and users files before each test
        if (!articlesFile.exists()) {
            articlesFile.createNewFile();
        } else {
            new PrintWriter(articlesFile).close();
        }

        if (!usersFile.exists()) {
            usersFile.createNewFile();
        } else {
            new PrintWriter(usersFile).close();
        }
    }

    // ------------------------------------------------------
    // TEST: viewArticles(String username)
    // ------------------------------------------------------
    @Test
    @Order(1)
    @DisplayName("viewArticles() - should display articles belonging to a given instructor")
    void testViewArticles() throws IOException {
        // We'll add multiple articles to the articles file
        // Format in code: articleId, title, author, publishDate, content
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(articlesFile))) {
            bw.write("A111,Article One,instructorA,2025-01-01,Some content here\n");
            bw.write("A222,Article Two,instructorB,2025-01-02,Other content\n");
            bw.write("A333,Article Three,instructorA,2025-01-03,More content\n");
        }

        // We'll capture console output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Provide user input so that viewArticles() tries to read an article ID
        // We'll choose an article that belongs to "instructorA"
        // The code first prints all articles belonging to instructorA (IDs: A111 and A333),
        // then asks for an ID. We'll enter "A111".
        String input = "A111\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Now call the method
        InstructorDashboard.viewArticles("instructorA");

        // Restore streams
        System.setOut(System.out);
        System.setIn(System.in);

        String consoleOutput = outContent.toString();
        // Make sure we see "A111" and "A333" in the summary listing
        assertTrue(consoleOutput.contains("ID: A111 - Title: Article One"),
                   "Should list article A111");
        assertTrue(consoleOutput.contains("ID: A333 - Title: Article Three"),
                   "Should list article A333");

        // After entering "A111", we expect the code to show full details of Article One
        assertTrue(consoleOutput.contains("Article Details"),
                   "Should display the 'Article Details' header");
        assertTrue(consoleOutput.contains("ID: A111"),
                   "Should display details for article A111");
        assertTrue(consoleOutput.contains("Title: Article One"),
                   "Should show 'Title: Article One' in details");
        assertTrue(consoleOutput.contains("Author: instructorA"),
                   "Should show 'Author: instructorA' in details");
        assertTrue(consoleOutput.contains("Publish Date: 2025-01-01"),
                   "Should show publish date 2025-01-01");
        assertTrue(consoleOutput.contains("Content: Some content here"),
                   "Should show full content for article A111");

        // Ensure article from another instructor (A222) is not listed or chosen
        assertFalse(consoleOutput.contains("Article Two"),
                    "Should NOT list 'Article Two' because it's instructorB's article");
    }

    @Test
    @Order(2)
    @DisplayName("viewArticles() - no articles found for instructor")
    void testViewArticlesNoMatches() throws IOException {
        // We only add an article for a different instructor
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(articlesFile))) {
            bw.write("X123,Unrelated Title,otherInstructor,2025-01-10,Some content\n");
        }

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // We'll provide some input, but it won't matter because no articles belong to instructorY
        String input = "X123\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        InstructorDashboard.viewArticles("instructorY");

        System.setOut(System.out);
        System.setIn(System.in);

        String consoleOutput = outContent.toString();
        // The code logs: "No articles found related to you."
        assertTrue(consoleOutput.contains("No articles found related to you."),
                   "Should warn no articles found");
    }

    // ------------------------------------------------------
    // TEST: viewProfile(String username)
    // ------------------------------------------------------
    @Test
    @Order(3)
    @DisplayName("viewProfile() - shows correct profile details for given user")
    void testViewProfileFound() throws IOException {
        // The code expects Main.USERS_FILE to contain:
        // username, ???, role, gender, age, status, subscription
        // Indices: [0]=username, [1]=???, [2]=role, [3]=gender, [4]=age, [5]=status, [6]=subscription
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(usersFile))) {
            bw.write("instructorX,xxx,Instructor,Male,35,Active,Monthly\n");
            bw.write("clientY,xxx,Client,Female,28,Active,Daily\n");
        }

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Call method
        InstructorDashboard.viewProfile("instructorX");

        System.setOut(System.out);
        String consoleOutput = outContent.toString();
        // The code logs something like:
        // "Username: instructorX
        //  Role: Instructor
        //  Gender: Male
        //  Age: 35
        //  Status: Active
        //  Subscription: Monthly"
        assertTrue(consoleOutput.contains("Username: instructorX"),
                   "Should show 'Username: instructorX'");
        assertTrue(consoleOutput.contains("Role: Instructor"),
                   "Should show 'Role: Instructor'");
        assertTrue(consoleOutput.contains("Gender: Male"),
                   "Should show 'Gender: Male'");
        assertTrue(consoleOutput.contains("Age: 35"),
                   "Should show 'Age: 35'");
        assertTrue(consoleOutput.contains("Status: Active"),
                   "Should show 'Status: Active'");
        assertTrue(consoleOutput.contains("Subscription: Monthly"),
                   "Should show 'Subscription: Monthly'");
    }

    @Test
    @Order(4)
    @DisplayName("viewProfile() - not found case")
    void testViewProfileNotFound() throws IOException {
        // We'll just add a different user
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(usersFile))) {
            bw.write("clientZ,xxx,Client,Female,30,Active,Yearly\n");
        }

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        InstructorDashboard.viewProfile("instructorX");

        System.setOut(System.out);
        String consoleOutput = outContent.toString();
        // The code logs: "No profile found for username: instructorX"
        assertTrue(consoleOutput.contains("No profile found for username: instructorX"),
                   "Should warn the profile is not found");
    }

    // ------------------------------------------------------
    // TEST: createArticle(String username)
    // ------------------------------------------------------
    @Test
    @Order(5)
    @DisplayName("createArticle() - successfully creates a new article")
    void testCreateArticle() throws IOException {
        // We'll feed user input for the article's title and content
        // The code also automatically sets the publish date to today's date
        // and an ID = System.currentTimeMillis().
        String input = "My Article Title\nThis is the content.\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Capture output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // The method we'll test
        InstructorDashboard.createArticle("instructorA");

        // Restore
        System.setOut(System.out);
        System.setIn(System.in);

        String consoleOutput = outContent.toString();
        // The code logs: "Article created successfully with ID: {something}"
        assertTrue(consoleOutput.contains("Article created successfully with ID:"),
                   "Expected creation success message");

        // Now verify the article was written to the file
        try (BufferedReader br = new BufferedReader(new FileReader(articlesFile))) {
            String line = br.readLine();
            assertNotNull(line, "Should have 1 line in the articles file");
            String[] parts = line.split(",", 5);
            // Format: articleId, title, author, publishDate, content
            assertEquals("My Article Title", parts[1], "Title should match user input");
            assertEquals("instructorA", parts[2], "Author should be instructorA");
            assertEquals("This is the content.", parts[4], "Content should match user input");
            // part[3] is the publish date; we won't assert it because it depends on LocalDate.now()
        }
    }

    // ------------------------------------------------------
    // (Optional) If you want to do a minimal test of showProgramsDashboard() 
    // you can do something like:
    // ------------------------------------------------------
    @Test
    @Order(6)
    @DisplayName("showProgramsDashboard() - minimal test (choose '6' to exit immediately)")
    void testShowProgramsDashboardExit() {
        // We'll just feed "6" so it prints the dashboard menu and then exits 
        String input = "6\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        InstructorDashboard.showProgramsDashboard("someInstructor");

        System.setOut(System.out);
        System.setIn(System.in);

        String consoleOutput = outContent.toString();
        // We expect it to print the "Programs Dashboard" menu and then "Exiting Programs Dashboard..."
        assertTrue(consoleOutput.contains("--- Programs Dashboard ---"),
                   "Should display the programs dashboard menu");
        assertTrue(consoleOutput.contains("Exiting Programs Dashboard..."),
                   "Should show exit message after choosing 6");
    }

}
