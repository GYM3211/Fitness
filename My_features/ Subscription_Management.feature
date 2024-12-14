Feature: Manage subscription plans for clients and instructors

  Scenario: Add a new subscription plan
    Given that the user is an admin
    When the admin creates a new subscription plan with the following details:
      | Name       | Price | Features             |
      | Basic      | $10   | Access to 5 programs |
    Then the subscription plan "Basic" is added successfully
    And the plan is visible in the subscription list

  Scenario: Update an existing subscription plan
    Given that the user is an admin
    And a subscription plan named "Premium" exists
    When the admin updates the subscription plan with the following details:
      | Name       | Price | Features                         |
      | Premium    | $50   | Access to all programs, VIP support |
    Then the subscription plan "Premium" is updated successfully
    And the updated plan is visible in the subscription list

  Scenario: Delete a subscription plan
    Given that the user is an admin
    And a subscription plan named "Standard" exists
    When the admin deletes the subscription plan "Standard"
    Then the subscription plan "Standard" is removed from the subscription list

  Scenario: Assign a subscription plan to a user
    Given that the user is an admin
    And a user with ID 101 exists
    When the admin assigns the subscription plan "Premium" to the user with ID 101
    Then the user with ID 101 is subscribed to "Premium"
    And the user's subscription status is "Active"

  Scenario: View subscription details for a user
    Given that the user is an admin
    And a user with ID 102 has an active subscription
    When the admin views the subscription details for the user with ID 102
    Then the system displays the following details:
      | Plan Name  | Status  | Expiry Date |
      | Basic      | Active  | 2024-12-31  |

  Scenario: Fail to update a subscription plan due to missing details
    Given that the user is an admin
    When the admin tries to update a subscription plan with the following details:
      | Name   | Price | Features |
      |        | $20   |          |
    Then the update fails
    And the system displays an error message "Missing required fields for updating the subscription plan"
