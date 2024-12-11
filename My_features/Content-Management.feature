
@tag
Feature: Content Management
  As an admin,
  I want to manage wellness articles, tips, and recipes,
  So that I can ensure high-quality content and address user feedback.
@tag1
  Background:
    Given I am logged in as an admin
    And the content management dashboard is open
@tag2
  Scenario: Approve a wellness article shared by an instructor
    Given there is a pending wellness article submitted by an instructor
    When I review the article
    And I approve the article
    Then the article should be marked as approved
    And it should be visible to users
@tag3
  Scenario: Reject a wellness recipe shared by an instructor
    Given there is a pending wellness recipe submitted by an instructor
    When I review the recipe
    And I reject the recipe
    Then the recipe should be marked as rejected
    And a notification should be sent to the instructor explaining the rejection reason
@tag4
  Scenario: Approve an article shared on health and wellness
    Given there is a pending health and wellness article
    When I review the article
    And I approve the article
    Then the article should be marked as approved
    And it should be visible to users
@tag5
  Scenario: Handle user feedback
    Given there is new user feedback in the system
    When I review the feedback
    Then I should see options to mark it as resolved or escalate it
    And I resolve the feedback
    Then the feedback should be marked as resolved in the system
@tag6
  Scenario: Handle user complaints
    Given there is a complaint submitted by a user
    When I review the complaint
    And I escalate the complaint for further action
    Then the complaint should be marked as escalated in the system
