
Feature: Subscription Management
  As an admin,
  I want to manage subscription plans for clients and instructors,
  So that I can provide appropriate access to features based on their subscription plans.

  Background:
    Given I am logged in as an admin
    And the subscription management dashboard is open

  Scenario: Add a new subscription plan for clients
    Given there is no subscription plan called "Elite"
    When I create a new subscription plan with the name "Elite"
    And set the price to "$50 per month"
    And set the features to "Access to all programs, 1-on-1 coaching, Exclusive content"
    Then the subscription plan "Elite" should be saved successfully
    And it should be visible in the list of client subscription plans

  Scenario: Update an existing subscription plan for instructors
    Given there is a subscription plan called "Basic" for instructors
    When I update the "Basic" subscription plan
    And change the price to "$30 per month"
    And add the feature "Access to advanced analytics"
    Then the changes should be saved successfully
    And the updated plan should reflect the new price and features

  Scenario: Deactivate a subscription plan
    Given there is a subscription plan called "Premium"
    When I deactivate the subscription plan "Premium"
    Then the plan should be marked as inactive
    And it should not be available for new subscriptions

  Scenario: View details of a subscription plan
    Given there is a subscription plan called "Premium"
    When I view the details of the "Premium" subscription plan
    Then I should see the name, price, and list of features for the plan

  Scenario: Assign a subscription plan to a client
    Given there is a client named "John Doe" without an active subscription
    And there is a subscription plan called "Basic"
    When I assign the "Basic" subscription plan to "John Doe"
    Then the client "John Doe" should have the "Basic" subscription plan active

  Scenario: Assign a subscription plan to an instructor
    Given there is an instructor named "Jane Smith" without an active subscription
    And there is a subscription plan called "Premium"
    When I assign the "Premium" subscription plan to "Jane Smith"
    Then the instructor "Jane Smith" should have the "Premium" subscription plan active
