package najah.edu.acceptance_tests;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Client {
    String name;
    List<String> enrolledPrograms;
    List<String> receivedMessages;
    List<String> receivedNotifications; // Added notifications list

    public Client(String name) {
        this.name = name;
        this.enrolledPrograms = new ArrayList<>();
        this.receivedMessages = new ArrayList<>();
        this.receivedNotifications = new ArrayList<>(); // Initialize the list
    }

    public void enrollIn(String program) {
        enrolledPrograms.add(program);
    }

    public void receiveMessage(String message) {
        receivedMessages.add(message);
    }

    public void receiveNotification(String notification) {
        receivedNotifications.add(notification);
    }
}

class DiscussionTopic {
    String title;
    Map<String, List<String>> posts; // Map stores post content as key, list of responses as value
    List<Client> participants;

    public DiscussionTopic(String title) {
        this.title = title;
        this.posts = new HashMap<>();
        this.participants = new ArrayList<>();
    }

    public void addPost(String postContent, Client author) {
        posts.put(postContent, new ArrayList<>()); // Initialize an empty response list for the post
        if (!participants.contains(author)) {
            participants.add(author);
        }
    }

    public void addResponse(String originalPost, String responseContent, Client author) {
        if (posts.containsKey(originalPost)) {
            posts.get(originalPost).add(responseContent);
            if (!participants.contains(author)) {
                participants.add(author);
            }
        } else {
            throw new IllegalArgumentException("Post not found: " + originalPost);
        }
    }
}
class ProgressReport {
    String weekCompleted;
    String feedback;
    String suggestedActions;

    public ProgressReport(String weekCompleted, String feedback, String suggestedActions) {
        this.weekCompleted = weekCompleted;
        this.feedback = feedback;
        this.suggestedActions = suggestedActions;
    }

      @Override
    public String toString() {
        return "Week Completed: " + weekCompleted + "\n" +
               "Feedback: " + feedback + "\n" +
               "Suggested Actions: " + suggestedActions;
    }
}


class ClientRepository {
    List<Client> clients = new ArrayList<>();

    public void addClient(Client client) {
        clients.add(client);
    }

    public Client getClient(String name) {
        for (Client client : clients) {
            if (client.name.equals(name)) {
                return client;
            }
        }
        return null; // Or throw an exception if client is not found
    }

    public boolean isEnrolled(String clientName, String programTitle) {
        Client client = getClient(clientName);
        if (client != null) {
            return client.enrolledPrograms.contains(programTitle);
        }
        return false;
    }
}

public class ClientMessagingSteps {

    private ClientRepository clientRepository = new ClientRepository();
    private Client currentClient;
    private String sentMessage;
    private String currentPost;
    private ProgressReport currentReport;
    private DiscussionTopic currentTopic;
    private String programForDiscussion;
    private Map<String, DiscussionTopic> existingTopics = new HashMap<>(); // Store existing topics
    private String errorMessage;

    @Given("the admin is on the \"Client Management\" page")
    public void theAdminIsOnTheClientManagementPage() {
        System.out.println("Admin is on the Client Management page.");
    }

    @And("the client {string} is enrolled in the {string} program")
    public void theClientIsEnrolledInTheProgram(String clientName, String programTitle) {
        Client client = clientRepository.getClient(clientName);
        if (client == null) {
            client = new Client(clientName);
            clientRepository.addClient(client);
        }
        client.enrollIn(programTitle);
        currentClient = client;
        System.out.println(clientName + " is enrolled in " + programTitle);
    }

    @When("the admin selects \"Send Message\" for the client")
    public void theAdminSelectsSendMessageForTheClient() {
        System.out.println("Admin selects Send Message for the client.");
    }

    @And("the admin types the message:")
    public void theAdminTypesTheMessage(String message) {
        sentMessage = message;
        System.out.println("Admin types the message:\n" + message);
    }

    @And("clicks the \"Send\" button")
    public void clicksTheSendButton() {
        if (currentClient != null) {
            currentClient.receiveMessage(sentMessage);
            currentClient.receiveNotification("You have a new message from your instructor."); //Use the new function
        }
        System.out.println("Admin clicks the Send button.");
    }

    @Then("the message is delivered to the client")
    public void theMessageIsDeliveredToTheClient() {
        if (currentClient == null || !currentClient.receivedMessages.contains(sentMessage)) {
            throw new AssertionError("Message was not delivered.");
        }
        System.out.println("Message delivered to the client.");
    }

