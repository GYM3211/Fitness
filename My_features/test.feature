
  Scenario: Admin monitors client progress
    Given the admin is on the "Client Progress" page
    And the client "John Doe" is enrolled in the "Yoga for Beginners" program
    When the admin views the progress report for the client
    Then the following details are displayed:
      | Metric             | Value        |
      | Completion Rate    | 75%          |
      | Attendance         | 8/10 sessions |
      | Goals Achieved     | Increased flexibility |

  