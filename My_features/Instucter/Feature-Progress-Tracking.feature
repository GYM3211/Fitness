Feature: Progress Tracking
  As an instructor
  I want to monitor client progress and attendance
  So that I can provide motivational reminders or recommendations when needed

  Background:
    Given I am logged in as an instructor

  # Scenario: View client progress and completion rates
  Scenario: View progress for an enrolled client
    Given I have a client named "Jane Doe" enrolled in "Bodyweight Basics"
    And "Jane Doe" has attended 3 out of 5 scheduled sessions
    When I navigate to the "Progress Tracking" page
    And I select "Jane Doe" under "Bodyweight Basics"
    Then I should see:
      | Sessions Attended | "3"      |
      | Sessions Total    | "5"      |
      | Completion Rate   | "60%"    |
    And I should see a chart or summary that reflects these progress details

  # Scenario: Monitor attendance for a specific session
  Scenario: Track attendance for a given session
    Given "Bodyweight Basics" has a session scheduled for "2024-12-30 10:00 AM"
    And the following clients are enrolled:
      | Name       |
      | "Jane Doe" |
      | "John Smith" |
    When the session "2024-12-30 10:00 AM" occurs
    And I mark attendance in the system:
      | Name       | Attended? |
      | "Jane Doe" | "Yes"     |
      | "John Smith" | "No"     |
    Then the attendance record for "2024-12-30 10:00 AM" should show:
      | "Jane Doe"   | "Present" |
      | "John Smith" | "Absent"  |

  # Scenario: Send motivational reminders to a client
  Scenario: Send motivational reminders or recommendations
    Given a client named "John Smith" is enrolled in "Bodyweight Basics"
    And "John Smith" has a completion rate of "40%"
    When I choose to send a motivational reminder to "John Smith"
    And I enter the message "Keep pushing, John! You’re almost halfway there!"
    And I click "Send Reminder"
    Then a message should be sent to "John Smith"
    And I should see a confirmation message "Motivational reminder sent to John Smith"

  # Scenario: Recommend activities or adjustments based on progress
  Scenario: Provide recommendations to improve client progress
    Given a client named "Jane Doe" is enrolled in "Bodyweight Basics"
    And "Jane Doe" has completed "2" out of "5" modules
    When I open "Jane Doe's" progress details
    And I select "Provide Recommendations"
    And I enter "Focus on core exercises next week to build foundational strength."
    And I click "Send Recommendations"
    Then I should see a confirmation "Recommendations sent to Jane Doe"
    And "Jane Doe" should receive a notification with the recommendation