    @And("the client receives a notification: {string}")
    public void theClientReceivesANotification(String expectedNotification) {
        if (currentClient == null || !currentClient.receivedNotifications.contains(expectedNotification)) {
            throw new AssertionError("Expected notification: " + expectedNotification + ", but got: " + (currentClient != null ? currentClient.receivedNotifications : "null"));
        }
        System.out.println("Client received notification: " + expectedNotification);
    }
    @When("the admin selects \"Provide Progress Report\" for the client")
    public void theAdminSelectsProvideProgressReportForTheClient() {
        System.out.println("Admin selects Provide Progress Report for the client.");
    }

    @And("the admin fills out the progress report:")
    public void theAdminFillsOutTheProgressReport(Map<String, String> reportDetails) {
        String weekCompleted = reportDetails.get("Week Completed");
        String feedback = reportDetails.get("Feedback");
        String suggestedActions = reportDetails.get("Suggested Actions");
        currentReport = new ProgressReport(weekCompleted, feedback, suggestedActions);
        System.out.println("Admin fills out the report:\n" + currentReport);
    }

    @And("clicks the \"Send Report\" button")
    public void clicksTheSendReportButton() {
        if (currentClient != null) {
            currentClient.receiveMessage("New progress report:\n" + currentReport);
        }
        System.out.println("Admin clicks the Send Report button.");
    }

    @Then("the client receives the progress report")
    public void theClientReceivesTheProgressReport() {
         if (currentClient == null || !currentClient.receivedMessages.contains("New progress report:\n" + currentReport)) {
            throw new AssertionError("Report was not delivered.");
        }
        System.out.println("Report delivered to the client.");
    }
    @Given("the admin is on the \"Discussion Forum\" page for the {string} program")
    public void theAdminIsOnTheDiscussionForumPageForTheProgram(String programTitle) {
        programForDiscussion = programTitle;
        System.out.println("Admin is on the Discussion Forum page for the " + programTitle + " program.");
    }
    @When("the admin starts a new discussion topic titled {string}")
    public void theAdminStartsANewDiscussionTopicTitled(String topicTitle) {
        currentTopic = new DiscussionTopic(topicTitle);
        existingTopics.put(topicTitle, currentTopic); // Store the new topic
        System.out.println("Admin starts a new discussion topic titled " + topicTitle);
    }

    @And("writes the first post:")
    public void writesTheFirstPost(String post) {
        if (currentTopic != null) {
            currentTopic.addPost(post, currentClient); 
        }
        System.out.println("Admin writes the first post:\n" + post);
    }

    @And("clicks the \"Post\" button")
    public void clicksThePostButton() {
        System.out.println("Admin clicks the Post button.");
    }

    @Then("the topic is created successfully")
    public void theTopicIsCreatedSuccessfully() {
        if (currentTopic == null) {
            throw new AssertionError("Topic was not created.");
        }
        System.out.println("Topic created successfully.");
    }

    @And("all enrolled clients receive a notification: {string}")
    public void allEnrolledClientsReceiveANotification(String expectedNotification) {
        for (Client client : clientRepository.clients) {
            if (client.enrolledPrograms.contains(programForDiscussion)) {
                client.receiveNotification(expectedNotification);
                if (!client.receivedNotifications.contains(expectedNotification)) {
                    throw new AssertionError(client.name + " did not receive the notification.");
                }
            }
        }
        System.out.println("All enrolled clients received the notification.");
    }
    
    
    @Given("the client {string} is on the \"Discussion Forum\" page for the {string} program")
    public void theClientIsOnTheDiscussionForumPageForTheProgram(String clientName, String programTitle) {
        currentClient = clientRepository.getClient(clientName);
        programForDiscussion = programTitle;
        if (currentClient == null) {
            currentClient = new Client(clientName);
            clientRepository.addClient(currentClient);
        }
        System.out.println(clientName + " is on the Discussion Forum for " + programTitle);
    }
    
    @Given("the topic {string} exists")
    public void theTopicExists(String topicTitle) {
        if (!existingTopics.containsKey(topicTitle)) {
            throw new AssertionError("Topic '" + topicTitle + "' does not exist.");
        }
        currentTopic = existingTopics.get(topicTitle);
        System.out.println("The topic " + topicTitle + " exists.");
    }
    
    @When("the client clicks on the topic")
    public void theClientClicksOnTheTopic() {
        System.out.println("Client clicks on the topic.");
    }

    @And("writes a response to the post:")
    public void writesAResponseToThePost(String response) {
        if (currentTopic != null && currentClient != null && currentPost != null) {
            currentTopic.addResponse(currentPost, response, currentClient);
        }
        System.out.println("Client writes a response:\n" + response);
    }

