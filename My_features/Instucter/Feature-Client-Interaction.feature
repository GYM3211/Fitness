Feature: Client Interaction

  Scenario: Admin sends a message to an enrolled client
    Given the admin is on the "Client Management" page
    And the client "John Doe" is enrolled in the "Yoga for Beginners" program
    When the admin selects "Send Message" for the client
    And the admin types the message:
      """
      Hi John, great job on completing Week 1! Keep up the good work.
      """
    And clicks the "Send" button
    Then the message is delivered to the client
    And the client receives a notification: "You have a new message from your instructor."

  Scenario Outline: Admin provides a progress report to a client
    Given the admin is on the "Client Management" page
    And the client "<clientName>" is enrolled in the "<programTitle>" program
    When the admin selects "Provide Progress Report" for the client
    And the admin fills out the progress report:
      | Field             | Value                         |
      | Week Completed    | <weekCompleted>              |
      | Feedback          | <feedback>                   |
      | Suggested Actions | <suggestedActions>           |
    And clicks the "Send Report" button
    Then the client receives the progress report
    And it is displayed in the client's "Reports" section

    Examples:
      | clientName | programTitle          | weekCompleted | feedback                              | suggestedActions              |
      | John Doe   | Yoga for Beginners    | Week 2        | Excellent progress on flexibility.    | Continue stretching daily.    |
      | Jane Smith | Strength Pro          | Week 3        | Good strength improvement so far.     | Add protein-rich meals.       |



  Scenario: Admin interacts with clients via a discussion forum
    Given the admin is on the "Discussion Forum" page for the "Yoga for Beginners" program
    When the admin starts a new discussion topic titled "Tips for Maintaining Flexibility"
    And writes the first post:
      """
      Hi everyone! Share your favorite stretching routines or tips for improving flexibility.
      """
    And clicks the "Post" button
    Then the topic is created successfully
    And all enrolled clients receive a notification: "New discussion topic in Yoga for Beginners."




  Scenario: Client responds to a discussion forum topic
    Given the client "Jane Smith" is on the "Discussion Forum" page for the "Strength Pro" program
    And the topic "How to Stay Consistent with Workouts" exists
    When the client clicks on the topic
    And writes a response:
      """
      I schedule workouts in the mornings before work. It helps me stay consistent.
      """
    And clicks the "Post" button
    Then the response is added to the topic
    And all other participants in the topic are notified: "New response in 'How to Stay Consistent with Workouts.'"

  Scenario: Admin replies to a client’s question in the forum
    Given the admin is on the "Discussion Forum" page for the "Yoga for Beginners" program
    And the topic "Tips for Maintaining Flexibility" exists
    And the client "John Doe" has posted a question:
      """
      What is the best time to stretch during the day?
      """
    When the admin clicks "Reply" to the question
    And writes:
      """
      The best time to stretch is after a workout or in the evening when your muscles are warm.
      """
    And clicks the "Post Reply" button
    Then the reply is added under the question
    And the client "John Doe" is notified: "Your question has been answered."

  Scenario: Admin fails to send a message due to missing content
    Given the admin is on the "Client Management" page
    And the client "John Doe" is enrolled in the "Yoga for Beginners" program
    When the admin selects "Send Message" for the client
    And leaves the message content empty
    And clicks the "Send" button
    Then the message is not sent
    And an error message is displayed: "Message content cannot be empty."

    
    
    
    