# Testing Concept Occupi

## Testing Strategy

Where possible, all public methods are tested for correct functionality using unit tests. We use `JUnit Jupiter` and
`Mockito` for this. The test classes are created according to the scheme {Class name}Test and are stored in the /test
directory. All tests should have a meaningful error message and are documented in the code.

Classes that cannot be tested with unit tests because they are responsible for the UI are
tested manually. A test protocol with all relevant cases is created for this purpose.
The final product should work within a `gradle` environment.

## Responsibilities

The implementation of unit test is distributed across team members. The person that wrote a class should **NOT** be responsible for writing the tests to avoid possible testing bias. The test case documentation serves as a guideline for test implementation.

## Timeline

The test and the test protocol are written simultaneously during programming. This allows classes and methods to be
tested as soon as they are implemented. For certain classes, it is necessary to write additional suitable test cases
after implementation in order to increase test coverage. The manual tests are executed during programming if necessary.
After completion of the program, the tests are all executed again. The test protocol is then created. For found issues,
the tester will open GitHub Issues.

## Possible Problems

All methods that use the UI are difficult to check using automated tests. Here, the output must be checked for
correctness by the tester.

## Overview

By clicking on the class name, you can jump to the corresponding test class documentation. The test classes marked with
manual have to be tested manually.

| Class Name                                                        | Method Coverage | Branch Coverage | Line Coverage | Last Updated | Status |
|-------------------------------------------------------------------|-----------------|-----------------|---------------|--------------|--------|
| [Building](testcases/testing-doc-building.md)                     | 0%              | 0%              | 0%            | 17.10.2025   | To do  |
| [Event](testcases/testing-doc-event.md)                           | 0%              | 0%              | 0%            | 17.10.2025   | To do  |
| [Room](testcases/testing-doc-room.md)                             | 0%              | 0%              | 0%            | 17.10.2025   | To do  |
| [RoomEquipment](testcases/testing-doc-roomequipment.md)           | 0%              | 0%              | 0%            | 17.10.2025   | To do  |
| [BuildingController](testcases/testing-doc-buildingcontroller.md) | 0%              | 0%              | 0%            | 17.10.2025   | To do  |
                    
