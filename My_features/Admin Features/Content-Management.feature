Feature: Approve or reject wellness articles, tips, or recipes shared by instructors

  Scenario: Approve a wellness article
    Given that the user is an admin
    And there are pending articles submitted by instructors
    When the admin selects an article with ID <articleID>
    And the admin approves the article
    Then the article with ID <articleID> is published successfully
    And the instructor is notified of the approval

    Examples:
      | articleID |
      | 101       |
      | 102       |

  Scenario: Reject a wellness article
    Given that the user is an admin
    And there are pending articles submitted by instructors
    When the admin selects an article with ID <articleID>
    And the admin rejects the article
    Then the article with ID <articleID> is marked as "Rejected"
    And the instructor is notified of the rejection

    Examples:
      | articleID |
      | 103       |
      | 104       |

  Scenario: Fail to approve or reject due to invalid article ID
    Given that the user is an admin
    And there are pending articles submitted by instructors
    When the admin selects an article with ID <articleID>
    And the article ID is invalid
    Then the system displays a message "Article not found"

    Examples:
      | articleID |
      | 999       |
      | 888       |

     Feature: Approve articles or tips shared on health and wellness

  Scenario: Approve a health and wellness tip
    Given that the user is an admin
    And there are pending tips submitted by users
    When the admin selects a tip with ID <tipID>
    And the admin approves the tip
    Then the tip with ID <tipID> is published successfully
    And the user is notified of the approval

    Examples:
      | tipID |
      | 201   |
      | 202   |

  Scenario: Reject a health and wellness tip
    Given that the user is an admin
    And there are pending tips submitted by users
    When the admin selects a tip with ID <tipID>
    And the admin rejects the tip
    Then the tip with ID <tipID> is marked as "Rejected"
    And the user is notified of the rejection

    Examples:
      | tipID |
      | 203   |
      | 204   |

  Scenario: Fail to approve or reject a tip due to invalid tip ID
    Given that the user is an admin
    And there are pending tips submitted by users
    When the admin selects a tip with ID <tipID>
    And the tip ID is invalid
    Then the system displays a message "Tip not found"

    Examples:
      | tipID |
      | 999   |
      | 888   |
     
     Feature: Handle user feedback and complaints

  Scenario: Resolve a user complaint
    Given that the user is an admin
    And there are pending complaints submitted by users
    When the admin selects a complaint with ID <complaintID>
    And the admin resolves the complaint
    Then the complaint with ID <complaintID> is marked as "Resolved"
    And the user is notified of the resolution

    Examples:
      | complaintID |
      | 301         |
      | 302         |

  Scenario: Fail to resolve a complaint due to invalid complaint ID
    Given that the user is an admin
    And there are pending complaints submitted by users
    When the admin selects a complaint with ID <complaintID>
    And the complaint ID is invalid
    Then the system displays a message "Complaint not found"

    Examples:
      | complaintID |
      | 999         |
      | 888         |

  Scenario: Mark a complaint as "Needs Further Review"
    Given that the user is an admin
    And there are pending complaints submitted by users
    When the admin selects a complaint with ID <complaintID>
    And the admin marks it as "Needs Further Review"
    Then the complaint with ID <complaintID> is updated to "Needs Further Review"
    And the user is notified that their complaint is under review

    Examples:
      | complaintID |
      | 303         |
      | 304         |
      