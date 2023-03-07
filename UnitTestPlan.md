Unit Test Plan
==============

Introduction
------------

This document outlines the unit test plan for the Institute Learning Management System. The purpose of this document is to provide an overview of the unit testing strategy, approach, and procedures to be followed during the testing phase of the project. This test plan will use Jacoco and Mockito for code coverage and mocking, respectively. Mockito, as advised has only been used for mock objects. Interactions with database are simulated using a test databse.


Scope
-----

The scope of the unit testing will cover the individual components or units of the application. The main goal is to ensure that each component is functioning as expected and that any issues or defects are identified and fixed before moving on to integration testing.

Testing Approach
----------------

The testing approach will follow a combination of black box and white box testing. Black box testing will be used to verify that each component behaves as expected when provided with specific input and expected output. White box testing will be used to verify the internal workings of the components and ensure that all code paths have been exercised. Jacoco will be used to measure the code coverage of the unit tests.

Test Environment
----------------

The test environment will consist of a dedicated testing environment that is separate from the development and production environments. The testing environment will be set up to mimic the production environment as closely as possible to ensure that the results of the testing are as accurate as possible.

Pass/Fail Criteria:
-------------------

To successfully complete the test phase, the run rate must be 100% unless a valid reason is provided, and the pass rate must be at least 90%. We will continuously monitor and refine our testing approach to ensure we meet or exceed these criteria.


Components Tested:
-------------------

| No. | Component | Description |
| --- | --- | --- |
| 1. | Login Component | Verifies user authentication and ensures that no unauthorized access is granted to the system. |
| 2. | Reset Password Component | Validates that users can reset their passwords securely through OTP sent on email. |
| 3. | Register Course | Confirms that students can register for courses while adhering to business logic constraints. |
| 4. | Drop Course | Validates that students can drop courses and that course records are accurately updated. |
| 5. | View Enrolled Courses | Ensures that students can view their enrolled courses and associated information. |
| 6. | View CGPA | Verifies that the system calculates and displays the CGPA correctly for each student. |
| 7. | View Profile | Ensures that users can view and edit their profile information. |
| 8. | Float Course | Validates that instructors can float courses for the upcoming semester while adhering to business logic constraints. |
| 9. | Delist Course | Verifies that instructors can delist their floated courses, and the course records are accurately updated. |
| 10. | View Student List | Ensures that authorized personnel can view student lists and associated information. |
| 11. | Upload Grades | Validates that faculty members can upload grades and associated information for each enrolled student. |
| 12. | Add Semester Timeline | Verifies that authorized personnel can add new semester timelines for the system. |
| 13. | Generate Transcript | Confirms that authorized personnel can generate their transcripts, and the system provides accurate information. |
| 14. | Update Course Catalog | Ensures that authorized personnel can update the course catalog, and the changes are accurately reflected in the system. |
| 15. | Check Graduation Eligibility | Validates that the system accurately checks each student's graduation eligibility based on the specified criteria. |
| 16. | View Student Grades | Ensures that authorized personnel can view and analyze student grades and associated information. |
| 17. | Change System Date | Verifies that authorized personnel can change the system date, and the changes are accurately reflected in the system. |


Conclusion
----------

This unit test plan outlines the testing approach, strategies, and procedures to be followed during the testing phase of the project. The goal is to ensure that each component is functioning as expected and that any issues or defects are identified and fixed before moving on to integration testing. Jacoco will be used to measure the code coverage of the unit tests, and Mockito will be used to create mock objects for any dependencies that the code being tested relies on.
