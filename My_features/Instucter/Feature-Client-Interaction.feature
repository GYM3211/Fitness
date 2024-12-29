Feature: Client Interaction
  As an instructor
  I want to communicate with enrolled clients and provide feedback
  So that I can support their progress and maintain engagement

  Background:
    Given I am logged in as an instructor

  # Scenario: Communicate with an enrolled client via messaging
  Scenario: Send a message to an enrolled client
    Given I have at least one enrolled client named "John Doe"
    When I navigate to the "Messages" page
    And I select "John Doe" from the list of enrolled clients
    And I compose a new message with the following details:
      | Subject | "Weekly Check-in"                                 |
      | Body    | "Hi John, how are you feeling about your workouts?" |
    And I click "Send Message"
    Then I should see a confirmation message "Message sent successfully"
    And I should see "Weekly Check-in" in my "Sent Messages" list

  # Scenario: Participate in a discussion forum (if applicable)
  Scenario: Post in a client discussion forum
    Given "John Doe" is enrolled in the "My Custom Program"
    And there is a discussion forum for "My Custom Program"
    When I navigate to the "My Custom Program Discussion Forum"
    And I write a new post with the following details:
      | Title   | "Strength Tips"                               |
      | Message | "Remember to focus on proper form to avoid injuries." |
    And I click "Post"
    Then I should see my post "Strength Tips" in the forum
    And "Remember to focus on proper form to avoid injuries." should be displayed under that post

  # Scenario: Provide feedback or progress reports to a client
  Scenario: Provide feedback on a client's progress
    Given I have an enrolled client named "John Doe"
    When I access "John Doe's" progress report
    And I update the progress notes with "Great improvement in squat form and endurance."
    And I set the metrics:
      | Weight (lbs) | "180"            |
      | Body Fat (%) | "15"             |
      | Strength     | "Bench: 150 lbs" |
    And I click "Save Feedback"
    Then I should see a confirmation message "Feedback saved successfully"
    And the progress report for "John Doe" should reflect:
      | Notes   | "Great improvement in squat form and endurance." |
      | Weight  | "180"                                           |
      | BodyFat | "15"                                            |
      | Strength| "Bench: 150 lbs"                                 |
