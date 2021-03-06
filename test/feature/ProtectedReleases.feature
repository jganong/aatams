Feature: protected releases
    As a tag data provider, I would like to limit visibility of detections of my tags to authorised users only.

# Version: 0.0.4


Scenario Outline: project protection configuration
    Given I am <auth level>
    And I have navigated to the "Create/Edit Project" screen
    Then a check box labelled "Protected" will be <visibility>

    Examples:

        | auth level                  | visibility  |
        |                             |             |
        | sys-admin user              | visible     |
        | authenticated, project user | not visible |


Scenario Outline: protection notification in project screen
    Given I have navigated to the "Edit/Show" project screen
    And the project is <protection level>
    Then a notification "Project is protected" will be <visibility>

    Examples:

        | protection level | visibility  |
        |                  |             |
        | protected        | visible     |
        | non protected    | not visible |


Scenario Outline: protection notification in release screen
    Given I have navigated to the "Edit/Show" release screen
    And the release's project is <protection level>
    And the current date is <relative date> the release's embargo expiry date
    Then a notification "Release is protected" will be <visibility>

    Examples:

        | protection level | relative date | visibility  |
        |                  |               |             |
        | protected        | before        | visible     |
        | protected        | after         | not visible |
        | non protected    | before        | not visible |
        | non protected    | after         | not visible |


#
# Visibility of protected releases and detections, including the following associated entities (list with their
# associated base URL):
#
# animal -              http://aatams-rc.emii.org.au/aatams/animal
# detection -           http://aatams-rc.emii.org.au/aatams/detection
# sensor -              http://aatams-rc.emii.org.au/aatams/sensor
# surgery -             http://aatams-rc.emii.org.au/aatams/surgery
# release -             http://aatams-rc.emii.org.au/aatams/animalRelease
# tag -                 http://aatams-rc.emii.org.au/aatams/tag
#
# Notes:
#
# - "Visibility" in this context means "list" and "show" views, as well as downloads (i.e. CSV and KMZ exports).
# - A release is considered to be protected if it's owning project is protected and it is currently under embargo.
# - If a detection is "sanitised" it means the tag ID and species information are not visible for that detection
#

Scenario Outline: detection and release visibility
    Given there is a protected release with associated detections
    And I am <auth level>
    And the current date is <relative date> the protected release's embargo expiry date
    Then the detections, releases and associated entities shall be <visibility>

    Examples:

        | auth level                      | relative date | visibility  |
        |                                 |               |             |
        | unathenticated                  | after         | visible     |
        | unathenticated                  | before        | not visible |
        | authenticated, non project user | before        | not visible |
        | authenticated, project user     | before        | visible     |


Scenario Outline: filtering
    Given I have navigated to the "Tag Detection List" screen
    And I am <auth level>
    And there is a <protection level> release with species "x" and associated detections
    And I have <filter set> the "species" filter to "x"
    Then the detections should be <visibility> in the list

    Examples:

        | auth level                      | protection level           | filter set | visibility          |
        |                                 |                            |            |                     |
        | unathenticated                  | none                       | not set    | visible             |
        | unathenticated                  | none                       | set        | visible             |
        | unathenticated                  | embargoed                  | not set    | visible (sanitised) |
        | unathenticated                  | embargoed                  | set        | not visible         |
        | unathenticated                  | embargoed + protected      | not set    | not visible         |
        | unathenticated                  | embargoed + protected      | set        | not visible         |
        | unathenticated                  | embargo passed + protected | not set    | visible             |
        | unathenticated                  | embargo passed + protected | set        | visible             |
        |                                 |                            |            |                     |
        | authenticated, non project user | none                       | not set    | visible             |
        | authenticated, non project user | none                       | set        | visible             |
        | authenticated, non project user | embargoed                  | not set    | visible (sanitised) |
        | authenticated, non project user | embargoed                  | set        | not visible         |
        | authenticated, non project user | embargoed + protected      | not set    | not visible         |
        | authenticated, non project user | embargoed + protected      | set        | not visible         |
        | authenticated, non project user | embargo passed + protected | set        | visible             |
        | authenticated, non project user | embargo passed + protected | not set    | visible             |
        |                                 |                            |            |                     |
        | authenticated, project user     | none                       | not set    | visible             |
        | authenticated, project user     | none                       | set        | visible             |
        | authenticated, project user     | embargoed                  | not set    | visible             |
        | authenticated, project user     | embargoed                  | set        | visible             |
        | authenticated, project user     | embargoed + protected      | not set    | visible             |
        | authenticated, project user     | embargoed + protected      | set        | visible             |
        | authenticated, project user     | embargo passed + protected | not set    | visible             |
        | authenticated, project user     | embargo passed + protected | set        | visible             |
        |                                 |                            |            |                     |
        | sys-admin user                  | none                       | not set    | visible             |
        | sys-admin user                  | none                       | set        | visible             |
        | sys-admin user                  | embargoed                  | not set    | visible             |
        | sys-admin user                  | embargoed                  | set        | visible             |
        | sys-admin user                  | embargoed + protected      | not set    | visible             |
        | sys-admin user                  | embargoed + protected      | set        | visible             |
        | sys-admin user                  | embargo passed + protected | not set    | visible             |
        | sys-admin user                  | embargo passed + protected | set        | visible             |
