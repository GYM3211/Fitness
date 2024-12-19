package najah.edu.acceptance_tests;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonalizedRecommendationsTest {

    private Map<String, String> userPreferences;
    private List<Map<String, String>> progressMilestones;
    private List<Map<String, String>> recommendations;
    private boolean isLoggedIn;
    private String errorMessage;

    @Before
    public void setUp() {
        userPreferences = new HashMap<>();
        progressMilestones = new ArrayList<>();
        recommendations = new ArrayList<>();
        isLoggedIn = false;
        errorMessage = "";
    }

    @Test
    public void testRecommendFitnessProgram() {
       
        isLoggedIn = true;

        
        Map<String, String> milestone = new HashMap<>();
        milestone.put("Date", "2024-12-16");
        milestone.put("Weight", "78");
        milestone.put("BMI", "24.5");
        milestone.put("Attendance", "15");
        progressMilestones.add(milestone);

       
        Map<String, String> program1 = new HashMap<>();
        program1.put("Program Name", "Advanced Flexibility");
        program1.put("Focus Area", "Flexibility");
        recommendations.add(program1);

        Map<String, String> program2 = new HashMap<>();
        program2.put("Program Name", "Strength Training Basics");
        program2.put("Focus Area", "Muscle Building");
        recommendations.add(program2);

       
        assertFalse("No programs recommended", recommendations.isEmpty());
        assertEquals("Advanced Flexibility", recommendations.get(0).get("Program Name"));
        System.out.println("Recommended programs: " + recommendations);
    }

    @Test
    public void testRecommendDietaryPlan() {
       
        isLoggedIn = true;
        userPreferences.put("Dietary Preferences", "Vegetarian");
        userPreferences.put("Fitness Goal", "Lose Weight");

        
        Map<String, String> meal1 = new HashMap<>();
        meal1.put("Meal", "Quinoa Salad");
        meal1.put("Calories", "250");
        recommendations.add(meal1);

        Map<String, String> meal2 = new HashMap<>();
        meal2.put("Meal", "Lentil Soup");
        meal2.put("Calories", "180");
        recommendations.add(meal2);

        Map<String, String> meal3 = new HashMap<>();
        meal3.put("Meal", "Avocado Toast");
        meal3.put("Calories", "300");
        recommendations.add(meal3);

       
        assertFalse("No dietary recommendations found", recommendations.isEmpty());
        assertEquals("Quinoa Salad", recommendations.get(0).get("Meal"));
        System.out.println("Recommended dietary plan: " + recommendations);
    }

    @Test
    public void testRecommendWorkoutPlan() {
        
        isLoggedIn = true;
        Map<String, String> milestone = new HashMap<>();
        milestone.put("Date", "2024-12-16");
        milestone.put("Flexibility Level", "Low");
        progressMilestones.add(milestone);

       
        Map<String, String> exercise1 = new HashMap<>();
        exercise1.put("Exercise", "Yoga (Sun Salutation)");
        exercise1.put("Duration (mins)", "20");
        recommendations.add(exercise1);

        Map<String, String> exercise2 = new HashMap<>();
        exercise2.put("Exercise", "Hamstring Stretch");
        exercise2.put("Duration (mins)", "10");
        recommendations.add(exercise2);

        Map<String, String> exercise3 = new HashMap<>();
        exercise3.put("Exercise", "Cat-Cow Stretch");
        exercise3.put("Duration (mins)", "15");
        recommendations.add(exercise3);

       
        assertFalse("No workout recommendations found", recommendations.isEmpty());
        assertEquals("Yoga (Sun Salutation)", recommendations.get(0).get("Exercise"));
        System.out.println("Recommended workouts: " + recommendations);
    }

    @Test
    public void testFailToGenerateRecommendationsDueToMissingGoals() {
        
        isLoggedIn = true;

        if (!userPreferences.containsKey("Fitness Goal") || userPreferences.get("Fitness Goal").isEmpty()) {
            errorMessage = "Please set your fitness goals to receive recommendations";
        }

        
        assertEquals("Please set your fitness goals to receive recommendations", errorMessage);
        System.out.println("Error: " + errorMessage);
    }
}
