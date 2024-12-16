Feature: Feedback and Reviews

  Scenario : Rate and review a completed program
  Scenario: Submit a rating and review for a completed program
    Given that the user is logged in
    And the user has completed the program "Weight Loss Program"
    When the user submits the following feedback:
      | Rating | Review                            |
      | 4      | "Effective program, but sessions were too long." |
    Then the feedback is saved successfully
    And the review is visible under the program's details

  Scenario : View all feedback for a program
  Scenario: View feedback for a specific program
    Given that the user is logged in
    And the user views the program "Yoga for Beginners"
    When the user checks the feedback section
    Then the system displays the following feedback:
      | User        | Rating | Review                     |
      | John Doe    | 5      | "Excellent program for flexibility." |
      | Jane Smith  | 4      | "Great, but needs more stretching exercises." |

 Scenario : Fail to submit feedback due to missing fields
  Scenario: Fail to submit feedback due to missing details
    Given that the user is logged in
    And the user has completed the program "Weight Loss Program"
    When the user tries to submit the following feedback:
      | Rating | Review |
      |        | "Good program" |
    Then the feedback submission fails
    And the system displays an error message "Rating is required"

  Scenario : Submit improvement suggestions to instructors
  Scenario: Submit a suggestion for a completed program
    Given that the user is logged in
    And the user has completed the program "Advanced Strength Training"
    When the user submits the following suggestion:
      | Suggestion                         |
      | "Add more lower body exercises to the second session." |
    Then the suggestion is sent to the instructor successfully
    And the user receives a confirmation message "Your suggestion has been sent."

  Scenario : View suggestions sent by a user
  Scenario: View previously submitted suggestions
    Given that the user is logged in
    And the user has submitted suggestions for programs
    When the user views their suggestion history
    Then the system displays the following suggestions:
      | Program Name               | Suggestion                          |
      | Advanced Strength Training | "Add more lower body exercises."    |
      | Yoga for Beginners         | "Include relaxation techniques."    |

 Scenario : Instructor reviews suggestions
  Scenario: Instructor reviews submitted suggestions
    Given that the instructor is logged in
    And there are suggestions submitted for their programs
    When the instructor views the suggestions for "Advanced Strength Training"
    Then the system displays the following suggestions:
      | User        | Suggestion                          |
      | John Doe    | "Add more lower body exercises."    |
      | Jane Smith  | "Include rest breaks in the sessions." |
