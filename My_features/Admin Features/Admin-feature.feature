Feature: Admin Dashboard Functionality Testing

  # User Management Tests
  Scenario: View all users with a populated list
    Given the user management system is initialized with test data
    When the admin selects "View All Users"
    Then the system displays the list of all users

  Scenario: View all users with an empty list
    Given the user management system is initialized with no user data
    When the admin selects "View All Users"
    Then the system displays "No users found"

  Scenario: Approve a pending user
    Given a user with the status "pending" exists in the system
    When the admin selects "Approve" for that user
    Then the user status is updated to "accepted"

  Scenario: Attempt to approve a non-pending user
    Given a user with the status "accepted" exists in the system
    When the admin selects "Approve" for that user
    Then the system displays "User cannot be approved"

  Scenario: Deny a user with pending status
    Given a user with the status "pending" exists in the system
    When the admin selects "Deny" for that user
    Then the user status is updated to "denied"

  Scenario: Create a valid user
    Given the admin inputs valid user data
    When the admin selects "Create User"
    Then the system creates a new user and stores it correctly

  Scenario: Attempt to create a user with invalid data
    Given the admin inputs incomplete user data
    When the admin selects "Create User"
    Then the system displays "Invalid user data"

  # Content Management Tests
  Scenario: View all articles with a populated list
    Given articles exist in the system
    When the admin selects "View All Articles"
    Then the system displays the list of articles

  Scenario: View all articles with no data
    Given no articles exist in the system
    When the admin selects "View All Articles"
    Then the system displays "No articles available"

  Scenario: Add a new article with valid details
    Given the admin inputs valid article data
    When the admin selects "Add New Article"
    Then the article is successfully added to the system

  Scenario: Edit an existing article
    Given an article exists in the system
    When the admin edits the article details
    Then the system updates the article with the new details

  Scenario: Delete an existing article
    Given an article exists in the system
    When the admin selects "Delete" for that article
    Then the article is removed from the system

  # System Logs Tests
  Scenario: View system logs with data
    Given logs exist in the system
    When the admin selects "View Logs"
    Then the system displays the logs

  Scenario: Search logs with a matching keyword
    Given logs exist in the system
    When the admin searches for "Error"
    Then the system displays logs containing "Error"

  Scenario: Clear system logs
    Given logs exist in the system
    When the admin selects "Clear Logs"
    Then the system removes all logs

  # Logout/Exit Tests
  Scenario: Logout and exit the system
    Given the admin is logged into the dashboard
    When the admin selects "Logout"
    Then the system redirects to the login menu and cleans up resources
