# Client.feature
Feature: Client Dashboard Interaction

  Scenario: Client views all programs when none are available
    Given the client is logged into the dashboard
    When the client views all programs
    Then the system displays "No programs available at the moment."

  Scenario: Client subscribes to a program successfully
    Given a program "ABC123" exists
    And the client is not already subscribed
    When the client subscribes to "ABC123"
    Then the system displays "You have successfully subscribed to the program!"

  Scenario: Client views their profile
    Given a client profile exists for "johnDoe"
    When the client views their profile
    Then the system displays the client details

  Scenario: Client views articles
    Given articles are available in the system
    When the client selects an article ID "A111"
    Then the system displays the article content

  Scenario: Client changes subscription type
    Given the client "clientX" exists with a free subscription
    When the client changes subscription to "Premium"
    Then the system updates the subscription to "Premium"