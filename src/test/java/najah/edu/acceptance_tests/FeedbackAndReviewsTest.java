package najah.edu.acceptance_tests;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedbackAndReviewsTest {

    private Map<String, List<Map<String, String>>> programFeedback;
    private List<Map<String, String>> userSuggestions;
    private boolean isLoggedIn;

    @Before
    public void setUp() {
        
        programFeedback = new HashMap<>();
        userSuggestions = new ArrayList<>();
        isLoggedIn = false;
    }

    @Test
    public void testSubmitFeedbackForCompletedProgram() {
       
        isLoggedIn = true;
        String programName = "Weight Loss Program";
        Map<String, String> feedback = new HashMap<>();
        feedback.put("User", "John Doe");
        feedback.put("Rating", "4");
        feedback.put("Review", "Effective program, but sessions were too long.");

     
        programFeedback.computeIfAbsent(programName, k -> new ArrayList<>()).add(feedback);

       
        assertTrue(programFeedback.containsKey(programName));
        assertEquals(1, programFeedback.get(programName).size());
        assertEquals("4", programFeedback.get(programName).get(0).get("Rating"));
    }

    @Test
    public void testViewFeedbackForSpecificProgram() {
        
        String programName = "Yoga for Beginners";
        List<Map<String, String>> feedbackList = new ArrayList<>();

        Map<String, String> feedback1 = new HashMap<>();
        feedback1.put("User", "John Doe");
        feedback1.put("Rating", "5");
        feedback1.put("Review", "Excellent program for flexibility.");

        Map<String, String> feedback2 = new HashMap<>();
        feedback2.put("User", "Jane Smith");
        feedback2.put("Rating", "4");
        feedback2.put("Review", "Great, but needs more stretching exercises.");

        feedbackList.add(feedback1);
        feedbackList.add(feedback2);

        programFeedback.put(programName, feedbackList);

        
        assertTrue(programFeedback.containsKey(programName));
        assertEquals(2, programFeedback.get(programName).size());
        assertEquals("5", programFeedback.get(programName).get(0).get("Rating"));
    }

    @Test
    public void testFailToSubmitFeedbackDueToMissingFields() {
       
        isLoggedIn = true;
        String programName = "Weight Loss Program";
        Map<String, String> feedback = new HashMap<>();
        feedback.put("Review", "Good program"); 

        try {
            if (!feedback.containsKey("Rating") || feedback.get("Rating").isEmpty()) {
                throw new IllegalArgumentException("Rating is required");
            }
            programFeedback.computeIfAbsent(programName, k -> new ArrayList<>()).add(feedback);
            fail("Feedback submission should fail due to missing rating");
        } catch (IllegalArgumentException e) {
            assertEquals("Rating is required", e.getMessage());
        }
    }

    @Test
    public void testSubmitSuggestionForCompletedProgram() {
        
        isLoggedIn = true;
        Map<String, String> suggestion = new HashMap<>();
        suggestion.put("Program Name", "Advanced Strength Training");
        suggestion.put("Suggestion", "Add more lower body exercises to the second session.");

        userSuggestions.add(suggestion);

       
        assertEquals(1, userSuggestions.size());
        assertEquals("Add more lower body exercises to the second session.", userSuggestions.get(0).get("Suggestion"));
    }

    @Test
    public void testViewPreviouslySubmittedSuggestions() {
       
        Map<String, String> suggestion1 = new HashMap<>();
        suggestion1.put("Program Name", "Advanced Strength Training");
        suggestion1.put("Suggestion", "Add more lower body exercises.");

        Map<String, String> suggestion2 = new HashMap<>();
        suggestion2.put("Program Name", "Yoga for Beginners");
        suggestion2.put("Suggestion", "Include relaxation techniques.");

        userSuggestions.add(suggestion1);
        userSuggestions.add(suggestion2);

       
        assertEquals(2, userSuggestions.size());
        assertEquals("Add more lower body exercises.", userSuggestions.get(0).get("Suggestion"));
        assertEquals("Include relaxation techniques.", userSuggestions.get(1).get("Suggestion"));
    }

    @Test
    public void testInstructorReviewsSubmittedSuggestions() {
       
        String programName = "Advanced Strength Training";
        List<Map<String, String>> instructorSuggestions = new ArrayList<>();

        Map<String, String> suggestion1 = new HashMap<>();
        suggestion1.put("User", "John Doe");
        suggestion1.put("Suggestion", "Add more lower body exercises.");

        Map<String, String> suggestion2 = new HashMap<>();
        suggestion2.put("User", "Jane Smith");
        suggestion2.put("Suggestion", "Include rest breaks in the sessions.");

        instructorSuggestions.add(suggestion1);
        instructorSuggestions.add(suggestion2);

        programFeedback.put(programName, instructorSuggestions);

       
        assertTrue(programFeedback.containsKey(programName));
        assertEquals(2, programFeedback.get(programName).size());
        assertEquals("Add more lower body exercises.", programFeedback.get(programName).get(0).get("Suggestion"));
    }
}
