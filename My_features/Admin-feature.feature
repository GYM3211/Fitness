Feature: Admin manages gym users

  Scenario Outline: Add a gym user successfully
    Given that the user is an admin
    When the user type is <type>
    And the username is <username>
    And the email is <email>
    And the password is <password>
    And the phone number is <phone>
    And the membership type is <membership>
    Then the user is added successfully
    And the user is visible in the user management system

    Examples:
      | type          | username   | email               | password  | phone        | membership  |
      | 'Instructor'  | 'JohnDoe'  | 'john.doe@example.com' | '123456' | '123-456-7890' | 'Premium'   |
      | 'Client'      | 'JaneSmith' | 'jane.smith@example.com' | 'password' | '987-654-3210' | 'Basic'     |

  Scenario Outline: Fail to add gym user due to missing required fields
    Given that the user is an admin
    When the user type is <type>
    And the username is <username>
    And the email is <email>
    And the password is <password>
    And the phone number is <phone>
    And the membership type is <membership>
    Then the user addition fails
    And an error message is displayed indicating the missing fields

    Examples:
      | type          | username   | email               | password  | phone        | membership  |
      | 'Instructor'  | 'JohnDoe'  | ''                  | '123456'  | '123-456-7890' | 'Premium'   |
      | 'Client'      | ''         | 'jane.smith@example.com' | 'password' | '987-654-3210' | 'Basic'     |
