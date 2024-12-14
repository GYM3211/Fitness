Feature: Admin manages gym users

  Scenario Outline: Add a gym user successfully
    Given that the user is an admin
    When the user type is <type>
    And the username is <username>
    And the email is <email>
    And the password is <password>
    And the phone number is <phone>
    And the membership type is <membership>
    Then the user is added successfully
    And the user is visible in the user management system

    Examples:
      | type          | username   | email               | password  | phone        | membership  |
      | 'Instructor'  | 'JohnDoe'  | 'john.doe@example.com' | '123456' | '123-456-7890' | 'Premium'   |
      | 'Client'      | 'JaneSmith' | 'jane.smith@example.com' | 'password' | '987-654-3210' | 'Basic'     |

  Scenario Outline: Fail to add gym user due to missing required fields
    Given that the user is an admin
    When the user type is <type>
    And the username is <username>
    And the email is <email>
    And the password is <password>
    And the phone number is <phone>
    And the membership type is <membership>
    Then the user addition fails
    And an error message is displayed indicating the missing fields

    Examples:
      | type          | username   | email               | password  | phone        | membership  |
      | 'Instructor'  | 'JohnDoe'  | ''                  | '123456'  | '123-456-7890' | 'Premium'   |
      | 'Client'      | ''         | 'jane.smith@example.com' | 'password' | '987-654-3210' | 'Basic'     |

      Feature: Update Gym User

  Scenario Outline: Fail to update gym user due to left blanks
    Given that the user is an admin
    When the user ID is <userID>
    And the user type is <type>
    And the username is <username>
    And the email is <email>
    And the password is <password>
    And the phone number is <phone>
    And the membership type is <membership>
    Then the gym user update fails
    And an error message is displayed indicating the missing fields

    Examples:
      | userID | type        | username | email              | password | phone         | membership |
      | '3'    | ''          | ''       | 'john.doe@example.com' | ''      | '123-456-7890' | 'Premium'  |
      | '4'    | 'Client'    | 'John'   | ''                 | '123456' | '987-654-3210' | 'Basic'    |

  Scenario Outline: Update gym user successfully
    Given that the user is an admin
    When the user ID is <userID>
    And the user type is <type>
    And the username is <username>
    And the email is <email>
    And the password is <password>
    And the phone number is <phone>
    And the membership type is <membership>
    Then the gym user is updated successfully
    And the updated information is visible in the user management system

    Examples:
      | userID | type        | username   | email               | password | phone         | membership |
      | '1'    | 'Instructor'| 'JohnDoe'  | 'john.doe@example.com' | '123456' | '123-456-7890' | 'Premium'  |
      | '2'    | 'Client'    | 'JaneSmith'| 'jane.smith@example.com' | 'password' | '987-654-3210' | 'Basic'    |

  Scenario Outline: Update gym user by username
    Given that the user is an admin
    When the current username is <oldUsername>
    And the new username is <username>
    And the email is <email>
    And the password is <password>
    And the phone number is <phone>
    And the membership type is <membership>
    Then the gym user is updated successfully
    And the updated username is reflected in the user management system

    Examples:
      | oldUsername | username   | email               | password  | phone         | membership  |
      | 'JohnDoe'   | 'JohnDoe2' | 'john.doe2@example.com' | 'newpass' | '123-456-7890' | 'Premium'   |
      | 'JaneSmith' | 'JaneNew'  | 'jane.new@example.com' | 'pass123' | '987-654-3210' | 'Basic'     |
      
      Feature: Deactivate accounts for instructors and clients

  Scenario Outline: Successfully deactivate an account
    Given that the user is an admin
    When the admin selects the user with ID <userID>
    And the account type is <accountType>
    And the admin confirms the deactivation
    Then the account with ID <userID> is deactivated successfully
    And the account status is marked as "Deactivated" in the system

    Examples:
      | userID | accountType  |
      | 101    | Instructor   |
      | 202    | Client       |

  Scenario Outline: Fail to deactivate an account due to invalid ID
    Given that the user is an admin
    When the admin selects the user with ID <userID>
    And the account type is <accountType>
    And the admin attempts to deactivate the account
    Then the deactivation fails
    And an error message is displayed stating "User not found"

    Examples:
      | userID | accountType  |
      | 999    | Instructor   |
      | 888    | Client       |

  Scenario Outline: Prevent deactivating already deactivated accounts
    Given that the user is an admin
    When the admin selects the user with ID <userID>
    And the account type is <accountType>
    And the account status is already "Deactivated"
    Then the deactivation fails
    And an error message is displayed stating "Account is already deactivated"

    Examples:
      | userID | accountType  |
      | 103    | Instructor   |
      | 204    | Client       |
      
      
      Feature: Approve new instructor registrations

  Scenario: Successfully approve a pending instructor registration
    Given that the user is an admin
    And there are pending instructor registration requests
    When the admin selects the request with ID <requestID>
    And the admin approves the request
    Then the instructor account with ID <instructorID> is activated
    And the instructor is notified of the approval

    Examples:
      | requestID | instructorID |
      | 201       | 101          |
      | 202       | 102          |

  Scenario: Reject a pending instructor registration
    Given that the user is an admin
    And there are pending instructor registration requests
    When the admin selects the request with ID <requestID>
    And the admin rejects the request
    Then the registration request is marked as "Rejected"
    And the applicant is notified of the rejection

    Examples:
      | requestID |
      | 203       |
      | 204       |

  Scenario: Fail to approve or reject due to invalid request ID
    Given that the user is an admin
    And there are pending instructor registration requests
    When the admin selects the request with ID <requestID>
    And the request ID is invalid
    Then an error message is displayed stating "Request not found"

    Examples:
      | requestID |
      | 999       |
      | 888       |
      
      
      Feature: Monitor user activity and engagement statistics

  Scenario: View activity and engagement statistics for all users
    Given that the user is an admin
    When the admin requests the user activity dashboard
    Then the system displays the following statistics:
      | Metric                  | Value                |
      | Total users             | <totalUsers>         |
      | Active users            | <activeUsers>        |
      | Instructors online      | <instructorsOnline>  |
      | Clients online          | <clientsOnline>      |
      | Average session duration| <averageSessionTime> |
      | Most engaged user       | <mostEngagedUser>    |

    Examples:
      | totalUsers | activeUsers | instructorsOnline | clientsOnline | averageSessionTime | mostEngagedUser |
      | 500        | 320         | 50                | 270           | 35 mins            | JohnDoe         |
      | 600        | 400         | 60                | 340           | 40 mins            | JaneSmith       |

  Scenario: Monitor a specific user's engagement statistics
    Given that the user is an admin
    And the admin searches for the user with ID <userID>
    When the admin requests the user's engagement details
    Then the system displays the following information:
      | Metric                  | Value          |
      | Total logins            | <logins>       |
      | Total sessions          | <sessions>     |
      | Average session duration| <sessionTime>  |
      | Last active date        | <lastActive>   |

    Examples:
      | userID | logins | sessions | sessionTime | lastActive    |
      | 101    | 150    | 30       | 40 mins     | 2024-12-01    |
      | 102    | 200    | 45       | 50 mins     | 2024-12-05    |
      
      
      