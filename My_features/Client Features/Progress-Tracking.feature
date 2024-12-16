Feature: Progress Tracking

 Scenario : Track personal fitness milestones
  Scenario: Add a new fitness milestone
    Given that the user is logged in
    And the user has access to the progress tracking feature
    When the user enters the following milestone details:
      | Date       | Weight | BMI   | Attendance |
      | 2024-12-16 | 78     | 24.5  | 15         |
    Then the milestone is saved successfully
    And the system displays the updated progress chart

  Scenario : View progress milestones over time
  Scenario: View fitness milestones history
    Given that the user is logged in
    And the user has recorded fitness milestones
    When the user views their milestones history
    Then the system displays the following data:
      | Date       | Weight | BMI   | Attendance |
      | 2024-12-01 | 80     | 25.0  | 10         |
      | 2024-12-08 | 79     | 24.7  | 12         |
      | 2024-12-16 | 78     | 24.5  | 15         |

 Scenario : Earn achievements for completing programs
  Scenario: Earn a badge for completing a program
    Given that the user is logged in
    And the user has completed the program "Weight Loss Challenge"
    When the user views their achievements
    Then the system displays the following badge:
      | Badge Name               | Description                 |
      | Weight Loss Champion     | "Completed Weight Loss Challenge" |

 Scenario : Earn a badge for attendance milestones
  Scenario: Earn a badge for attendance milestones
    Given that the user is logged in
    And the user has attended 10 sessions
    When the system checks attendance milestones
    Then the system awards the following badge:
      | Badge Name           | Description          |
      | Attendance Star      | "Attended 10 sessions" |

 Scenario : View all earned badges and achievements
  Scenario: View all earned badges and achievements
    Given that the user is logged in
    And the user has earned badges and achievements
    When the user views their achievements section
    Then the system displays the following achievements:
      | Badge Name               | Description                     |
      | Weight Loss Champion     | "Completed Weight Loss Challenge" |
      | Attendance Star          | "Attended 10 sessions"          |

 Scenario : Fail to add fitness milestone due to missing data
  Scenario: Fail to add fitness milestone due to missing details
    Given that the user is logged in
    And the user has access to the progress tracking feature
    When the user tries to add the following milestone:
      | Date       | Weight | BMI | Attendance |
      | 2024-12-16 |        |     | 15         |
    Then the system displays an error message "Weight and BMI are required"
