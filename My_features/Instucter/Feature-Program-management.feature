Feature: Program management

  Scenario Outline: Create a new fitness program
    Given the admin is on the "Create Program" page
    When the admin enters the following details:
      | Field            | Value                 |
      | Program Title    | <title>              |
      | Duration         | <duration>           |
      | Difficulty Level | <difficulty>         |
      | Goals            | <goals>              |
      | Price            | <price>              |
    And the admin uploads media:
      | Type    | File Name        |
      | Video   | <videoFile>      |
      | Image   | <imageFile>      |
      | Document| <documentFile>   |
    And the admin sets the schedule:
      | Session Type | Date       | Time    | Mode       |
      | <sessionType>| <date>     | <time>  | <mode>     |
    And the admin clicks the "Save" button
    Then the program is created successfully
    And it is visible on the program listing page

    Examples:
      | title             | duration | difficulty | goals              | price  | sessionType    | date       | time    | mode        |
      | Yoga for Beginners | 4 weeks  | Beginner   | Flexibility       | 50    |  Group Session  | 2024-01-15 | 10:00AM | Online      |
      | Strength Pro       | 8 weeks  | Advanced   | Muscle Building   | 100    | Group Session  | 2024-02-01 | 6:00PM  | In-person   |

  Scenario Outline: Update an existing fitness program
    Given the admin is on the "Program Management" page
    And the program titled "<title>" exists
    When the admin updates the following details:
      | Field            | New Value            |
      | Duration         | <newDuration>        |
      | Price            | <newPrice>           |
    
    And the admin clicks the "Update" button
    Then the program is updated successfully
    And the changes are reflected in the program details

    Examples:
      | title             | newDuration | newPrice | 
      | Yoga for Beginners | 6 weeks     | 60       | 
      | Strength Pro       | 10 weeks    | 120      |

  Scenario: Delete a fitness program
    Given the admin is on the "Program Management" page
    And the program titled "Strength Pro" exists
    When the admin clicks the "Delete" button next to the program
    And confirms the deletion
    Then the program is removed from the program listing
    And a success message is displayed: "Program deleted successfully"

  Scenario Outline: Set group session schedules for a program
    Given the admin is on the "Edit Program" page for the program titled "<title>"
    When the admin adds the following group session schedules:
      | Session Type  | Date       | Time     | Mode      |
      | <sessionType> | <date>     | <time>   | <mode>    |
    And the admin clicks "Save"
    Then the session schedule is saved successfully
    And the updated schedule is displayed in the program details

    Examples:
      | title             | sessionType   | date       | time     | mode       |
      | Yoga for Beginners | Group Session | 2024-03-01 | 8:00 AM  | Online     |
      | Strength Pro       | Group Session | 2024-03-15 | 5:00 PM  | In-person  |

  Scenario: Fail to create a program due to missing required fields
    Given the admin is on the "Create Program" page
    When the admin leaves the "Program Title" field empty
    And clicks the "Save" button
    Then the program is not created
    And an error message is displayed: "Program Title is required."
