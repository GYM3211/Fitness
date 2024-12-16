Feature: Client Account Management

  Scenario : Create a new client profile
  Scenario: Create a new client profile successfully
    Given that the user is not logged in
    When the user registers with the following details:
      | Name         | Email                 | Password   |
      | John Doe     | john.doe@example.com  | password123|
    Then the user account is created successfully
    And the user can log in to their account

  Scenario : Customize profile with personal details
  Scenario: Customize client profile with personal details
    Given that the user is logged in
    And the user has an incomplete profile
    When the user updates their profile with the following details:
      | Age | Gender | Fitness Goals             |
      | 30  | Male   | Lose Weight, Build Muscle |
    Then the profile is updated successfully
    And the updated details are visible in the user's profile

  Scenario : Add dietary preferences or restrictions
  Scenario: Add dietary preferences or restrictions
    Given that the user is logged in 
    And the user has an incomplete profile
    When the user selects the following dietary preferences:
      | Preferences       |
      | Vegetarian        |
      | Gluten-Free       |
    Then the preferences are saved successfully
    And the user receives personalized dietary recommendations

   Scenario : View profile details
  Scenario: View client profile details
    Given that the user is logged in
    And the user has completed their profile
    When the user views their profile
    Then the system displays the following details:
      | Name         | Age | Gender | Fitness Goals             | Preferences       |
      | John Doe     | 30  | Male   | Lose Weight, Build Muscle | Vegetarian        |

  Scenario : Edit existing profile details
  Scenario: Edit existing profile details
    Given that the user is logged in
    And the user has completed their profile
    When the user updates their profile with the following details:
      | Fitness Goals             | Preferences  |
      | Improve Flexibility       | Vegan        |
    Then the profile is updated successfully
    And the updated details are visible in the user's profile

   Scenario : Fail to create a profile due to missing mandatory details
  Scenario: Fail to create a profile due to missing details
    Given that the user is not logged in
    When the user tries to register with the following details:
      | Name         | Email                 | Password   |
      |              | john.doe@example.com  | password123|
    Then the account creation fails
    And the system displays an error message "Name is required"

   Scenario : Fail to update profile due to missing or invalid details
  Scenario: Fail to update profile due to invalid details
    Given that the user is logged in
    And the user has an incomplete profile
    When the user updates their profile with the following details:
      | Age | Fitness Goals             |
      | -5  | Lose Weight, Build Muscle |
    Then the profile update fails
    And the system displays an error message "Invalid age entered"

  Scenario : Delete account
  Scenario: Delete user account
    Given that the user is logged in
    And the user wants to delete their account
    When the user confirms the account deletion
    Then the account is deleted successfully
    And the user is logged out of the system
