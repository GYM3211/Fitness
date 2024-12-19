package najah.edu.acceptance_tests;

import io.cucumber.java.en.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientAccountSteps {

    private Map<String, String> userAccount = new HashMap<>();
    private boolean isLoggedIn = false;
    private String errorMessage = "";
    private Map<String, String> profileDetails = new HashMap<>();
    private boolean accountDeleted = false;
    private Client currentClient;

    @Given("that the user is not logged in")
    public void user_is_not_logged_in() {
        
        isLoggedIn = false;    
        currentClient = null;  
        userAccount.clear();  
        System.out.println("User is now logged out and has no active session.");
    }


    @When("the user registers with the following details:")
    public void user_registers_with_details(io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> registrationDetails = dataTable.asMap(String.class, String.class);
        String name = registrationDetails.get("Name");
        String email = registrationDetails.get("Email");
        String password = registrationDetails.get("Password");

        try {
        
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Name is required");
            }
            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("Email is required");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("Password is required");
            }

         
            currentClient = new Client(name, email, password); 
            userAccount.put("Name", name);
            userAccount.put("Email", email);
            userAccount.put("Password", password);
            isLoggedIn = true;

            System.out.println("Registration successful for: " + name);

        } catch (IllegalArgumentException e) {
            
            errorMessage = e.getMessage();
        }
    }


    @Then("the user account is created successfully")
    public void user_account_created_successfully() {
     
        if (currentClient != null && isLoggedIn) {
            System.out.println("Account creation successful for: " + currentClient.getName());
            assertTrue("User account should be created successfully", isLoggedIn);
        } else {
            
            System.out.println("Account creation failed. User is not logged in or client object is null.");
            fail("Account creation failed: User is not logged in or current client is null");
        }
    }


    
    @Then("the user can log in to their account")
    public void user_can_log_in_to_account() {
        if (currentClient != null && isLoggedIn) {
            assertEquals("User email does not match", "john.doe@example.com", currentClient.getEmail());
            System.out.println("Login successful for: " + currentClient.getName());
        } else {
            System.out.println("Login failed: User is not logged in or client object is null.");
            fail("Login failed: User session invalid or account details missing");
        }
    }


    
    @Given("that the user is logged in")
    public void user_is_logged_in() {
        currentClient = new Client("John Doe", "john.doe@example.com", "password123");
     
        isLoggedIn = true;

        System.out.println("User is logged in as: " + currentClient.getName());
    }
    

    
    @And("the user has an incomplete profile")
    public void user_has_incomplete_profile() {
        if (currentClient != null) {
            profileDetails.clear(); 
            System.out.println("Profile reset to incomplete for user: " + currentClient.getName());
        } else {
            System.out.println("No user logged in. Cannot reset profile.");
            fail("User must be logged in to reset profile to incomplete.");
        }
    }

    
    
    @When("the user updates their profile with the following details:")
    public void user_updates_profile(io.cucumber.datatable.DataTable dataTable) {
       
        Map<String, String> updates = dataTable.asMap(String.class, String.class);

        try {
          
            if (updates.containsKey("Age")) {
                int age = Integer.parseInt(updates.get("Age"));
                if (age < 0) {
                    throw new IllegalArgumentException("Invalid age entered");
                }
            }

         
            profileDetails.putAll(updates);

            
            System.out.println("Profile updated with details: " + updates);
        } catch (NumberFormatException e) {
            
            errorMessage = "Invalid age format";
            System.out.println("Error: " + errorMessage);
        } catch (IllegalArgumentException e) {
           
            errorMessage = e.getMessage();
            System.out.println("Error: " + errorMessage);
        }
    }


    
    @Then("the profile is updated successfully")
    public void profile_updated_successfully() {
        
        if (!profileDetails.isEmpty()) {
            System.out.println("Profile update successful. Updated details: " + profileDetails);
            assertFalse("Profile update failed: Profile details should not be empty", profileDetails.isEmpty());
        } else {
            System.out.println("Profile update failed. No details were added.");
            fail("Profile update failed: Profile details are empty.");
        }
    }


    
    @And("the updated details are visible in the user's profile")
    public void updated_details_visible_in_profile() {
        
        boolean hasAge = profileDetails.containsKey("Age");
        boolean hasFitnessGoals = profileDetails.containsKey("Fitness Goals");

        
        assertTrue("Profile does not contain 'Age'", hasAge);
        assertTrue("Profile does not contain 'Fitness Goals'", hasFitnessGoals);

       
        if (hasAge && hasFitnessGoals) {
            System.out.println("Profile contains the updated details: " + profileDetails);
        }
    }


    
    @When("the user selects the following dietary preferences:")
    public void user_adds_dietary_preferences(io.cucumber.datatable.DataTable dataTable) {
    
        List<String> preferences = dataTable.asList();

      
        if (!preferences.isEmpty()) {
            profileDetails.put("Dietary Preferences", String.join(", ", preferences));
            System.out.println("Dietary preferences added: " + preferences);
        } else {
            System.out.println("No dietary preferences were selected.");
        }
    }


    
    @Then("the preferences are saved successfully")
    public void preferences_saved_successfully() {
        
        if (profileDetails.containsKey("Dietary Preferences")) {
            String dietaryPreferences = profileDetails.get("Dietary Preferences");
            System.out.println("Preferences saved successfully: " + dietaryPreferences);
            assertTrue("Dietary Preferences should be saved in the profile", profileDetails.containsKey("Dietary Preferences"));
        } else {
            System.out.println("Preferences were not saved in the profile.");
            fail("Preferences were not saved in the profile.");
        }
    }

    
    
    @Then("the user receives personalized dietary recommendations")
    public void user_receives_dietary_recommendations() {
       
        if (profileDetails.containsKey("Dietary Preferences")) {
            String dietaryPreferences = profileDetails.get("Dietary Preferences");
            
            
            String recommendations = generateDietaryRecommendations(dietaryPreferences);
            System.out.println("Dietary preferences saved: " + dietaryPreferences);
            System.out.println("Personalized recommendations: " + recommendations);

            
            assertNotNull("Dietary preferences should not be null", dietaryPreferences);
            assertFalse("Dietary recommendations should not be empty", recommendations.isEmpty());
        } else {
            System.out.println("No dietary preferences found in the profile.");
            fail("Dietary preferences are missing. Unable to provide recommendations.");
        }
    }

    private String generateDietaryRecommendations(String dietaryPreferences) {
        if (dietaryPreferences == null || dietaryPreferences.trim().isEmpty()) {
            return "No dietary preferences provided. We recommend a balanced diet.";
        }

        if (dietaryPreferences.contains("Vegetarian")) {
            return "Based on your preferences, we recommend a high-protein vegetarian meal plan with legumes and tofu.";
        } else if (dietaryPreferences.contains("Gluten-Free")) {
            return "We recommend a gluten-free diet rich in vegetables, quinoa, and lean protein.";
        } else if (dietaryPreferences.contains("Dairy-Free")) {
            return "Consider incorporating almond milk, coconut yogurt, and other dairy-free options into your diet.";
        }

        return "Based on your preferences (" + dietaryPreferences + "), we recommend consulting with a nutritionist for a personalized plan.";
    }
    
    

    @Given("the user has completed their profile")
    public void user_has_completed_profile() {
        
        profileDetails.put("Name", "John Doe");
        profileDetails.put("Age", "30");
        profileDetails.put("Gender", "Male");
        profileDetails.put("Fitness Goals", "Lose Weight, Build Muscle");
        profileDetails.put("Dietary Preferences", "Vegetarian");

        System.out.println("User profile has been marked as completed: " + profileDetails);
    }
    
    

    @When("the user views their profile")
    public void user_views_profile() {
        
        if (!profileDetails.isEmpty()) {
            System.out.println("User profile details: " + profileDetails);
            assertFalse("Profile should not be empty", profileDetails.isEmpty());
        } else {
            System.out.println("Profile is empty. Unable to view details.");
            fail("Profile is empty. No details available to display.");
        }
    }


    
    @Then("the system displays the following details:")
    public void system_displays_profile_details(io.cucumber.datatable.DataTable dataTable) {

        Map<String, String> expectedDetails = dataTable.asMap(String.class, String.class);

        
        for (String key : expectedDetails.keySet()) {
            String expectedValue = expectedDetails.get(key);
            String actualValue = profileDetails.get(key);

            
            assertEquals("Mismatch for key: " + key, expectedValue, actualValue);
        }

        
        System.out.println("All expected profile details match the actual profile: " + profileDetails);
    }


    
    @When("the user tries to register with the following details:")
    public void user_registers_with_missing_details(io.cucumber.datatable.DataTable dataTable) {
       
        Map<String, String> registrationDetails = dataTable.asMap(String.class, String.class);
        String name = registrationDetails.get("Name");
        String email = registrationDetails.get("Email");
        String password = registrationDetails.get("Password");

        try {
            
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Name is required");
            }
            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("Email is required");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("Password is required");
            }

            
            user_registers_with_details(dataTable);

        } catch (IllegalArgumentException e) {
           
            errorMessage = e.getMessage();
            System.out.println("Error during registration: " + errorMessage);
        }
    }


    
    @Then("the account creation fails")
    public void account_creation_fails() {
      
        if (!isLoggedIn && currentClient == null) {
            System.out.println("Account creation failed as expected. User is not logged in and no client was created.");
            assertFalse("Account creation should not succeed", isLoggedIn);
        } else {
            System.out.println("Account creation unexpectedly succeeded. User login state: " + isLoggedIn);
            fail("Account creation succeeded unexpectedly. Current client: " + currentClient);
        }
    }


    
    @Then("the system displays an error message {string}")
    public void system_displays_error_message(String expectedMessage) {
        
        if (expectedMessage.equals(errorMessage)) {
            System.out.println("Error message displayed as expected: " + errorMessage);
            assertEquals("Error message does not match", expectedMessage, errorMessage);
        } else {
            System.out.println("Expected error message: " + expectedMessage);
            System.out.println("Actual error message: " + errorMessage);
            fail("Error message mismatch. Expected: " + expectedMessage + ", but was: " + errorMessage);
        }
    }


    
    @When("the user confirms the account deletion")
    public void user_confirms_account_deletion() {
        if (currentClient != null) {
         
            userAccount.clear();
            accountDeleted = true;
            isLoggedIn = false;
            currentClient = null;

           
            System.out.println("User account has been deleted successfully.");
        } else {
           
            System.out.println("No user is logged in to delete the account.");
            fail("Account deletion failed: No active user session.");
        }
    }


    
    @Then("the account is deleted successfully")
    public void account_deleted_successfully() {
       
        if (accountDeleted && currentClient == null && userAccount.isEmpty()) {
            System.out.println("Account deletion confirmed. All user data has been cleared.");
            assertTrue("Account deletion should be marked as successful", accountDeleted);
        } else {
            System.out.println("Account deletion failed. Current client: " + currentClient + ", User account data: " + userAccount);
            fail("Account deletion failed: User data or session still exists.");
        }
    }


    
    @And("the user is logged out of the system")
    public void user_logged_out_of_system() {
      
        if (!isLoggedIn && currentClient == null) {
            System.out.println("User is successfully logged out of the system.");
            assertFalse("User should not be logged in", isLoggedIn);
        } else {
            System.out.println("User logout failed. Login state: " + isLoggedIn + ", Current client: " + currentClient);
            fail("User logout failed: User is still logged in or session is not cleared.");
        }
    }


    
    @Then("the profile update fails")
    public void profile_update_fails() {
       
        if (errorMessage != null && !errorMessage.isEmpty()) {
            System.out.println("Profile update failed as expected with error: " + errorMessage);
            assertEquals("Profile update failed with unexpected error message", "Invalid age entered", errorMessage);
        } else {
            System.out.println("No error message was generated for the failed profile update.");
            fail("Profile update failure was expected, but no error message was found.");
        }
    }

}
