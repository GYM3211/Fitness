Feature: Instructor Program Management
  As an instructor
  I want to manage fitness programs
  So that I can provide structured training content and schedules for my clients

  Background:
    Given I am logged in as an instructor

  # Scenario: Create a new fitness program
  Scenario: Create a new fitness program
    Given I navigate to the "Create Program" page
    When I enter the following program details:
      | Field               | Value                                  |
      | Title               | "Total Body Transformation"            |
      | Duration            | "12 weeks"                             |
      | Difficulty Level    | "Intermediate"                         |
      | Goals               | "Weight loss and strength building"    |
      | Video Tutorials     | "Uploaded 5 tutorial videos"           |
      | Images/Documents    | "Program overview PDF"                 |
      | Price               | "99"                                   |
    And I set the group session schedules:
      | Session Type | Day       | Time       |
      | "Online"     | "Monday"  | "8:00 AM"   |
      | "In-person"  | "Friday"  | "6:00 PM"   |
    And I click "Create Program"
    Then I should see a confirmation message "Program has been created successfully"
    And the newly created program "Total Body Transformation" should be listed on my programs page

  # Scenario: Update an existing fitness program
  Scenario: Update an existing fitness program
    Given I have an existing program titled "Total Body Transformation"
    When I click on the "Edit" option for "Total Body Transformation"
    And I update the following fields:
      | Field               | Value                                  |
      | Duration            | "16 weeks"                             |
      | Difficulty Level    | "Advanced"                             |
      | Goals               | "Advanced strength and endurance"      |
    And I update the group session schedules:
      | Session Type | Day       | Time       |
      | "Online"     | "Wednesday" | "7:00 AM" |
      | "In-person"  | "Saturday"  | "5:00 PM" |
    And I click "Update Program"
    Then I should see a confirmation message "Program updated successfully"
    And the program details should reflect:
      | Duration         | "16 weeks"                          |
      | Difficulty Level | "Advanced"                          |
      | Goals            | "Advanced strength and endurance"   |

  # Scenario: Delete a fitness program
  Scenario: Delete a fitness program
    Given I have multiple existing programs
    And one of them is titled "Total Body Transformation"
    When I click on the "Delete" option for "Total Body Transformation"
    And I confirm the deletion
    Then I should see a confirmation message "Program deleted successfully"
    And "Total Body Transformation" should no longer be listed on my programs page
