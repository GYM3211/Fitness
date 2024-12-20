
package najah.edu.acceptance_tests;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;



class Announcement {
    String title;
    String content;

    public Announcement(String title, String content) {
        this.title = title;
        this.content = content;
    }
}


public class NotificationsAndUpdatesSteps {

    private ProgramRepository programRepository = new ProgramRepository();
    private Program currentProgram;
    private String notificationMessage;
    private ClientRepository clientRepository = new ClientRepository();//reuse
    private String programForNotification;
    private String announcementTitle;
    private String announcementContent;
    private List<Announcement> announcements = new ArrayList<>();
    
    @Given("the admin is on the {string} page")
    public void theAdminIsOnThePage(String pageName) {
        System.out.println("Admin is on the " + pageName + " page.");
    }

    @And("the program {string} has a schedule change")
    public void theProgramHasAScheduleChange(String programName) {
        currentProgram = programRepository.getProgram1(programName);
        if (currentProgram == null) {
            currentProgram = new Program(programName);
            programRepository.addProgram(currentProgram);
        }
        programForNotification=programName;
        System.out.println("The program " + programName + " has a schedule change.");
    }

    @When("the admin updates the schedule to:")
    public void theAdminUpdatesTheScheduleTo(Map<String, String> newSchedule) {
        if (currentProgram != null) {
            currentProgram.updateSchedule(newSchedule);
        }
        System.out.println("Admin updates the schedule to:\n" + newSchedule);
    }

    @And("clicks {string}")
    public void clicks(String button) {
        System.out.println("Admin clicks " + button);
        if (currentProgram != null && currentProgram.schedule != null) {
            String date = currentProgram.schedule.get("Date");
            String time = currentProgram.schedule.get("Time");
            String mode = currentProgram.schedule.get("Mode");

            notificationMessage = String.format("The schedule for \"%s\" has been updated. \nNew session details: %s at %s (%s).",
                    currentProgram.title, date, time, mode);
        }
    }

    @Then("all enrolled clients are notified")
    public void allEnrolledClientsAreNotified() {
        if (notificationMessage == null) {
            throw new AssertionError("Notification message was not generated.");
        }
        for (Client client : clientRepository.clients) {
            if (client.enrolledPrograms.contains(programForNotification)) {
                client.receiveNotification(notificationMessage);
                if (!client.receivedNotifications.contains(notificationMessage)) {
                    throw new AssertionError(client.name + " did not receive the notification.");
                }
            }
        }
        System.out.println("All enrolled clients are notified.");
    }

    @And("the notification message reads:")
    public void theNotificationMessageReads(String expectedMessage) {
        if (!notificationMessage.trim().equals(expectedMessage.trim())) { // Trim to handle extra whitespace
            throw new AssertionError("Notification message does not match expected message.\nExpected:\n" + expectedMessage + "\nActual:\n" + notificationMessage);
        }
        System.out.println("Notification message is correct.");
    }
    @When("the admin creates a new announcement titled {string}")
    public void theAdminCreatesANewAnnouncementTitled(String title) {
        announcementTitle = title;
        System.out.println("Admin creates a new announcement titled: " + title);
    }

    @And("writes:")
    public void writes(String content) {
        announcementContent = content;
        System.out.println("Admin writes:\n" + content);
    }

    @And("selects {string}")
    public void selects(String option) {
        System.out.println("Admin selects: " + option);
    }

    @And("clicks {string}")
    public void clicks1(String button) {
        Announcement newAnnouncement = new Announcement(announcementTitle, announcementContent);
        announcements.add(newAnnouncement);

        for (Client client : clientRepository.clients) {
            client.receiveNotification("New Announcement: " + announcementTitle);
        }
        System.out.println("Admin clicks: " + button);
    }

    @Then("all clients receive a notification")
    public void allClientsReceiveANotification() {
         for (Client client : clientRepository.clients) {
            if (!client.receivedNotifications.contains("New Announcement: " + announcementTitle)) {
                throw new AssertionError(client.name + " did not receive the notification.");
            }
        }
        System.out.println("All clients received the notification.");
    }

    @And("the announcement is displayed in the {string} section on the client's dashboard")
    public void theAnnouncementIsDisplayedInTheSectionOnTheClientSDashboard(String section) {
        boolean announcementFound = false;
        for (Announcement announcement : announcements) {
            if (announcement.title.equals(announcementTitle) && announcement.content.equals(announcementContent)) {
                announcementFound = true;
                break;
            }
        }
        if (!announcementFound) {
            throw new AssertionError("Announcement not found in " + section + " section.");
        }
        System.out.println("Announcement displayed in the " + section + " section.");
    }
  

    @And("writes:")
    public void writescotent(String content) {
        announcementContent = content;
        System.out.println("Admin writes:\n" + content);
    }

    @And("clicks {string}")
    public void clickspush(String button) {
        Announcement newAnnouncement = new Announcement(announcementTitle, announcementContent);
        announcements.add(newAnnouncement);

        for (Client client : clientRepository.clients) {
            client.receiveNotification("New Announcement: " + announcementTitle);
        }
        System.out.println("Admin clicks: " + button);
    }
    


}
