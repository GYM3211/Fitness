package najah.edu.acceptance_tests;

import org.junit.jupiter.api.*;

import com.example.fitness.ContentManagement;
import com.example.fitness.Main;

import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

class ContentManagementTest {

    private static final String TEST_ARTICLES_FILE = "test_articles.txt";

    @BeforeAll
    static void setup() throws IOException {
    	
        Main.ARTICLES_FILE = TEST_ARTICLES_FILE;
        Files.createFile(Paths.get(TEST_ARTICLES_FILE));
    }

    @AfterEach
    void cleanUp() throws IOException {
        Files.write(Paths.get(TEST_ARTICLES_FILE), new ArrayList<>()); // Clear the file after each test
    }

    @AfterAll
    static void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_ARTICLES_FILE));
    }

    @Test
    void testPrintAllArticlesNoArticles() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        ContentManagement.printAllArticles();

        String output = outContent.toString();
        assertTrue(output.contains("No articles available."),
                "The output should indicate no articles are available.");
    }

    @Test
    void testPrintAllArticlesWithArticles() throws IOException {
        // Add test articles
        List<String> articles = Arrays.asList(
                "1,Title1,Author1,2025-01-01,Content1",
                "2,Title2,Author2,2025-01-02,Content2"
        );
        Files.write(Paths.get(TEST_ARTICLES_FILE), articles);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        ContentManagement.printAllArticles();

        String output = outContent.toString();
        assertTrue(output.contains("ID: 1 - Title: Title1"),
                "The output should contain the first article's details.");
        assertTrue(output.contains("ID: 2 - Title: Title2"),
                "The output should contain the second article's details.");
    }

    @Test
    void testAddNewArticle() throws IOException {
        // Mock user input for adding a new article
        String input = "Test Title\nTest Content\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ContentManagement.addNewArticle("TestUser");

        // Verify the article was added to the file
        List<String> lines = Files.readAllLines(Paths.get(TEST_ARTICLES_FILE));
        assertEquals(1, lines.size(), "There should be one article in the file.");
        assertTrue(lines.get(0).contains("Test Title"),
                "The file should contain the article title.");
        assertTrue(lines.get(0).contains("TestUser"),
                "The file should contain the article author.");
    }

    @Test
    void testEditArticleByIdFound() throws IOException {
        // Add test article
        Files.write(Paths.get(TEST_ARTICLES_FILE),
                Arrays.asList("1,Title1,Author1,2025-01-01,Content1"));

        // Mock user input for editing the article
        String input = "1\nUpdated Content\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ContentManagement.editArticleById();

        // Verify the article was updated
        List<String> lines = Files.readAllLines(Paths.get(TEST_ARTICLES_FILE));
        assertEquals(1, lines.size(), "There should still be one article in the file.");
        assertTrue(lines.get(0).contains("Updated Content"),
                "The file should contain the updated content.");
    }

    @Test
    void testEditArticleByIdNotFound() throws IOException {
        // Add a test article
        Files.write(Paths.get(TEST_ARTICLES_FILE),
                Arrays.asList("1,Title1,Author1,2025-01-01,Content1"));

        // Mock user input for editing a non-existent article
        String input = "2\nUpdated Content\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Capture output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Call the method
        ContentManagement.editArticleById();
        // Verify the output indicates the article ID was not found
        String output = outContent.toString();
        assertTrue(output.contains("Article ID 2 not found"),
                "The output should indicate the article ID was not found.");
    }
    @Test
    void testDeleteArticleByIdFound() throws IOException {
        // Add test articles
        Files.write(Paths.get(TEST_ARTICLES_FILE),
                Arrays.asList("1,Title1,Author1,2025-01-01,Content1",
                        "2,Title2,Author2,2025-01-02,Content2"));

        // Mock user input for deleting an article
        String input = "1\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ContentManagement.deleteArticleById();

        // Verify the article was deleted
        List<String> lines = Files.readAllLines(Paths.get(TEST_ARTICLES_FILE));
        assertEquals(1, lines.size(), "There should be one article remaining in the file.");
        assertTrue(lines.get(0).contains("Title2"),
                "The remaining article should not be the deleted one.");
    }

    @Test
    void testDeleteArticleByIdNotFound() throws IOException {
        // Add test article
        Files.write(Paths.get(TEST_ARTICLES_FILE),
                Arrays.asList("1,Title1,Author1,2025-01-01,Content1"));

        // Mock user input for deleting a non-existent article
        String input = "2\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        

        ContentManagement.deleteArticleById();

        // Verify the output indicates the article was not found
        String output = outContent.toString();
        assertTrue(output.contains("Article ID 2 not found"),
                "The output should indicate the article ID was not found.");
    }
}
