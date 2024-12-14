Feature: View statistics on the most popular programs by enrollment

  Scenario: Display the most popular programs
    Given that the user is an admin
    When the admin requests the program statistics dashboard
    Then the system displays a list of programs sorted by enrollment
    And the following details are shown:
      | Program Name        | Enrollment Count |
      | Fitness Bootcamp    | 150              |
      | Yoga for Beginners  | 120              |
      | Advanced Strength   | 100              |

  Scenario: No programs available for statistics
    Given that the user is an admin
    When the admin requests the program statistics dashboard
    And no programs have been registered
    Then the system displays a message "No programs available to display."

Feature: Generate reports on revenue, attendance, and client progress

  Scenario: Generate a detailed report for a program
    Given that the user is an admin
    And a program named "Fitness Bootcamp" exists
    When the admin generates a report for the program
    Then the system provides a report containing:
      | Metric            | Value          |
      | Revenue           | $15,000        |
      | Attendance Rate   | 90%            |
      | Client Progress   | 80%            |

  Scenario: Generate a summary report for all programs
    Given that the user is an admin
    When the admin generates a summary report for all programs
    Then the system provides a report containing:
      | Program Name       | Revenue   | Attendance Rate | Average Client Progress |
      | Fitness Bootcamp   | $15,000   | 90%             | 80%                     |
      | Yoga for Beginners | $12,000   | 85%             | 75%                     |
      | Advanced Strength  | $10,000   | 88%             | 70%                     |

  Scenario: No data available for generating reports
    Given that the user is an admin
    When the admin generates a report for all programs
    And no data is available
    Then the system displays a message "No data available for reports."
	
	Feature: Track active and completed programs

  Scenario: View active programs
    Given that the user is an admin
    When the admin requests the list of active programs
    Then the system displays the following details:
      | Program Name       | Start Date  | End Date    | Participants |
      | Fitness Bootcamp   | 2024-01-01  | 2024-03-01  | 50           |
      | Yoga for Beginners | 2024-02-01  | 2024-04-01  | 30           |

  Scenario: View completed programs
    Given that the user is an admin
    When the admin requests the list of completed programs
    Then the system displays the following details:
      | Program Name       | Total Enrollments | Revenue   | Feedback Score |
      | Advanced Strength  | 100               | $10,000   | 4.5            |
      | Meditation Basics  | 50                | $5,000    | 4.7            |

  Scenario: No active or completed programs to display
    Given that the user is an admin
    When the admin requests the list of active or completed programs
    And no programs are available
    Then the system displays a message "No active or completed programs to display."
	