# Admin.feature
Feature: Admin Dashboard Management

  Scenario: Admin logs out from the dashboard
    Given the admin is logged into the system
    When the admin selects the "Logout" option
    Then the system displays "Logging out..."

  Scenario: Admin selects an invalid dashboard option
    Given the admin is on the dashboard menu
    When the admin enters an invalid option "99"
    Then the system displays "Invalid option. Please select a valid option."

  Scenario: Admin approves a pending user
    Given a user "alice" is in pending status
    When the admin approves the user
    Then the system updates "alice" to "accepted"

  Scenario: Admin denies a pending user
    Given a user "charlie" is in pending status
    When the admin denies the user
    Then the system updates "charlie" to "denied"

  Scenario: Admin views all registered users
    Given there are users in the system
    When the admin views all users
    Then the system displays the list of all users