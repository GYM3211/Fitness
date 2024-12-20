package najah.edu.acceptance_tests;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

class Program {
    String title;
    String duration;
    String difficulty;
    String goals;
    double price;  
    List<Schedule> schedules;
    Map<String, String> schedule;  

    public Program(String title, String duration, String difficulty, String goals, double price,  List<Schedule> schedules) {
        this.title = title;
        this.duration = duration;
        this.difficulty = difficulty;
        this.goals = goals;
        this.price = price;
        this.schedules = schedules;
        
    }
    public Program(String name) {
        this.title = name;
        this.schedule = new java.util.HashMap<>();
    }

    public void updateDetails(String duration, double price) {
        this.duration = duration;
        this.price = price;
       
        
     }
    public void updateSchedule(Map<String, String> newSchedule) {
        this.schedule = newSchedule;
    }
   
               }


class Schedule {
    String sessionType;
    String date;
    String time;
    String mode;

    public Schedule(String sessionType, String date, String time, String mode) {
        this.sessionType = sessionType;
        this.date = date;
        this.time = time;
        this.mode = mode;
    }
              }

class ProgramRepository {
	 List<Program> program = new java.util.ArrayList<>();

	   

  public Program getProgram1(String programName) {
	  for (Program program : program) {
	  if (program.title.equals(programName)) {
     return program;
	    }
	    }
	 return null;
	    }
    private Map<String, Program> programs = new HashMap<>();

    public void addProgram(Program program) {
        programs.put(program.title, program);
    }

   public Program getProgram(String title) {
      return programs.get(title);
   }

    public void updateProgram(String title, Program updatedProgram) {
        programs.put(title, updatedProgram);
    }

    public void deleteProgram(String title) {
        programs.remove(title);
    }

    public boolean exists(String title) {
        return programs.containsKey(title);
    }
}

public class ProgramManagementSteps {

	   private ProgramRepository repository = new ProgramRepository();
	    private Program currentProgram;
	    private String errorMessage;

	    @Given("the admin is on the \"Create Program\" page")
	    public void adminIsOnCreateProgramPage() {
	        System.out.println("Admin navigates to Create Program page.");
	    }

	    @When("the admin enters the following details:")
	    public void adminEntersDetails(Map<String, String> details) {
	        String title = details.get("Program Title");
	        String duration = details.get("Duration");
	        String difficulty = details.get("Difficulty Level");
	        String goals = details.get("Goals");
	        double price = Double.parseDouble(details.get("Price"));

	        if (title == null || title.isEmpty()) {
	            errorMessage = "Program Title is required.";
	        } else {
	            currentProgram = new Program(title, duration, difficulty, goals, price, new ArrayList<>());
	        }
	    }

	  

	    @And("the admin sets the schedule:")
	    public void adminSetsSchedule(List<Map<String, String>> schedules) {
	        if (currentProgram != null) {
	            for (Map<String, String> scheduleDetails : schedules) {
	                Schedule schedule = new Schedule(
	                    scheduleDetails.get("Session Type"),
	                    scheduleDetails.get("Date"),
	                    scheduleDetails.get("Time"),
	                    scheduleDetails.get("Mode")
	                );
	                currentProgram.schedules.add(schedule);
	            }
	        }
	    }

	    @And("the admin clicks the \"Save\" button")
	    public void adminClicksSave() {
	        if (errorMessage != null) {
	            System.out.println(errorMessage);
	        } else if (currentProgram != null) {
	            repository.addProgram(currentProgram);
	            System.out.println("Program saved successfully.");
	        }
	    }

	    @Then("the program is created successfully")
	    public void programIsCreatedSuccessfully() {
	        if (currentProgram == null || !repository.exists(currentProgram.title)) {
	            throw new AssertionError("Program was not created.");
	        }
	        System.out.println("Program created and added to the repository.");
	    }

	    @Then("an error message is displayed: \"(.*)\"")
	    public void errorMessageDisplayed(String expectedMessage) {
	        if (!expectedMessage.equals(errorMessage)) {
	            throw new AssertionError("Expected: " + expectedMessage + " but got: " + errorMessage);
	        }
	    }

	    @Given("the admin is on the \"Program Management\" page")
	    public void adminIsOnProgramManagementPage() {
	        System.out.println("Admin navigates to Program Management page.");
	    }

	    @And("the program titled \"(.*)\" exists")
	    public void programExists(String title) {
	        if (!repository.exists(title)) {
	            throw new AssertionError("Program titled " + title + " does not exist.");
	        }
	        currentProgram = repository.getProgram(title);
	    }

	    @When("the admin updates the following details:")
	    public void adminUpdatesDetails(Map<String, String> updatedDetails) {
	        if (currentProgram != null) {
	            String newDuration = updatedDetails.get("Duration");
	            double newPrice = Double.parseDouble(updatedDetails.get("Price"));
	           
	            currentProgram.updateDetails(newDuration, newPrice);
	            repository.updateProgram(currentProgram.title, currentProgram);
	        }
	    }

	    @Then("the program is updated successfully")
	    public void programIsUpdatedSuccessfully() {
	        if (currentProgram == null || !repository.exists(currentProgram.title)) {
	            throw new AssertionError("Program update failed.");
	        }
	        System.out.println("Program updated successfully.");
	    }

	    @When("the admin clicks the \"Delete\" button next to the program")
	    public void adminClicksDelete() {
	        if (currentProgram != null) {
	            repository.deleteProgram(currentProgram.title);
	        }
	    }

	    @And("confirms the deletion")
	    public void adminConfirmsDeletion() {
	        System.out.println("Admin confirms deletion.");
	    }

	    @Then("the program is removed from the program listing")
	    public void programIsRemovedFromListing() {
	        if (currentProgram != null && repository.exists(currentProgram.title)) {
	            throw new AssertionError("Program deletion failed.");
	        }
	        System.out.println("Program removed from listing.");
	    }

	    @Then("a success message is displayed: \"(.*)\"")
	    public void successMessageDisplayed(String message) {
	        System.out.println("Displayed message: " + message);
	    }}