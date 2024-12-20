Feature: Notifications and Updates

  Scenario: Notify clients about changes to program schedules
    Given the admin is on the "Program Management" page
    And the program "Yoga for Beginners" has a schedule change
    When the admin updates the schedule to:
      | Date       | Time     | Session Type  | Mode      |
      | 2024-01-20 | 10:00AM  | Group Session | Online    |
    And clicks "Notify Clients"
    Then all enrolled clients are notified
    And the notification message reads:
      """
      The schedule for "Yoga for Beginners" has been updated. 
      New session details: 2024-01-20 at 10:00AM (Online).
      """

  Scenario: Announce new programs to clients
    Given the admin is on the "Announcements" page
    When the admin creates a new announcement titled "New Fitness Program Available!"
    And writes:
      """
      Exciting news! Our new program, "HIIT for Beginners," is now available. 
      Enroll today and kickstart your fitness journey!
      """
    And selects "Send to All Clients"
    And clicks "Publish"
    Then all clients receive a notification
    And the announcement is displayed in the "Updates" section on the client's dashboard.

  Scenario: Announce special offers to clients
    Given the admin is on the "Announcements" page
    When the admin creates a new announcement titled "Limited-Time Offer!"
    And writes:
      """
      Sign up for "Strength Pro" now and get 20% off! Offer valid until 2024-01-31.
      """
    And selects "Send to All Clients"
    And clicks "Publish"
    Then all clients receive a notification
    And the announcement is displayed in the "Offers" section on the client's dashboard.