Feature: Program Exploration and Enrollment

  Scenario: Browse programs using difficulty level filter
    Given that the user is logged in
    And there are available programs in the system
    When the user selects the difficulty level filter "Beginner"
    Then the system displays the following programs:
      | Program Name            | Difficulty Level |
      | Weight Loss for Beginners | Beginner        |
      | Flexibility Basics       | Beginner        |

  Scenario: Browse programs using focus area filter
    Given that the user is logged in
    And there are available programs in the system
    When the user selects the focus area filter "Weight Loss"
    Then the system displays the following programs:
      | Program Name              | Focus Area     |
      | Weight Loss for Beginners | Weight Loss    |
      | Advanced Fat Burning      | Weight Loss    |

  Scenario: Browse programs using multiple filters
    Given that the user is logged in
    And there are available programs in the system
    When the user selects the difficulty level filter "Beginner"
    And the user selects the focus area filter "Weight Loss"
    Then the system displays the following programs:
      | Program Name              | Difficulty Level | Focus Area  |
      | Weight Loss for Beginners | Beginner        | Weight Loss |

  Scenario: View all available programs
    Given that the user is logged in
    And there are available programs in the system
    When the user views all programs
    Then the system displays the following programs:
      | Program Name              | Difficulty Level | Focus Area     |
      | Weight Loss for Beginners | Beginner        | Weight Loss     |
      | Muscle Building Advanced  | Advanced        | Muscle Building |

  Scenario: No programs available for selected filters
    Given that the user is logged in
    And there are available programs in the system
    When the user selects the focus area filter "Keto Training"
    Then the system displays a message "No programs found for the selected criteria"

  Scenario: Register for a program
    Given that the user is logged in
    And a program named "Weight Loss for Beginners" is available
    When the user registers for the program
    Then the system confirms the registration
    And the user receives the program schedule:
      | Day       | Time      |
      | Monday    | 7:00 PM  |
      | Wednesday | 7:00 PM  |

  Scenario: View program schedule after registration
    Given that the user is logged in
    And the user is registered in the program "Flexibility Basics"
    When the user views the program details
    Then the system displays the schedule:
      | Day       | Time      |
      | Tuesday   | 6:00 PM  |
      | Thursday  | 6:00 PM  |

  Scenario: Cancel enrollment in a program
    Given that the user is logged in
    And the user is enrolled in the program "Flexibility Basics"
    When the user cancels their enrollment
    Then the system confirms the cancellation
    And the program "Flexibility Basics" is removed from the user’s schedule

  Scenario: Re-enroll in a previously canceled program
    Given that the user is logged in
    And the user has previously canceled enrollment in "Weight Loss for Beginners"
    When the user re-enrolls in the program
    Then the system confirms the registration
    And the program schedule is added back to the user’s schedule
