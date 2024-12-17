package najah.edu.acceptance_tests;

import io.cucumber.java.en.*;
import static org.junit.Assert.*;

import java.util.HashMap;
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
        // Ensure the user is logged out by resetting any relevant variables
        isLoggedIn = false;    // Flag to track login state
        currentClient = null;  // Clear any user session or active client object
        userAccount.clear();   // Reset user account data storage
        System.out.println("User is now logged out and has no active session.");
    }


    @When("the user registers with the following details:")
    public void user_registers_with_details(io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> registrationDetails = dataTable.asMap(String.class, String.class);
        String name = registrationDetails.get("Name");
        String email = registrationDetails.get("Email");
        String password = registrationDetails.get("Password");

        try {
            // Check if mandatory fields are present
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Name is required");
            }
            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("Email is required");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("Password is required");
            }

            // Create a new client object and simulate saving to database
            currentClient = new Client(name, email, password); 
            userAccount.put("Name", name);
            userAccount.put("Email", email);
            userAccount.put("Password", password);
            isLoggedIn = true;

            System.out.println("Registration successful for: " + name);

        } catch (IllegalArgumentException e) {
            // Capture error message for validation step
            errorMessage = e.getMessage();
        }
    }


    @Then("the user account is created successfully")
    public void user_account_created_successfully() {
        // Check if the currentClient object exists and the user is logged in
        if (currentClient != null && isLoggedIn) {
            System.out.println("Account creation successful for: " + currentClient.getName());
            assertTrue("User account should be created successfully", isLoggedIn);
        } else {
            // Fail the test with a clear error message
            System.out.println("Account creation failed. User is not logged in or client object is null.");
            fail("Account creation failed: User is not logged in or current client is null");
        }
    }


    @Then("the user can log in to their account")
    public void user_can_log_in_to_account() {
        // Validate that the user is logged in and email matches the stored value
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
        // Simulate a logged-in user by creating a valid Client object
        currentClient = new Client("John Doe", "john.doe@example.com", "password123");
     
        isLoggedIn = true;

        System.out.println("User is logged in as: " + currentClient.getName());
    }
    
//////////////

    @And("the user has an incomplete profile")
    public void user_has_incomplete_profile() {
        profileDetails.clear();
    }

    @When("the user updates their profile with the following details:")
    public void user_updates_profile(io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> updates = dataTable.asMap(String.class, String.class);
        if (updates.containsKey("Age") && Integer.parseInt(updates.get("Age")) < 0) {
            errorMessage = "Invalid age entered";
        } else {
            profileDetails.putAll(updates);
        }
    }

    @Then("the profile is updated successfully")
    public void profile_updated_successfully() {
        assertFalse("Profile update failed", profileDetails.isEmpty());
    }

    @And("the updated details are visible in the user's profile")
    public void updated_details_visible_in_profile() {
        assertTrue(profileDetails.containsKey("Age"));
        assertTrue(profileDetails.containsKey("Fitness Goals"));
    }

    @When("the user selects the following dietary preferences:")
    public void user_adds_dietary_preferences(io.cucumber.datatable.DataTable dataTable) {
        profileDetails.put("Dietary Preferences", String.join(", ", dataTable.asList()));
    }

    @Then("the preferences are saved successfully")
    public void preferences_saved_successfully() {
        assertTrue(profileDetails.containsKey("Dietary Preferences"));
    }

    @Then("the user receives personalized dietary recommendations")
    public void user_receives_dietary_recommendations() {
        System.out.println("Dietary preferences saved: " + profileDetails.get("Dietary Preferences"));
    }

    @Given("the user has completed their profile")
    public void user_has_completed_profile() {
        profileDetails.put("Name", "John Doe");
        profileDetails.put("Age", "30");
        profileDetails.put("Gender", "Male");
        profileDetails.put("Fitness Goals", "Lose Weight, Build Muscle");
        profileDetails.put("Dietary Preferences", "Vegetarian");
    }

    @When("the user views their profile")
    public void user_views_profile() {
        assertFalse("Profile is empty", profileDetails.isEmpty());
    }

    @Then("the system displays the following details:")
    public void system_displays_profile_details(io.cucumber.datatable.DataTable dataTable) {
        Map<String, String> expectedDetails = dataTable.asMap(String.class, String.class);
        for (String key : expectedDetails.keySet()) {
            assertEquals(expectedDetails.get(key), profileDetails.get(key));
        }
    }

    @When("the user tries to register with the following details:")
    public void user_registers_with_missing_details(io.cucumber.datatable.DataTable dataTable) {
        user_registers_with_details(dataTable);
    }

    @Then("the account creation fails")
    public void account_creation_fails() {
        assertFalse("Account creation succeeded unexpectedly", isLoggedIn);
    }

    @Then("the system displays an error message {string}")
    public void system_displays_error_message(String expectedMessage) {
        assertEquals(expectedMessage, errorMessage);
    }

    @When("the user confirms the account deletion")
    public void user_confirms_account_deletion() {
        userAccount.clear();
        accountDeleted = true;
        isLoggedIn = false;
    }

    @Then("the account is deleted successfully")
    public void account_deleted_successfully() {
        assertTrue("Account deletion failed", accountDeleted);
    }

    @And("the user is logged out of the system")
    public void user_logged_out_of_system() {
        assertFalse("User is still logged in", isLoggedIn);
    }

    @Then("the profile update fails")
    public void profile_update_fails() {
        assertTrue("Profile update succeeded unexpectedly", errorMessage.equals("Invalid age entered"));
    }
}

