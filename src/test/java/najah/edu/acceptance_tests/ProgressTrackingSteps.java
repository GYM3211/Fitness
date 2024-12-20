package najah.edu.acceptance_tests;



import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;

class ClientProgress {
    String completionRate;
    String attendance;
    String goalsAchieved;
    

    public ClientProgress(String completionRate, String attendance, String goalsAchieved) {
        this.completionRate = completionRate;
        this.attendance = attendance;
        this.goalsAchieved = goalsAchieved;
    }

    @Override
    public String toString() {
        return "Completion Rate: " + completionRate + "\n" +
               "Attendance: " + attendance + "\n" +
               "Goals Achieved: " + goalsAchieved;
    }
}

public class ProgressTrackingSteps {

    private ClientProgress currentProgress;
    private Client currentClient;
    private ClientRepository clientRepository = new ClientRepository(); // Assuming you have this from previous code
    private String sentMotivationalMessage;
    private String sentRecommendation;
    
    @Given("the admin is on the {string} page")
    public void theAdminIsOnThePage(String pageName) {
        System.out.println("Admin is on the " + pageName + " page.");
    }

    @And("the client {string} is enrolled in the {string} program")
    public void theClientIsEnrolledInTheProgram(String clientName, String programTitle) {
        currentClient = clientRepository.getClient(clientName);
        if (currentClient == null) {
            currentClient = new Client(clientName);
            clientRepository.addClient(currentClient);
        }
        currentClient.enrollIn(programTitle);
        System.out.println(clientName + " is enrolled in " + programTitle);
    }


    @When("the admin views the progress report for the client")
    public void theAdminViewsTheProgressReportForTheClient() {
        System.out.println("Admin views the progress report for the client.");
    }

    @Then("the following details are displayed:")
    public void theFollowingDetailsAreDisplayed(Map<String, String> progressDetails) {
        String completionRate = progressDetails.get("Completion Rate");
        String attendance = progressDetails.get("Attendance");
        String goalsAchieved = progressDetails.get("Goals Achieved");

        currentProgress = new ClientProgress(completionRate, attendance, goalsAchieved);

        System.out.println("Progress Details Displayed:\n" + currentProgress);

        // Assertions (Important for testing)
        if (currentProgress == null || !currentProgress.completionRate.equals(completionRate) ||
            !currentProgress.attendance.equals(attendance) || !currentProgress.goalsAchieved.equals(goalsAchieved)) {
            throw new AssertionError("Progress details do not match expected values.");
        }
    }
    @When("the admin selects {string} for the client")
    public void theAdminSelectsForClient() {
        System.out.println("Admin selects Send Motivational Reminderfor the client.");
    }

    @And("writes the message:")
    public void writesTheMessage(String message) {
        sentMotivationalMessage = message;
        System.out.println("Admin writes the message:\n" + message);
    }

    @And("clicks {string}")
    public void clicks(String button) {
        if (currentClient != null) {
            currentClient.receiveMessage(sentMotivationalMessage);
            currentClient.receiveNotification("You have a new motivational message from your coach.");
        }
        System.out.println("Admin clicks " + button);
    }

    @Then("the message is delivered to the client")
    public void theMessageIsDeliveredToTheClient() {
        if (currentClient == null || !currentClient.receivedMessages.contains(sentMotivationalMessage)) {
            throw new AssertionError("Motivational message was not delivered.");
        }
        System.out.println("Motivational message delivered to the client.");
    }

    @And("the client receives a notification: {string}")
    public void theClientReceivesANotification(String expectedNotification) {
        if (currentClient == null || !currentClient.receivedNotifications.contains(expectedNotification)) {
            throw new AssertionError("Expected notification: " + expectedNotification + ", but got: " + (currentClient != null ? currentClient.receivedNotifications : "null"));
        }
        System.out.println("Client received notification: " + expectedNotification);
    }
    @When("the admin selects {string} for the client")
    public void theAdminSelectsForClient(String action) {
        System.out.println("Admin selects " + action + " for the client.");
    }

    @And("writes:")
    public void writes(String message) {
        sentRecommendation = message;
        System.out.println("Admin writes:\n" + message);
    }

   

    @Then("the message is delivered to the client")
    public void theRecommendationIsDeliveredToTheClient() {
        if (currentClient == null || !currentClient.receivedMessages.contains(sentRecommendation)) {
            throw new AssertionError("Recommendation was not delivered.");
        }
        System.out.println("Recommendation delivered to the client.");
    }

    @And("the client receives a notification: {string}")
    public void theClientReceivesRecommendationANotification(String expectedNotification) {
        if (currentClient == null || !currentClient.receivedNotifications.contains(expectedNotification)) {
            throw new AssertionError("Expected notification: " + expectedNotification + ", but got: " + (currentClient != null ? currentClient.receivedNotifications : "null"));
        }
        System.out.println("Client received notification: " + expectedNotification);
    }

   
}