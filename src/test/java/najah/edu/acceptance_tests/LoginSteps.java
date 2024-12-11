package najah.edu.acceptance_tests;

import com.example.fitness.model.User;
import com.example.fitness.repository.UserRepository;
import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserManagementSteps {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @Given("the admin wants to add an instructor with name {string} and email {string}")
    public void theAdminWantsToAddAnInstructor(String name, String email) {
        user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword("password"); // Set a default password for testing
        user.setRole(User.Role.INSTRUCTOR);
        user.setStatus(User.Status.PENDING);
    }

    @When("the admin submits the new instructor details")
    public void theAdminSubmitsTheNewInstructorDetails() {
        user = userRepository.save(user);
    }

    @Then("the system should create an instructor with status {string}")
    public void theSystemShouldCreateAnInstructorWithStatus(String status) {
        Assertions.assertNotNull(user);
        Assertions.assertEquals(User.Status.valueOf(status), user.getStatus());
    }

    @Given("an instructor with name {string} exists")
    public void anInstructorWithNameExists(String name) {
        user = new User();
        user.setName(name);
        user.setEmail("instructor@example.com");
        user.setPassword("password");
        user.setRole(User.Role.INSTRUCTOR);
        user.setStatus(User.Status.ACTIVE);
        user = userRepository.save(user);
    }

    @When("the admin updates the instructor's email to {string}")
    public void theAdminUpdatesTheInstructorsEmail(String email) {
        user.setEmail(email);
        user = userRepository.save(user);
    }

    @Then("the instructor's email should be updated to {string}")
    public void theInstructorsEmailShouldBeUpdatedTo(String email) {
        Assertions.assertEquals(email, user.getEmail());
    }

    @When("the admin deactivates the instructor")
    public void theAdminDeactivatesTheInstructor() {
        user.setStatus(User.Status.INACTIVE);
        user = userRepository.save(user);
    }

    @Then("the instructor's status should be {string}")
    public void theInstructorsStatusShouldBe(String status) {
        Assertions.assertEquals(User.Status.valueOf(status), user.getStatus());
    }

    @Given("an instructor with status {string} exists")
    public void anInstructorWithStatusExists(String status) {
        user = new User();
        user.setName("Pending Instructor");
        user.setEmail("pending@example.com");
        user.setPassword("password");
        user.setRole(User.Role.INSTRUCTOR);
        user.setStatus(User.Status.valueOf(status));
        user = userRepository.save(user);
    }

    @When("the admin approves the instructor's registration")
    public void theAdminApprovesTheInstructorsRegistration() {
        user.setStatus(User.Status.ACTIVE);
        user = userRepository.save(user);
    }
}
