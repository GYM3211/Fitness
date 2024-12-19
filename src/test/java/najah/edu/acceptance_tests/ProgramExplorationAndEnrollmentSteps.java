package najah.edu.acceptance_tests;


import io.cucumber.java.en.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProgramExplorationAndEnrollmentSteps {

    private List<Map<String, String>> programs = new ArrayList<>();
    private List<Map<String, String>> enrolledPrograms = new ArrayList<>();
    private boolean isLoggedIn = false;
    private String filterErrorMessage = "";

    @Given("the user is logged in")
    public void user_is_logged_in() {
        isLoggedIn = true;
        System.out.println("User is logged in.");
    }

    @Given("there are available programs in the system")
    public void available_programs_in_system() {
        boolean isProgramAdded;

        // Create schedule for "Weight Loss for Beginners"
        List<Map<String, String>> beginnersWeightLossSchedule = createSchedule(
                new String[][]{
                        {"Day", "Monday", "Time", "7:00 PM"},
                        {"Day", "Wednesday", "Time", "7:00 PM"}
                }
        );
        isProgramAdded = programs.add(createProgram("Weight Loss for Beginners", "Beginner", "Weight Loss", beginnersWeightLossSchedule));
        System.out.println("Program 'Weight Loss for Beginners' added: " + isProgramAdded);

        // Create schedule for "Flexibility Basics"
        List<Map<String, String>> flexibilityBasicsSchedule = createSchedule(
                new String[][]{
                        {"Day", "Tuesday", "Time", "6:00 PM"},
                        {"Day", "Thursday", "Time", "6:00 PM"}
                }
        );
        isProgramAdded = programs.add(createProgram("Flexibility Basics", "Beginner", "Flexibility", flexibilityBasicsSchedule));
        System.out.println("Program 'Flexibility Basics' added: " + isProgramAdded);

        // Add programs with no schedules
        programs.add(createProgram("Advanced Fat Burning", "Advanced", "Weight Loss", new ArrayList<>()));
        programs.add(createProgram("Muscle Building Advanced", "Advanced", "Muscle Building", new ArrayList<>()));

        System.out.println("Programs added to the system: " + programs.size());
    }

    private Map<String, String> createProgram(String name, String difficulty, String focusArea, List<Map<String, String>> schedule) {
        Map<String, String> program = new HashMap<>();
        program.put("Program Name", name);
        program.put("Difficulty Level", difficulty);
        program.put("Focus Area", focusArea);
        program.put("Schedule", schedule.toString());
        return program;
    }

    private List<Map<String, String>> createSchedule(String[][] scheduleData) {
        List<Map<String, String>> schedule = new ArrayList<>();
        for (String[] entry : scheduleData) {
            Map<String, String> daySchedule = new HashMap<>();
            for (int i = 0; i < entry.length; i += 2) {
                daySchedule.put(entry[i], entry[i + 1]);
            }
            schedule.add(daySchedule);
        }
        return schedule;
    }

    @When("the user selects the difficulty level filter {string}")
    public void user_selects_difficulty_level_filter(String difficultyLevel) {
        if (!isLoggedIn) {
            throw new IllegalStateException("User must be logged in to filter programs.");
        }

        if (difficultyLevel == null || difficultyLevel.trim().isEmpty()) {
            throw new IllegalArgumentException("Difficulty level filter cannot be null or empty.");
        }

        enrolledPrograms = filterProgramsBy1("Difficulty Level", difficultyLevel);

        if (enrolledPrograms.isEmpty()) {
            System.out.println("No programs found for the difficulty level: " + difficultyLevel);
        } else {
            System.out.println("Programs found for difficulty level '" + difficultyLevel + "': " + enrolledPrograms.size());
        }
    }


    @When("the user selects the focus area filter {string}")
    public void user_selects_focus_area_filter(String focusArea) {
        if (!isLoggedIn) {
            throw new IllegalStateException("User must be logged in to filter programs.");
        }

        if (focusArea == null || focusArea.trim().isEmpty()) {
            throw new IllegalArgumentException("Focus area filter cannot be null or empty.");
        }

        enrolledPrograms = filterProgramsBy1("Focus Area", focusArea);

        if (enrolledPrograms.isEmpty()) {
            System.out.println("No programs found for the focus area: " + focusArea);
        } else {
            System.out.println("Programs found for focus area '" + focusArea + "': " + enrolledPrograms.size());
        }
    }


    @When("the user selects the difficulty level filter {string} and the focus area filter {string}")
    public void user_selects_multiple_filters(String difficultyLevel, String focusArea) {
        if (!isLoggedIn) {
            throw new IllegalStateException("User must be logged in to filter programs.");
        }

        if ((difficultyLevel == null || difficultyLevel.trim().isEmpty()) ||
            (focusArea == null || focusArea.trim().isEmpty())) {
            throw new IllegalArgumentException("Filters cannot be null or empty.");
        }

        enrolledPrograms = filterProgramsByMultipleFilters1(difficultyLevel, focusArea);

        if (enrolledPrograms.isEmpty()) {
            System.out.println("No programs found for the criteria: Difficulty Level = " + difficultyLevel + ", Focus Area = " + focusArea);
        } else {
            System.out.println("Programs found for criteria: Difficulty Level = '" + difficultyLevel + "', Focus Area = '" + focusArea + "'. Count: " + enrolledPrograms.size());
        }
    }
    private List<Map<String, String>> filterProgramsBy1(String key, String value) {
        List<Map<String, String>> filtered = new ArrayList<>();
        for (Map<String, String> program : programs) {
            if (program.get(key).equalsIgnoreCase(value)) {
                filtered.add(program);
            }
        }
        return filtered;
    }
    private List<Map<String, String>> filterProgramsByMultipleFilters1(String difficulty, String focusArea) {
        List<Map<String, String>> filtered = new ArrayList<>();
        for (Map<String, String> program : programs) {
            if (program.get("Difficulty Level").equalsIgnoreCase(difficulty) &&
                program.get("Focus Area").equalsIgnoreCase(focusArea)) {
                filtered.add(program);
            }
        }
        return filtered;
    }


    @When("the user views all programs")
    public void user_views_all_programs() {
        if (!isLoggedIn) {
            throw new IllegalStateException("User must be logged in to view programs.");
        }

        if (programs.isEmpty()) {
            System.out.println("No programs available in the system.");
        } else {
            enrolledPrograms = new ArrayList<>(programs); // Copy the list to ensure isolation
            System.out.println("All programs have been displayed. Count: " + enrolledPrograms.size());
        }
    }


    @Then("the system displays the following programs:")
    public void system_displays_programs(io.cucumber.datatable.DataTable dataTable) {
        List<Map<String, String>> expectedPrograms = dataTable.asMaps(String.class, String.class);

        assertEquals("Program count mismatch", expectedPrograms.size(), enrolledPrograms.size());

        for (int i = 0; i < expectedPrograms.size(); i++) {
            Map<String, String> expectedProgram = expectedPrograms.get(i);
            Map<String, String> actualProgram = enrolledPrograms.get(i);

            assertEquals("Mismatch in program details at index " + i, expectedProgram, actualProgram);
        }

        System.out.println("All expected programs match the displayed programs.");
    }

    @Then("the system displays a message {string}")
    public void system_displays_message(String expectedMessage) {
        if (filterErrorMessage == null || filterErrorMessage.trim().isEmpty()) {
            throw new AssertionError("No error message was generated by the system.");
        }

        assertEquals("Message mismatch", expectedMessage, filterErrorMessage);
        System.out.println("Displayed message matches the expected message: " + filterErrorMessage);
    }


    @When("the user registers for the program {string}")
    public void user_registers_for_program(String programName) {
        if (!isLoggedIn) {
            throw new IllegalStateException("User must be logged in to register for a program.");
        }

        Map<String, String> program = findProgramByName1(programName);

        if (program != null) {
            if (!enrolledPrograms.contains(program)) {
                enrolledPrograms.add(program);
                System.out.println("User successfully registered for the program: " + programName);
            } else {
                System.out.println("User is already enrolled in the program: " + programName);
            }
        } else {
            throw new IllegalArgumentException("Program not found: " + programName);
        }
    }
    private Map<String, String> findProgramByName1(String programName) {
        for (Map<String, String> program : programs) {
            if (program.get("Program Name").equalsIgnoreCase(programName)) {
                return program;
            }
        }
        return null;
    }


    @Then("the system confirms the registration")
    public void system_confirms_registration() {
        if (enrolledPrograms.isEmpty()) {
            throw new AssertionError("No programs registered. Registration failed.");
        }

        System.out.println("Registration confirmed for program: " + enrolledPrograms.get(enrolledPrograms.size() - 1).get("Program Name"));
        assertFalse("No programs registered", enrolledPrograms.isEmpty());
    }


    @Then("the user receives the program schedule:")
    public void user_receives_program_schedule(io.cucumber.datatable.DataTable dataTable) {
        if (enrolledPrograms.isEmpty()) {
            throw new AssertionError("No programs registered. Cannot display schedule.");
        }

        List<Map<String, String>> expectedSchedule = dataTable.asMaps(String.class, String.class);
        String actualSchedule = enrolledPrograms.get(0).get("Schedule");

        assertEquals("Schedule mismatch", expectedSchedule.toString(), actualSchedule);
        System.out.println("Expected schedule matches the actual schedule: " + actualSchedule);
    }


    @When("the user views the program details")
    public void user_views_program_details() {
        if (enrolledPrograms.isEmpty()) {
            throw new IllegalStateException("No programs are available to view.");
        }

        Map<String, String> programDetails = enrolledPrograms.get(0); // Viewing the first enrolled program
        System.out.println("Viewing program details for: " + programDetails.get("Program Name"));
        System.out.println("Details: " + programDetails);
    }


    @When("the user cancels their enrollment")
    public void user_cancels_enrollment() {
        if (enrolledPrograms.isEmpty()) {
            throw new IllegalStateException("No enrollment found to cancel.");
        }

        String canceledProgram = enrolledPrograms.get(0).get("Program Name"); // Canceling the first program
        enrolledPrograms.clear();
        System.out.println("Enrollment canceled for program: " + canceledProgram);
    }


    @Then("the program {string} is removed from the user’s schedule")
    public void program_removed_from_schedule(String programName) {
        // Verify that the user's enrolled programs list is empty
        assertTrue("Program is still enrolled or schedule is not empty", enrolledPrograms.isEmpty());

        // Log the successful removal of the program
        System.out.println("Program successfully removed from the user's schedule: " + programName);
    }


    @When("the user re-enrolls in the program {string}")
    public void user_reenrolls_in_program(String programName) {
        if (!isLoggedIn) {
            throw new IllegalStateException("User must be logged in to re-enroll in a program.");
        }

        Map<String, String> program = findProgramByName(programName);

        if (program != null) {
            if (!enrolledPrograms.contains(program)) {
                enrolledPrograms.add(program);
                System.out.println("Re-enrolled in program: " + programName);
            } else {
                System.out.println("User is already enrolled in the program: " + programName);
            }
        } else {
            throw new IllegalArgumentException("Program not found: " + programName);
        }
    }


    private List<Map<String, String>> filterProgramsBy(String key, String value) {
        List<Map<String, String>> filtered = new ArrayList<>();
        for (Map<String, String> program : programs) {
            if (program.get(key).equalsIgnoreCase(value)) {
                filtered.add(program);
            }
        }
        if (filtered.isEmpty()) {
            filterErrorMessage = "No programs found for the selected criteria";
        }
        return filtered;
    }

    private List<Map<String, String>> filterProgramsByMultipleFilters(String difficulty, String focusArea) {
        List<Map<String, String>> filtered = new ArrayList<>();
        for (Map<String, String> program : programs) {
            if (program.get("Difficulty Level").equalsIgnoreCase(difficulty) &&
                    program.get("Focus Area").equalsIgnoreCase(focusArea)) {
                filtered.add(program);
            }
        }
        if (filtered.isEmpty()) {
            filterErrorMessage = "No programs found for the selected criteria";
        }
        return filtered;
    }

    private Map<String, String> findProgramByName(String programName) {
        for (Map<String, String> program : programs) {
            if (program.get("Program Name").equalsIgnoreCase(programName)) {
                return program;
            }
        }
        return null;
    }
}
