Feature: Notifications and Updates
  As an instructor
  I want to notify clients about changes to program schedules
  And announce new programs or special offers
  So that they remain informed and engaged

  Background:
    Given I am logged in as an instructor

  # Scenario: Notify clients about schedule changes
  Scenario: Notify clients about a change to an existing program schedule
    Given I have an existing program titled "My Strength Program"
    And the program currently has group sessions scheduled:
      | Day        | Time     |
      | "Monday"   | "8:00 AM"|
      | "Thursday" | "6:00 PM"|
    When I edit the schedule for "My Strength Program"
    And I set the new session times:
      | Day        | Time      |
      | "Monday"   | "7:00 AM" |
      | "Thursday" | "7:30 PM" |
    And I choose to "Notify All Enrolled Clients"
    Then the system should send a notification to all clients enrolled in "My Strength Program"
    And I should see a confirmation message "All enrolled clients have been notified of the schedule change"

  # Scenario: Announce a new program to all clients
  Scenario: Announce a new fitness program
    Given I have created a new program titled "Bodyweight Basics"
    When I select "Announce Program" for "Bodyweight Basics"
    And I choose "All Clients" as the target audience
    Then the system should send a notification about "Bodyweight Basics" to all clients
    And I should see a confirmation message "Announcement sent: Bodyweight Basics"

  # Scenario: Announce a special offer to all clients
  Scenario: Announce a special offer
    Given I have a special offer named "Summer Fitness Sale"
    When I select "Announce Offer" for "Summer Fitness Sale"
    And I choose "All Clients" as the target audience
    Then a notification about "Summer Fitness Sale" should be sent to all clients
    And I should see a confirmation message "Announcement sent: Summer Fitness Sale"