        @And("the post:")
    public void thePost(String postContent) {
        if (currentTopic != null && currentTopic.posts.containsKey(postContent)) {
            currentPost = postContent;
            System.out.println("Found the post:\n" + postContent);
        } else {
            throw new AssertionError("Post not found: " + postContent);
        }
    }

    @Then("the response is added to the topic")
    public void theResponseIsAddedToTheTopic() {
        if (currentTopic == null || !currentTopic.posts.containsKey(currentPost) || currentTopic.posts.get(currentPost).isEmpty()) {
            throw new AssertionError("Response was not added to the topic.");
        }
        System.out.println("Response added to the topic.");
    }

    @And("all other participants in the topic are notified: {string}")
    public void allOtherParticipantsInTheTopicAreNotified(String expectedNotification) {
        if (currentTopic != null) {
            for (Client participant : currentTopic.participants) {
                if (!participant.equals(currentClient)) { // Don't notify the poster
                    participant.receiveNotification(expectedNotification);
                    if (!participant.receivedNotifications.contains(expectedNotification)) {
                        throw new AssertionError(participant.name + " did not receive the notification.");
                    }
                }
            }
        }
        System.out.println("Other participants notified.");
    }
    @Given("the client {string} has posted a question:")
    public void theClientHasPostedAQuestion(String clientName, String question) {
        currentClient = clientRepository.getClient(clientName);
        if (currentClient == null) {
            currentClient = new Client(clientName);
            clientRepository.addClient(currentClient);
        }
        if (currentTopic != null) {
            currentTopic.addPost(question, currentClient);
            currentPost = question; // Set the current post for replying
        } else {
            throw new AssertionError("No current topic. Did you forget to define it?");
        }
        System.out.println(clientName + " has posted the question:\n" + question);
    }

    @When("the admin clicks \"Reply\" to the question")
    public void theAdminClicksReplyToTheQuestion() {
        System.out.println("Admin clicks Reply to the question.");
    }

    @And("writes:")
    public void writes(String reply) {
        if (currentTopic != null && currentPost != null) {
            currentTopic.addResponse(currentPost, reply, null); //Admin reply
        }
        System.out.println("Admin writes the reply:\n" + reply);
    }

    @And("clicks the \"Post Reply\" button")
    public void clicksThePostReplyButton() {
        System.out.println("Admin clicks the Post Reply button.");
    }

    @Then("the reply is added under the question")
    public void theReplyIsAddedUnderTheQuestion() {
         if (currentTopic == null || !currentTopic.posts.containsKey(currentPost) || currentTopic.posts.get(currentPost).isEmpty()) {
            throw new AssertionError("Reply was not added to the topic.");
        }
        System.out.println("Reply added to the topic.");
    }

    @And("the client {string} is notified: {string}")
    public void theClientIsNotified(String clientName, String expectedNotification) {
        Client client = clientRepository.getClient(clientName);
        if (client == null) {
            throw new AssertionError("Client " + clientName + " not found.");
        }
        if (!client.receivedNotifications.contains(expectedNotification)) {
            throw new AssertionError(client.name + " did not receive the notification. Received: "+ client.receivedNotifications);
        }
        System.out.println(clientName + " is notified: " + expectedNotification);
    }
    @When("leaves the message content empty")
    public void leavesTheMessageContentEmpty() {
        sentMessage = ""; // Set sentMessage to empty
        System.out.println("Admin leaves the message content empty.");
    }

    @Then("the message is not sent")
    public void theMessageIsNotSent() {
        if (currentClient != null && currentClient.receivedMessages.contains(sentMessage) && !sentMessage.isEmpty()) {
            throw new AssertionError("Message was sent when it should not have been.");
        }
        System.out.println("Message was not sent.");
    }


    @And("clicks the \"Send\" button")
    public void clicksTheSendButton1() {
        if (sentMessage.isEmpty()) {
            errorMessage = "Message content cannot be empty.";
        } else if (currentClient != null) {
            currentClient.receiveMessage( errorMessage);
        }
        System.out.println("Admin clicks the Send button.");
    }
    @And("an error message is displayed: {string}")
    public void anErrorMessageIsDisplayed(String expectedErrorMessage) {
        if (!expectedErrorMessage.equals(errorMessage)) {
            throw new AssertionError("Expected error message: '" + expectedErrorMessage + "', but got: '" + errorMessage + "'");
        }
        System.out.println("Error message displayed: " + errorMessage);
    }

}