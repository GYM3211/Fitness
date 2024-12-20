Feature: Progress Tracking

  Scenario: Admin monitors client progress
    Given the admin is on the "Client Progress" page
    And the client "John Doe" is enrolled in the "Yoga for Beginners" program
    When the admin views the progress report for the client
    Then the following details are displayed:
      | Metric             | Value        |
      | Completion Rate    | 75%          |
      | Attendance         | 8/10 sessions |
      | Goals Achieved     | Increased flexibility |

  Scenario: Admin sends a motivational reminder to a client
    Given the admin is on the "Client Management" page
    And the client "Jane Smith" is enrolled in the "Strength Pro" program
    When the admin selects "Send Motivational Reminder" for the client
    And writes the message:
      """
      Keep up the great work, Jane! You're almost halfway through the program—stay consistent!
      """
    And clicks "Send"
    Then the message is delivered to the client
    And the client receives a notification: "You have a new motivational message from your coach."

  Scenario Outline: Admin sends personalized recommendations to a client
    Given the admin is on the "Client Management" page
    And the client "<clientName>" is enrolled in the "<programTitle>" program
    When the admin selects "Send Recommendation" for the client
    And writes:
      """
      <recommendation>
      """
    And clicks "Send"
    Then the message is delivered to the client
    And the client receives a notification: "You have a new recommendation from your coach."

    Examples:
      | clientName | programTitle        | recommendation                            |
      | John Doe   | Yoga for Beginners  | Try adding 10 minutes of morning yoga.   |
      | Jane Smith | Strength Pro        | Consider increasing weights gradually.   |
