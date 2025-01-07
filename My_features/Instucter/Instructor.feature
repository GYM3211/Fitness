# Instructor.feature
Feature: Instructor Dashboard Operations

  Scenario: Instructor views their articles
    Given articles exist for "instructorA"
    When the instructor views their articles
    Then the system lists all articles authored by "instructorA"

  Scenario: Instructor creates a new article
    Given the instructor is logged in
    When the instructor creates an article titled "New Training Tips"
    And provides the content "Tips for effective training."
    Then the system saves the article

  Scenario: Instructor edits a program successfully
    Given a program "99999" exists for "instructorX"
    When the instructor edits the program to "New Title" and "New Description"
    Then the system updates the program details

  Scenario: Instructor deletes a program
    Given a program "55555" exists for "instructorZ"
    When the instructor deletes the program
    Then the system removes the program

  Scenario: Instructor views program subscribers
    Given subscribers exist for "instructorA" programs
    When the instructor views their subscribers
    Then the system lists the subscribers for each program